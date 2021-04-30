package com.example.knotsandcrosses

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import com.example.knotsandcrosses.api.GameService
import com.example.knotsandcrosses.api.GameServiceCallback
import com.example.knotsandcrosses.api.data.Game
import com.example.knotsandcrosses.databinding.ActivityGameBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var state: Game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        state = intent.getParcelableExtra("EXTRA_STATE")!!

        binding.tvGameId.text = state.gameId
        binding.tvPlayerName.text = state.players[0]

        setListenersOnButtons()

        toggleAllGameButtons()

        waitForOpponent()
    }

    private fun waitForOpponent(){
        CoroutineScope(IO).launch {
            while(state.players.size != 2){
                Log.d("waitForOpponent", "Launching pollgame request")
                GameService.pollGame(state.gameId, this@GameActivity::onPolledGame)
                delay(5000)
            }
        }
    }

    private fun foundOpponent(){
        Log.d("FoundOpponent", "Found!")
        binding.tvOpponentName.text = state.players[1]
        binding.tvPlayerName.setTextColor(Color.GREEN)
        toggleAllGameButtons()
    }


    private fun onPolledGame(newState: Game?, errorCode:Int?){
        Log.d("polledGame", "testing")
        if(newState == null){
            Log.e("polledGame", errorCode.toString())
        } else {
            if(newState != state){
                state = newState
            }
            Log.d("polledGame", state.players.toString())
            if(state.players.size == 2){
                foundOpponent()
            }
        }
    }

    private fun toggleAllGameButtons(){
        binding.apply {
            val gameButtons = listOf(zeroZero, zeroOne, zeroTwo, oneZero, oneOne, oneTwo, twoZero, twoOne, twoTwo)
            for (button in gameButtons){
                button.isEnabled = !button.isEnabled
            }
        }
    }

    private fun setListenersOnButtons(){
        binding.apply {
            val gameButtons = listOf(zeroZero, zeroOne, zeroTwo, oneZero, oneOne, oneTwo, twoZero, twoOne, twoTwo)
            gameButtons.forEachIndexed { index, button ->
                button.setOnClickListener {
                    clickedButton(index)
                }
            }
        }
    }

    private fun clickedButton(index:Int){
        Log.d("clickedButton", index.toString())
        var row = -1
        if(index < 9){
            row = 2
        }
        if(index < 6){
            row = 1
        }
        if(index < 3){
            row = 0
        }
        state.state[row][index] = "X"
        GameService.updateGame(state.gameId, state.state, this::onUpdatedGame)
        toggleAllGameButtons()
        binding.tvOpponentName.setTextColor(Color.GREEN)
        binding.tvPlayerName.setTextColor(Color.WHITE)
    }

    private fun onUpdatedGame(state: Game?, errorCode:Int?){
        Log.d("update", state.toString())
    }
}