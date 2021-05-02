package com.example.knotsandcrosses.util

import android.util.Log
import com.example.knotsandcrosses.GameManager

fun checkForWin(player: String): Boolean {
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

fun checkForDraw(): Boolean {
    GameManager.state?.forEach { row ->
        row.forEach {
            if(it == "0"){
                return false
            }
        }
    }
    return true
}