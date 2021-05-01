package com.example.knotsandcrosses

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.example.knotsandcrosses.api.GameService
import com.example.knotsandcrosses.api.data.Game
import com.example.knotsandcrosses.databinding.ActivityGameBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var state: Game
    private var isPlayerOne = true
    private var waiting = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        state = intent.getParcelableExtra("EXTRA_STATE")!!
        isPlayerOne = intent.getBooleanExtra("EXTRA_isPlayerOne", true)

        binding.tvGameId.text = state.gameId

        if(!isPlayerOne){
            binding.tvPlayerName.text = state.players[1]
            binding.tvOpponentName.text = state.players[0]
            binding.tvOpponentName.setTextColor(Color.GREEN)
            binding.tvPlayerName.setTextColor(Color.BLACK)
        } else {
            binding.tvPlayerName.text = state.players[0]
        }
        setListenersOnButtons()

        disableAllGameButtons()

        waitForOpponent()
    }

    private fun waitForOpponent(){
        CoroutineScope(IO).launch {
            while(waiting){
                Log.d("waitForOpponent", "Launching pollgame request")
                GameService.pollGame(state.gameId, this@GameActivity::onPolledGame)
                delay(5000)
            }
        }
        waiting = true
    }

    private fun stateChanged(){
        Log.d("StateChanged", "Changed!")
        updateButtonText()  // Update the knots and crosses text on the buttons.
        val player = if(isPlayerOne){
            "player2"
        } else {
            "player1"
        }
        if(checkForWin(player)){
            Log.d("checkForWin", "You lost!")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            return
        }
        if(isPlayerOne){
            binding.tvOpponentName.text = state.players[1]
        }
        binding.tvPlayerName.setTextColor(Color.GREEN)
        binding.tvOpponentName.setTextColor(Color.BLACK)
        var buttonsToBeEnabled = mutableListOf<Int>()
        var index = 0
        state.state.forEach{ row ->
            row.forEach {
                if(it == "0"){
                    buttonsToBeEnabled.add(index)
                }
                index += 1
            }
        }
        toggleValidGameButtons(buttonsToBeEnabled)
    }

    private fun toggleValidGameButtons(toBeEnabled: List<Int>){
        binding.apply {
            val gameButtons = listOf(zeroZero, zeroOne, zeroTwo, oneZero, oneOne, oneTwo, twoZero, twoOne, twoTwo)
            toBeEnabled.forEach{
                val button = gameButtons[it]
                button.isEnabled = true  // Enable the buttons for places that has not been taken.
            }
        }
    }


    private fun onPolledGame(newState: Game?, errorCode:Int?){
        Log.d("polledGame", "testing")
        if(newState == null){
            Log.e("polledGame", errorCode.toString())
        } else {
            if(newState != state){
                state = newState
                waiting = false
                stateChanged()
            }
        }
    }

    private fun disableAllGameButtons(){
        binding.apply {
            val gameButtons = listOf(zeroZero, zeroOne, zeroTwo, oneZero, oneOne, oneTwo, twoZero, twoOne, twoTwo)
            for (button in gameButtons){
                button.isEnabled = false
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
        var indx = -1
        if(index == 0 || index == 1 || index == 2){
            row = 0
            indx = index
        }
        if(index == 3 || index == 4 || index == 5){
            row = 1
            indx = index - 3
        }
        if(index == 6 || index == 7  || index == 8){
            row = 2
            indx = index - 6
        }
        val mark = if(isPlayerOne){
            "X"
        } else {
            "O"
        }
        state.state[row][indx] = mark
        updateButtonText()
        GameService.updateGame(state.gameId, state.state, this::onUpdatedGame)
        disableAllGameButtons()
        binding.tvOpponentName.setTextColor(Color.GREEN)
        binding.tvPlayerName.setTextColor(Color.BLACK)
        val player = if(isPlayerOne){
            "player1"
        } else {
            "player2"
        }
        if(checkForWin(player)){
            Log.d("checkForWin", "You won!")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            return
        }
        waitForOpponent()
    }
    
    private fun updateButtonText(){
        var gameButtons: List<Button>
        binding.apply {
            gameButtons =
                listOf(zeroZero, zeroOne, zeroTwo, oneZero, oneOne, oneTwo, twoZero, twoOne, twoTwo)
        }

        var index = 0
        state.state.forEach { row ->
            row.forEach { 
                if(it != "0"){
                    gameButtons[index].text = it
                } else {
                    gameButtons[index].text = ""
                }
                index += 1
            }
        }
    }

    private fun checkForWin(player: String): Boolean {
        var compareParameter = ""
        if(player == "player1"){
            compareParameter = "X"
        } else if(player == "player2") {
            compareParameter = "O"
        }
        var oneBigRow = mutableListOf<Int>()
        state.state.forEach { row ->
            row.forEach {
                if(it == compareParameter){
                    oneBigRow.add(1)
                } else {
                    oneBigRow.add(0)
                }
            }
        }
        for(i in 0..2){
            if(oneBigRow[3 * i] + oneBigRow[1 + 3 * i] + oneBigRow[2 + 3 * i] == 3){  // Horizontal win
                Log.d("checkForWin", "A player won.")
                return true
            }
            if(oneBigRow[i] + oneBigRow[3 + i] + oneBigRow[6 + i] == 3 ){  // Vertical win
                return true
            }
        }
        for(i in 0..1)
        if(oneBigRow[0 + 2 * i] + oneBigRow[4] + oneBigRow[8 - i * 2] == 3){  // Diagonal win
            return true
        }


        return false
    }

    private fun onUpdatedGame(state: Game?, errorCode:Int?){
        Log.d("update", state.toString())
    }
}