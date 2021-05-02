package com.example.knotsandcrosses

import android.os.SystemClock
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
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