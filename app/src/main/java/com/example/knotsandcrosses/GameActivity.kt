package com.example.knotsandcrosses

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.example.knotsandcrosses.databinding.ActivityGameBinding
import com.example.knotsandcrosses.dialogs.ResultDialogListener
import com.example.knotsandcrosses.dialogs.ResultDialog
import com.example.knotsandcrosses.util.isDrawn
import com.example.knotsandcrosses.util.isWon
import com.example.knotsandcrosses.util.copyGameStateWithoutReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameActivity : AppCompatActivity(), ResultDialogListener {

    private lateinit var binding: ActivityGameBinding
    private var noFirstPlayer = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            tvGameId.text = GameManager.gameId
            tvPlayerName.text = GameManager.player1
            tvPlayerName.setTextColor(Color.BLACK)
            tvOpponentName.setTextColor(Color.BLACK)
            if(GameManager.player2 != null){
                tvOpponentName.text = GameManager.player2
            } else {
                disableAllGameButtons()  //Only disable the buttons if we are waiting for the opponent.
            }
        }

        setListenersOnButtons()

        waitForOpponent()
    }

    private fun waitForOpponent(){
        CoroutineScope(IO).launch {
            while(GameManager.waiting){
                if(GameManager.isWaitingForPlayer){
                    Log.d("PollGame", "with foundPlayer")
                    GameManager.pollGame(this@GameActivity::foundPlayer)
                } else {
                    Log.d("PollGame", "with stateChanged")
                    GameManager.pollGame(this@GameActivity::stateChanged)
                }
                delay(1000)
            }
            GameManager.waiting = true
        }
    }

    private fun foundPlayer(){
        binding.tvOpponentName.text = GameManager.player2
        toggleValidGameButtons()
        waitForOpponent()
    }

    private fun stateChanged(){
        if(noFirstPlayer){
            GameManager.isPlayerOne = false
            noFirstPlayer = false
        }
        updateButtonText()  // Update the knots and crosses text on the buttons.
        val player = if(GameManager.isPlayerOne){
            "player2"
        } else {
            "player1"
        }
        if(isWon(player)){
            val dlg = ResultDialog("lost")
            dlg.show(supportFragmentManager,"GameResultDialogFragment")
            return
        }
        if(isDrawn()){
            val dlg = ResultDialog("drawed")
            dlg.show(supportFragmentManager, "GameResultDialogFragment")
            return
        }
        if(GameManager.isPlayerOne){
            binding.tvOpponentName.text = GameManager.player2
        }
        binding.tvPlayerName.setTextColor(Color.GREEN)
        binding.tvOpponentName.setTextColor(Color.BLACK)

        toggleValidGameButtons()
    }

    private fun toggleValidGameButtons(){
        val buttonsToBeEnabled = mutableListOf<Int>()
        var index = 0
        GameManager.state?.forEach{ row ->
            row.forEach {
                if(it == "0"){
                    buttonsToBeEnabled.add(index)
                }
                index += 1
            }
        }

        binding.apply {
            val gameButtons = listOf(zeroZero, zeroOne, zeroTwo, oneZero, oneOne, oneTwo, twoZero, twoOne, twoTwo)
            disableAllGameButtons()
            buttonsToBeEnabled.forEach{
                val button = gameButtons[it]
                button.isEnabled = true  // Enable the buttons for places that has not been taken.
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
            buttonCheatMode.setOnClickListener {
                toggleCheatMode()
            }
            buttonStandardMode.setOnClickListener {
                toggleCheatMode()
            }
            buttonStandardMode.isEnabled = false
        }
    }

    private fun toggleCheatMode() {
        GameManager.cheatMode = !GameManager.cheatMode
        if(GameManager.cheatMode){
            binding.buttonCheatMode.isEnabled = false
            binding.buttonStandardMode.isEnabled = true
        } else {
            binding.buttonCheatMode.isEnabled = true
            binding.buttonStandardMode.isEnabled = false
            if(!GameManager.firstMark){  // If a cheat mark has been used.
                toggleValidGameButtons()
                updateButtonText()
                GameManager.firstMark = true  // Makes it possible to use the cheat mark again.

                GameManager.tempState = copyGameStateWithoutReference()
            }
        }
    }

    private fun clickedButton(index:Int){
        if(noFirstPlayer){
            GameManager.isPlayerOne = true
            noFirstPlayer = false
        }
        var row = -1
        var indx = -1
        for(i in 0..2){
            if(index == 3*i || index == 3*i+1 || index == 3*i+2){
                row = i
                indx = index - (3*i)
                break
            }
        }
        val mark = if(GameManager.isPlayerOne){
            "X"
        } else {
            "O"
        }
        if(GameManager.cheatMode && GameManager.firstMark && !isDrawn() && !isWon("player1")){
            GameManager.tempState = copyGameStateWithoutReference()

            GameManager.tempState[row][indx] = mark

            lateinit var gameButtons: List<Button>
            binding.apply {
                gameButtons = listOf(zeroZero, zeroOne, zeroTwo, oneZero, oneOne, oneTwo, twoZero, twoOne, twoTwo)
            }
            gameButtons[index].isEnabled = false
            gameButtons[index].text = mark
            GameManager.firstMark = false
            return
        }
        if(GameManager.cheatMode){
            GameManager.tempState[row][indx] = mark
        } else {
            GameManager.tempState = copyGameStateWithoutReference()

            GameManager.tempState[row][indx] = mark
        }
        GameManager.firstMark = true
        GameManager.updateGame{  // When the network has received the updated game state.
            updateButtonText()
            disableAllGameButtons()
            binding.tvOpponentName.setTextColor(Color.GREEN)
            binding.tvPlayerName.setTextColor(Color.BLACK)
            val player = if(GameManager.isPlayerOne){
                "player1"
            } else {
                "player2"
            }
            if(isWon(player)){
                val dlg = ResultDialog("won")
                dlg.show(supportFragmentManager,"GameResultDialogFragment")
                GameManager.waiting = false
                return@updateGame
            }
            if(isDrawn()){
                val dlg = ResultDialog("drawed")
                dlg.show(supportFragmentManager, "GameResultDialogFragment")
                GameManager.waiting = false
                return@updateGame
            }
            waitForOpponent()
        }

    }
    
    private fun updateButtonText(){
        var gameButtons: List<Button>
        binding.apply {
            gameButtons =
                listOf(zeroZero, zeroOne, zeroTwo, oneZero, oneOne, oneTwo, twoZero, twoOne, twoTwo)
        }

        var index = 0
        GameManager.state?.forEach { row ->
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

    override fun onDialogWinOrLoose() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}