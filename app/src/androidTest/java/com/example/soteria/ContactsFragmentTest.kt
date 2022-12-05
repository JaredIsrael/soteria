package com.example.soteria

import android.view.View
import androidx.core.view.size
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
 import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
internal class ContactsFragmentTest {

    private lateinit var scenario: FragmentScenario<ContactsFragment>

    private fun insertContact() {
        onView(withId(R.id.nameInput)).perform(typeText("Braxton White"))
        onView(withId(R.id.numInput)).perform(typeText("6148675309"))
        onView(withId(R.id.saveButton)).perform(click())
    }

    private fun recyclerViewSizeMatcher(size: Int) : Matcher<View?>? {
        return object : BoundedMatcher<View?, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description?) {
//                description?.appendText("with list size: $size")
            }

            override fun matchesSafely(item: RecyclerView?): Boolean {
                return size == item!!.adapter!!.itemCount
            }

        }
    }

    @Before
    fun setUp() {
        scenario = launchFragmentInContainer()
        scenario.moveToState(Lifecycle.State.STARTED)
    }

    @Test
    fun insertContactTest() {

        insertContact()

        onView(withId(R.id.recyclerView1)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerViewAdapter.MyViewHolder>(0, click()))
        onView(withId(R.id.nameInput)).check(matches(withText("Braxton White")))
        onView(withId(R.id.numInput)).check(matches(withText("6148675309")))
    }

    @Test
    fun deleteContactTest() {
        insertContact()
        onView(withId(R.id.recyclerView1)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerViewAdapter.MyViewHolder>(0, click()))
        onView(withId(R.id.deleteButton)).perform(click())

    }

    @Test
    fun updateContactTest() {
        insertContact()
        onView(withId(R.id.recyclerView1)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerViewAdapter.MyViewHolder>(0, click()))
        onView(withId(R.id.nameInput)).perform(clearText())
        onView(withId(R.id.nameInput)).perform(typeText("Umar Jara"))
        onView(withId(R.id.saveButton)).perform(click())
        onView(withId(R.id.recyclerView1)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerViewAdapter.MyViewHolder>(0, click()))
        onView(withId(R.id.nameInput)).check(matches(withText("Umar Jara")))
    }
}