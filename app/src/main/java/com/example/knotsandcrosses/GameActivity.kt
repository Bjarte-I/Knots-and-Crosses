package com.example.knotsandcrosses

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.knotsandcrosses.api.data.Game
import com.example.knotsandcrosses.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_game)

        val state = intent.getParcelableExtra<Game>("EXTRA_STATE")


        binding.tvGameId.text = state?.gameId
    }
}