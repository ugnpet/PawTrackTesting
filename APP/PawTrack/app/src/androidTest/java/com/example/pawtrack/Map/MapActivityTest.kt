package com.example.pawtrack.Map

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.pawtrack.HomePageActivity
import com.example.pawtrack.R
import com.example.pawtrack.Tracking.StatisticsActivity
import com.example.pawtrack.Tracking.TrackingActivity
import com.example.pawtrack.User.SubscriptionActivity
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf
import org.osmdroid.views.MapView

@RunWith(AndroidJUnit4::class)
class MapActivityTest {

    private lateinit var activityScenario: ActivityScenario<MapActivity>

    @Before
    fun setup() {
        Intents.init()
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sharedPreferences = context.getSharedPreferences("PawTrackPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putString("LastSelectedPetId", "valid_pet_id")
            .putString("USERNAME", "test_user")
            .apply()
        activityScenario = ActivityScenario.launch(MapActivity::class.java)
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
    fun testHomeNavigation() {
        onView(withId(R.id.home)).perform(click())
        Intents.intended(hasComponent(HomePageActivity::class.java.name))
    }

    @Test
    fun testTrackingNavigation() {
        onView(withId(R.id.tracking)).perform(click())
        Intents.intended(hasComponent(TrackingActivity::class.java.name))
    }

    @Test
    fun testStatisticsNavigation() {
        onView(withId(R.id.statistics)).perform(click())
        Intents.intended(hasComponent(StatisticsActivity::class.java.name))
    }

    @Test
    fun testSubscriptionNavigation() {
        onView(withId(R.id.subscription)).perform(click())
        Intents.intended(hasComponent(SubscriptionActivity::class.java.name))
    }

    @Test
    fun testSearchParksButton() {
        onView(allOf(withId(R.id.btnParks), withEffectiveVisibility(Visibility.VISIBLE))).perform(click())
        onView(allOf(withId(R.id.map), isAssignableFrom(MapView::class.java))).check(matches(isDisplayed()))
    }

    @Test
    fun testSearchShopsButton() {
        onView(allOf(withId(R.id.btnPetShops), withEffectiveVisibility(Visibility.VISIBLE))).perform(click())
        onView(allOf(withId(R.id.map), isAssignableFrom(MapView::class.java))).check(matches(isDisplayed()))
    }

    @Test
    fun testSearchVetsButton() {
        onView(allOf(withId(R.id.btnVets), withEffectiveVisibility(Visibility.VISIBLE))).perform(click())
        onView(allOf(withId(R.id.map), isAssignableFrom(MapView::class.java))).check(matches(isDisplayed()))
    }

    @Test
    fun testLocationPermissionRequest() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val permissionStatus = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)

        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
            // Simulate the user granting the permission
            activityScenario.onActivity { activity ->
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1001
                )
            }
        }
    }
}
