package com.example.pawtrack.Tracking

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.core.app.ActivityScenario
import com.example.pawtrack.HomePageActivity
import com.example.pawtrack.Map.MapActivity
import com.example.pawtrack.R
import com.example.pawtrack.User.SubscriptionActivity
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Calendar

@RunWith(AndroidJUnit4::class)
class YearlyReviewActivityTest {

    private lateinit var activityScenario: ActivityScenario<YearlyReviewActivity>

    @Before
    fun setup() {
        Intents.init()
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sharedPreferences = context.getSharedPreferences("PawTrackPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putString("LastSelectedPetId", "valid_pet_id")
            .putString("USERNAME", "valid_username")
            .apply()
        activityScenario = ActivityScenario.launch(YearlyReviewActivity::class.java)
    }

    @After
    fun tearDown() {
        Intents.release()
        activityScenario.close()
    }

    /*
    @Test
    fun testChartsVisibility() {
        Thread.sleep(3000)

        onView(withId(R.id.distanceChart))
            .check(matches(isDisplayed()))
        onView(withId(R.id.caloriesChart))
            .check(matches(isDisplayed()))
        onView(withId(R.id.activeDaysChart))
            .check(matches(isDisplayed()))
    }*/

    @Test
    fun testBackButtonNavigation() {
        onView(withId(R.id.button)).perform(click())
        Intents.intended(hasComponent(StatisticsActivity::class.java.name))
    }

    @Test
    fun testBottomNavigationVisibility() {
        onView(withId(R.id.bottomNavigationView))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testBottomNavigationFunctionality() {
        onView(withId(R.id.home)).perform(click())
        Intents.intended(hasComponent(HomePageActivity::class.java.name))
        activityScenario = ActivityScenario.launch(YearlyReviewActivity::class.java)
        onView(withId(R.id.map)).perform(click())
        Intents.intended(hasComponent(MapActivity::class.java.name))
        activityScenario = ActivityScenario.launch(YearlyReviewActivity::class.java)
        onView(withId(R.id.tracking)).perform(click())
        Intents.intended(hasComponent(TrackingActivity::class.java.name))
        activityScenario = ActivityScenario.launch(YearlyReviewActivity::class.java)
        onView(withId(R.id.subscription)).perform(click())
        Intents.intended(hasComponent(SubscriptionActivity::class.java.name))
    }

    @Test
    fun testYearTextViewVisibility() {
        onView(withId(R.id.textView31))
            .check(matches(isDisplayed()))
            .check(matches(withText(Calendar.getInstance().get(Calendar.YEAR).toString())))
    }

    @Test
    fun testToastWhenNoPetSelected() {
        // Remove pet ID to simulate no pet selected
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sharedPreferences = context.getSharedPreferences("PawTrackPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("LastSelectedPetId", null).apply()
        activityScenario.close()
        activityScenario = ActivityScenario.launch(YearlyReviewActivity::class.java)
    }
}
