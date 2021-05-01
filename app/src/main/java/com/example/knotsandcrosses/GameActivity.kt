package com.example.knotsandcrosses

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.example.knotsandcrosses.databinding.ActivityGameBinding
import com.example.knotsandcrosses.dialogs.CreateGameDialog
import com.example.knotsandcrosses.dialogs.ResultDialogListener
import com.example.knotsandcrosses.dialogs.WinOrLooseDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameActivity : AppCompatActivity(), ResultDialogListener {

    private lateinit var binding: ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvGameId.text = GameManager.gameId

        if(!GameManager.isPlayerOne){
            binding.tvPlayerName.text = GameManager.player2
            binding.tvOpponentName.text = GameManager.player1
            binding.tvOpponentName.setTextColor(Color.GREEN)
            binding.tvPlayerName.setTextColor(Color.BLACK)
        } else {
            binding.tvPlayerName.text = GameManager.player1
        }
        setListenersOnButtons()

        disableAllGameButtons()

        waitForOpponent()
    }

    private fun waitForOpponent(){
        CoroutineScope(IO).launch {
            while(GameManager.waiting){
                GameManager.pollGame(this@GameActivity::stateChanged)
                delay(5000)
            }
            GameManager.waiting = true
        }
    }

    fun stateChanged(){
        updateButtonText()  // Update the knots and crosses text on the buttons.
        val player = if(GameManager.isPlayerOne){
            "player2"
        } else {
            "player1"
        }
        if(checkForWin(player)){
            Log.d("checkForWin", "You lost!")
            val dlg = WinOrLooseDialog("lost")
            dlg.show(supportFragmentManager,"GameResultDialogFragment")
            return
        }
        if(GameManager.isPlayerOne){
            binding.tvOpponentName.text = GameManager.player2
        }
        binding.tvPlayerName.setTextColor(Color.GREEN)
        binding.tvOpponentName.setTextColor(Color.BLACK)
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
        var row = -1
        var indx = -1
        for(i in 0..2){
            if(index == 3*i || index == 3*i+1 || index == 3*i+2){
                row = i
                indx = index - (3*i)
            }
        }
        val mark = if(GameManager.isPlayerOne){
            "X"
        } else {
            "O"
        }
        GameManager.state!![row][indx] = mark
        updateButtonText()
        GameManager.updateGame()
        disableAllGameButtons()
        binding.tvOpponentName.setTextColor(Color.GREEN)
        binding.tvPlayerName.setTextColor(Color.BLACK)
        val player = if(GameManager.isPlayerOne){
            "player1"
        } else {
            "player2"
        }
        if(checkForWin(player)){
            Log.d("checkForWin", "You won!")
            val dlg = WinOrLooseDialog("won")
            dlg.show(supportFragmentManager,"GameResultDialogFragment")
            /*val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)*/
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

    private fun checkForWin(player: String): Boolean {
        var compareParameter = ""
        if(player == "player1"){
            compareParameter = "X"
        } else if(player == "player2") {
            compareParameter = "O"
        }
        val oneBigRow = mutableListOf<Int>()
        GameManager.state?.forEach { row ->
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

    override fun onDialogWinOrLoose() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}