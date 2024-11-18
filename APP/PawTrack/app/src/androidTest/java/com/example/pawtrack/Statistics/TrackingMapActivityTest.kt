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
class TrackingMapActivityTest {

    private lateinit var activityScenario: ActivityScenario<TrackingMapActivity>

    @Before
    fun setup() {
        Intents.init()
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sharedPreferences = context.getSharedPreferences("PawTrackPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putString("LastSelectedPetId", "valid_pet_id") // Ensure a valid pet ID
            .putString("USERNAME", "valid_username")
            .apply()
        activityScenario = ActivityScenario.launch(TrackingMapActivity::class.java)
    }

    @After
    fun tearDown() {
        Intents.release()
        activityScenario.close()
    }

    @Test
    fun testStartButtonFunctionality() {
        onView(withId(R.id.btnPetStart))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.map_start)))

        onView(withId(R.id.btnPetStart)).perform(click())
        onView(withId(R.id.btnPetStart))
            .check(matches(withText(R.string.map_stop)))

        onView(withId(R.id.btnPetStart)).perform(click())
        onView(withId(R.id.btnPetStart))
            .check(matches(withText(R.string.map_start)))
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
    fun testBottomNavigationVisibility() {
        onView(withId(R.id.bottomNavigationView))
            .check(matches(isDisplayed()))
    }
}
