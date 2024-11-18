package com.example.pawtrack.Tracking

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.assertion.ViewAssertions.matches
import com.example.pawtrack.Pet.PetProfileActivity
import com.example.pawtrack.R
import com.example.pawtrack.User.UserProfileActivity
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RouteActivityTest {

    private lateinit var activityScenario: ActivityScenario<RouteActivity>

    @Before
    fun setup() {
        Intents.init()
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sharedPreferences = context.getSharedPreferences("PawTrackPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putString("LastSelectedPetId", "some_pet_id")
            .putString("USERNAME", "some_username")
            .apply()
        activityScenario = ActivityScenario.launch(RouteActivity::class.java)
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
    fun testToastWhenNoPetSelected() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sharedPreferences = context.getSharedPreferences("PawTrackPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("LastSelectedPetId", null).apply()
        activityScenario.close()
        activityScenario = ActivityScenario.launch(RouteActivity::class.java)
        Intents.intended(hasComponent(PetProfileActivity::class.java.name))
    }
}
