package com.example.knotsandcrosses.util

import androidx.test.espresso.idling.CountingIdlingResource

//This is used to track when a network call is in progress, so Espresso can wait till it is finished in testing.
object CountingIdlingResourceSingleton {

    private const val RESOURCE = "GLOBAL"

    @JvmField val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }
}