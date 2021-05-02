package com.example.knotsandcrosses

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.knotsandcrosses.api.GameService
import com.example.knotsandcrosses.api.data.Game
import com.example.knotsandcrosses.util.CountingIdlingResourceSingleton
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UITesting {

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity>
            = ActivityScenarioRule(MainActivity::class.java)
    @Test
    fun createGameDialog_is_displayed() {
        onView(withId(R.id.startGameButton))
            .perform(click())
        onView(withId(R.id.username))
            .check(matches(isDisplayed()))
    }

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(CountingIdlingResourceSingleton.countingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(CountingIdlingResourceSingleton.countingIdlingResource)
    }

    @Test
    fun gameActivity_isLaunched_createGame(){
        onView(withId(R.id.startGameButton))
            .perform(click())
        onView(withId(R.id.username))
            .perform(typeText("Player 1"))
        onView(withText("Create"))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
            .perform(click())
        onView(withId(R.id.tv_player_name))
            .check(matches(withText("Player 1")))
        onView(withId(R.id.tv_opponent_name))
            .check(matches(withText("Waiting for opponent")))
    }

    @Test
    fun gameActivity_isLaunched_joinGame(){
        lateinit var gameId:String
        GameService.createGame("Player 1", GameManager.StartingGameState){ game: Game?, err:Int? ->
            if(err == null){
                gameId = game?.gameId.toString()
            }
        }

        onView(withId(R.id.joinGameButton))
            .perform(click())
        onView(withId(R.id.player_name))
            .perform(typeText("Player 2"))
        onView(withId(R.id.dialog_gameId))
            .perform(typeText(gameId))
        onView(withText("Join"))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
            .perform(click())
        onView(withId(R.id.tv_opponent_name))
            .check(matches(withText("Player 1")))
        onView(withId(R.id.tv_player_name))
            .check(matches(withText("Player 2")))
    }
}