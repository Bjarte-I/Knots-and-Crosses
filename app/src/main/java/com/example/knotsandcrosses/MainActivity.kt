package com.example.knotsandcrosses

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.knotsandcrosses.databinding.ActivityMainBinding
import com.example.knotsandcrosses.dialogs.CreateGameDialog
import com.example.knotsandcrosses.dialogs.GameDialogListener
import com.example.knotsandcrosses.dialogs.JoinGameDialog

class MainActivity : AppCompatActivity() , GameDialogListener {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startGameButton.setOnClickListener {
            createGame()
        }

        binding.joinGameButton.setOnClickListener {
            joinGame()
        }

    }

    private fun createGame(){
        val dlg = CreateGameDialog()
        dlg.show(supportFragmentManager,"CreateGameDialogFragment")
    }

    override fun onDialogCreateGame(player: String) {
        GameManager.createGame(player)
    }

    private fun joinGame(){
        val dlg = JoinGameDialog()
        dlg.show(supportFragmentManager, "JoinGameDialogFragment")
    }

    override fun onDialogJoinGame(player: String, gameId: String) {
        GameManager.joinGame(player, gameId)
    }
}