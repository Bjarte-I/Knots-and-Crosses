package com.example.knotsandcrosses

import android.content.Intent
import android.util.Log
import com.example.knotsandcrosses.api.GameService
import com.example.knotsandcrosses.api.data.Game
import com.example.knotsandcrosses.api.data.GameState
import com.example.knotsandcrosses.util.CountingIdlingResourceSingleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.reflect.KFunction0

object GameManager {
    var player1:String? = null
    var player2:String? = null
    var state:GameState? = null
    var tempState: GameState = mutableListOf(mutableListOf("0", "0", "0"), mutableListOf("0", "0", "0"), mutableListOf("0", "0", "0"))
    var gameId:String? = null
    var waiting = true
    var isPlayerOne = true
    var result = "Not decided yet"
    var cheatMode = false
    var firstMark = true
    var isWaitingForPlayer = false
    private var gotBothPlayersEarlier = false

    //There are many places I could have used StartingGameState, however since I had major
    // problems with them changing to something different, since the reference to the original
    // is hard to remove, I have decided to be safe and use mutableListOf directly.
    val StartingGameState:GameState = mutableListOf(mutableListOf("0", "0", "0"), mutableListOf("0", "0", "0"), mutableListOf("0", "0", "0"))

    fun createGame(player:String){

        CountingIdlingResourceSingleton.increment()

        GameService.createGame(player,mutableListOf(mutableListOf("0", "0", "0"), mutableListOf("0", "0", "0"), mutableListOf("0", "0", "0"))) { game: Game?, err: Int? ->

            if(err != null){
                print(err)
            } else {
                player1 = player
                player2 = null
                state = mutableListOf(mutableListOf("0", "0", "0"), mutableListOf("0", "0", "0"), mutableListOf("0", "0", "0"))
                gameId = game?.gameId
                gotBothPlayersEarlier = false
                result = "Not decided yet"
                cheatMode = false
                isWaitingForPlayer = true
                firstMark = true
                waiting = true

                val intent = Intent(App.context, GameActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                App.context.startActivity(intent)
                CountingIdlingResourceSingleton.decrement()
            }
        }

    }

    fun joinGame(player:String, gameId: String){
        GameService.joinGame(player, gameId){ game: Game?, err: Int? ->
            if(err != null){
                print(err)
            } else {
                player1 = player
                player2 = game?.players?.get(0)
                this@GameManager.gameId = game?.gameId
                state = mutableListOf(mutableListOf("0", "0", "0"), mutableListOf("0", "0", "0"), mutableListOf("0", "0", "0"))
                result = "Not decided yet"
                cheatMode = false
                isWaitingForPlayer = false
                state = StartingGameState
                firstMark = true
                waiting = true

                val intent = Intent(App.context, GameActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                App.context.startActivity(intent)
            }
        }
    }

    fun pollGame(callback: KFunction0<Unit>){
        CoroutineScope(Dispatchers.IO).launch {
            GameService.pollGame(gameId!!) { game: Game?, err: Int? ->
                if (err != null) {
                    print(err)
                } else {
                    if (game?.state != state && game?.state != null && game.state != listOf(null)) {  //state changes when cheating
                        state = game.state
                        waiting = false
                        callback()
                    }
                    if(game?.players?.size == 2 && isWaitingForPlayer){
                        isWaitingForPlayer = false
                        player2 = game.players[1]
                        waiting = false
                        callback()
                    }


                }
            }
        }
    }

    fun updateGame(kFunction0: () -> Unit) {
        GameService.updateGame(gameId!!, tempState) { game: Game?, err: Int? ->
            if(err != null){
                print(err)
            } else {
                Log.d("GameManager/updateGame", game?.state.toString())
                state = game?.state
                kFunction0()
            }
        }
    }
}
