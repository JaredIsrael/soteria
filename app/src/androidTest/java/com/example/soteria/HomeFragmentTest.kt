package com.example.soteria

import androidx.test.espresso.Espresso.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.action.ViewActions.click
import androidx.test.filters.LargeTest

import com.example.soteria.HomeFragment

import junit.framework.TestCase
import org.junit.runner.RunWith
import org.junit.Before
import org.junit.Test

@RunWith(AndroidJUnit4::class)
@LargeTest
internal class HomeFragmentTest : TestCase() {

    private lateinit var scenario: FragmentScenario<HomeFragment>

    @Before
    fun setup() {
        scenario = launchFragmentInContainer()
        scenario.moveToState(Lifecycle.State.STARTED)
    }

    @Test
    fun testStartBtn() {
        onView(withId(R.id.startBtn)).perform(click()).check(matches(withText(R.string.start_button_stop)))
    }

    @Test
    fun testSetTimeBtnOnStart() {
        onView(withId(R.id.startBtn)).perform(click())
        onView(withId(R.id.setTimeBtn)).check(matches(withText(R.string.set_time_button_start_recording)))
    }

    @Test
    fun testStartBtnStop() {
        onView(withId(R.id.startBtn)).perform(click())
        onView(withId(R.id.startBtn)).perform(click()).check(matches(withText(R.string.start_button_start)))
        onView(withId(R.id.timeTv)).check(matches(withText("00:30:00")))
    }

}