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
    var gameId:String? = null
    var waiting = true
    var isPlayerOne = true
    var result = "Not decided yet"
    private var gotBothPlayersEarlier = false

    val StartingGameState:GameState = mutableListOf(mutableListOf("0", "0", "0"), mutableListOf("0", "0", "0"), mutableListOf("0", "0", "0"))

    fun createGame(player:String){

        CountingIdlingResourceSingleton.increment()

        GameService.createGame(player,StartingGameState) { game: Game?, err: Int? ->

            if(err != null){
                print(err)
            } else {
                player1 = player
                state = StartingGameState
                gameId = game?.gameId
                isPlayerOne = true
                gotBothPlayersEarlier = false
                result = "Not decided yet"

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
                player1 = game?.players?.get(0)
                player2 = player
                isPlayerOne = false
                this@GameManager.gameId = game?.gameId
                state = game?.state
                result = "Not decided yet"

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
                    if (game?.state != state || (game?.players?.size == 2 && !gotBothPlayersEarlier && isPlayerOne)) {
                        gotBothPlayersEarlier = true
                        state = game?.state
                        player2 = game?.players?.get(1)
                        waiting = false
                        callback()
                    }
                }
            }
        }
    }

    fun updateGame() {
        GameService.updateGame(gameId!!, state!!) { game: Game?, err: Int? ->
            if(err != null){
                print(err)
            } else {
                Log.d("GameManager/updateGame", state.toString())
            }
        }
    }
}
