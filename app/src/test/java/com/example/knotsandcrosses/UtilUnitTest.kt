package com.example.knotsandcrosses

import com.example.knotsandcrosses.util.copyGameStateWithoutReference
import com.example.knotsandcrosses.util.isDrawn
import com.example.knotsandcrosses.util.isWon
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class UtilUnitTest {
    @Test
    fun isWon_isCorrect() {  // Player 1 always has X.
        var state = mutableListOf(mutableListOf("X", "X", "X"), mutableListOf("O", "0", "0"), mutableListOf("0", "O", "O"))
        GameManager.state = state
        var result = isWon("player1")
        assertThat(result).isTrue()
        result = isWon("player2")
        assertThat(result).isFalse()

        state = mutableListOf(mutableListOf("O", "X", "X"), mutableListOf("0", "O", "0"), mutableListOf("0", "X", "O"))
        GameManager.state = state
        result = isWon("player1")
        assertThat(result).isFalse()
        result = isWon("player2")
        assertThat(result).isTrue()

        state = mutableListOf(mutableListOf("O", "0", "X"), mutableListOf("0", "O", "X"), mutableListOf("0", "0", "X"))
        GameManager.state = state
        result = isWon("player1")
        assertThat(result).isTrue()
        result = isWon("player2")
        assertThat(result).isFalse()
    }

    @Test
    fun isDrawn_isCorrect() {
        var state = mutableListOf(mutableListOf("X", "O", "X"), mutableListOf("O", "X", "O"), mutableListOf("X", "O", "X"))
        GameManager.state = state
        var result = isDrawn()
        assertThat(result).isTrue()

        state = mutableListOf(mutableListOf("X", "0", "X"), mutableListOf("0", "X", "O"), mutableListOf("0", "O", "0"))
        GameManager.state = state
        result = isDrawn()
        assertThat(result).isFalse()

        state = mutableListOf(mutableListOf("X", "0", "X"), mutableListOf("X", "X", "O"), mutableListOf("O", "O", "X"))
        GameManager.state = state
        result = isDrawn()
        assertThat(result).isFalse()
    }

    @Test
    fun copyGameStateWithoutReference_isCorrect(){
        GameManager.state = mutableListOf(mutableListOf("X", "O", "X"), mutableListOf("O", "X", "O"), mutableListOf("X", "O", "X"))
        GameManager.tempState = copyGameStateWithoutReference()
        GameManager.tempState[0][0] = "O"
        assertThat(GameManager.state).isNotEqualTo(GameManager.tempState)
    }
}