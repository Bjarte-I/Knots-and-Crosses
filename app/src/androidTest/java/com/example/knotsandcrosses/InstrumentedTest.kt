package com.example.knotsandcrosses

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith
import com.google.common.truth.Truth.assertThat

@RunWith(AndroidJUnit4::class)
class InstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertThat("com.example.knotsandcrosses").isEqualTo(appContext.packageName)
    }

    @Test
    fun createGame_GameManager_isCorrect() {
        val player = "Player 1"
        GameManager.createGame(player)
        assertThat(GameManager.player1).isEqualTo(player)
        assertThat(GameManager.gameId).isNotNull()
    }
}