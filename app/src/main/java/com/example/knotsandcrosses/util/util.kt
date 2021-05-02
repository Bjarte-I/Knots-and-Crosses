package com.example.knotsandcrosses.util

import com.example.knotsandcrosses.GameManager

fun isWon(player: String): Boolean {
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

fun isDrawn(): Boolean {
    GameManager.state?.forEach { row ->
        row.forEach {
            if(it == "0"){
                return false
            }
        }
    }
    return true
}

fun copyGameStateWithoutReference(): MutableList<MutableList<String>> {
    val tempState = mutableListOf(mutableListOf("0", "0", "0"), mutableListOf("0", "0", "0"), mutableListOf("0", "0", "0"))
    val values = mutableListOf<String>()
    GameManager.state?.forEach { rows ->
        rows.forEach {
            values.add(it)
        }
    }
    tempState[0] = mutableListOf(values[0], values[1], values[2])
    tempState[1] = mutableListOf(values[3], values[4], values[5])
    tempState[2] = mutableListOf(values[6], values[7], values[8])
    return tempState
}