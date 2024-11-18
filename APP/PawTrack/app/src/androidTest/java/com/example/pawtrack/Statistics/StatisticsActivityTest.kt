package com.example.pawtrack.Tracking

import android.content.Context
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
import com.example.pawtrack.Pet.PetProfileActivity
import com.example.pawtrack.R
import com.example.pawtrack.User.UserProfileActivity
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StatisticsActivityTest {

    private lateinit var activityScenario: ActivityScenario<StatisticsActivity>

    @Before
    fun setup() {
        Intents.init()
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sharedPreferences = context.getSharedPreferences("PawTrackPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putString("LastSelectedPetId", "valid_pet_id")
            .putString("USERNAME", "valid_username")
            .apply()
        activityScenario = ActivityScenario.launch(StatisticsActivity::class.java)
    }

    @After
    fun tearDown() {
        Intents.release()
        activityScenario.close()
    }

    @Test
    fun testBottomNavigationVisibility() {
        onView(withId(R.id.bottomNavigationView))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testPetProfileButtonNavigation() {
        onView(withId(R.id.pet_profile)).perform(click())
        Intents.intended(hasComponent(PetProfileActivity::class.java.name))
    }

    @Test
    fun testProfileButtonNavigation() {
        onView(withId(R.id.floatingActionButton2)).perform(click())
        Intents.intended(hasComponent(UserProfileActivity::class.java.name))
    }

    @Test
    fun testYearlyReviewButtonNavigation() {
        onView(withId(R.id.floatingActionButton3)).perform(click())
        Intents.intended(hasComponent(YearlyReviewActivity::class.java.name))
    }

    @Test
    fun testCircularProgressBarsVisibility() {
        val progressBarIds = listOf(
            R.id.progressBar2,
            R.id.progressBar4,
            R.id.progressBar5,
            R.id.progressBar6,
            R.id.progressBar7,
            R.id.progressBar8,
            R.id.progressBar10,
            R.id.progressBar9
        )
        progressBarIds.forEach { id ->
            onView(withId(id))
                .check(matches(isDisplayed()))
        }
    }
}
