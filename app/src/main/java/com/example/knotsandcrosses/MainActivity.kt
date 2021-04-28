package com.example.knotsandcrosses

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.knotsandcrosses.api.GameService
import com.example.knotsandcrosses.api.GameServiceCallback
import com.example.knotsandcrosses.api.data.Game
import com.example.knotsandcrosses.databinding.ActivityMainBinding
import com.example.knotsandcrosses.dialogs.CreateGameDialog
import com.example.knotsandcrosses.dialogs.GameDialogListener
import com.example.knotsandcrosses.dialogs.GameIdDialog
import com.example.knotsandcrosses.dialogs.JoinGameDialog

class MainActivity : AppCompatActivity() , GameDialogListener {

    val TAG:String = "MainActivity"

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startGameButton.setOnClickListener {
            createNewGame()
        }

        binding.joinGameButton.setOnClickListener {
            joinGame()
        }

    }

    private fun createNewGame(){
        val dlg = CreateGameDialog()
        dlg.show(supportFragmentManager,"CreateGameDialogFragment")
    }

    private fun joinGame(){
        val dlg = JoinGameDialog()
        dlg.show(supportFragmentManager, "JoinGameDialogFragment")
    }

    override fun onDialogCreateGame(player: String) {
        Log.d(TAG,player)
        GameService.createGame(player, GameManager.StartingGameState, this::onCreatedGame)
        GameManager.createGame(player)
    }

    override fun onDialogJoinGame(player: String, gameId: String) {
        Log.d(TAG, "$player $gameId")
    }

    private fun onCreatedGame(state: Game?, errorCode:Int?){
        if(state == null){
            print(errorCode)
            return
        }
        val intent = Intent(this, GameActivity::class.java).apply {
            putExtra("EXTRA_STATE", state)
        }
        startActivity(intent)
        /*print(state.gameId)
        print(errorCode)
        val dlg = GameIdDialog(state.gameId)
        dlg.show(supportFragmentManager,"GameIdDialogFragment")*/
        /*val displayText = "%1s%2s".format("Latest game id: ", state.gameId)
        binding.tvGameId.text = displayText*/
    }

    private fun onJoinedGame(state: Game?, errorCode:Int?){
        print(state)
        print(errorCode)
    }

}