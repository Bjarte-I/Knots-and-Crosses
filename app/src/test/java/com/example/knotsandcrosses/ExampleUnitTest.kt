package com.example.knotsandcrosses

import com.example.knotsandcrosses.util.checkForDraw
import com.example.knotsandcrosses.util.checkForWin
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun gameWonOrLost_isCorrect() {
        var state = mutableListOf(mutableListOf("X", "X", "X"), mutableListOf("O", "0", "0"), mutableListOf("0", "O", "O"))
        GameManager.state = state
        var result = checkForWin("player1")
        assertThat(result).isTrue()
        result = checkForWin("player2")
        assertThat(result).isFalse()

        state = mutableListOf(mutableListOf("O", "X", "X"), mutableListOf("0", "O", "0"), mutableListOf("0", "X", "O"))
        GameManager.state = state
        result = checkForWin("player1")
        assertThat(result).isFalse()
        result = checkForWin("player2")
        assertThat(result).isTrue()

        state = mutableListOf(mutableListOf("O", "0", "X"), mutableListOf("0", "O", "X"), mutableListOf("0", "0", "X"))
        GameManager.state = state
        result = checkForWin("player1")
        assertThat(result).isTrue()
        result = checkForWin("player2")
        assertThat(result).isFalse()
    }

    @Test
    fun gameDraw_isCorrect() {
        var state = mutableListOf(mutableListOf("X", "O", "X"), mutableListOf("O", "X", "O"), mutableListOf("X", "O", "X"))
        GameManager.state = state
        var result = checkForDraw()
        assertThat(result).isTrue()

        state = mutableListOf(mutableListOf("X", "0", "X"), mutableListOf("0", "X", "O"), mutableListOf("0", "O", "0"))
        GameManager.state = state
        result = checkForDraw()
        assertThat(result).isFalse()

        state = mutableListOf(mutableListOf("X", "0", "X"), mutableListOf("X", "X", "O"), mutableListOf("O", "O", "X"))
        GameManager.state = state
        result = checkForDraw()
        assertThat(result).isFalse()
    }
}