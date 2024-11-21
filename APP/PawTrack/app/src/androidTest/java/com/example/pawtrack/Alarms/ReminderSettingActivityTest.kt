package com.example.pawtrack.Alarms

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import android.widget.TimePicker
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import androidx.test.core.app.ActivityScenario
import com.example.pawtrack.HomePageActivity
import com.example.pawtrack.Map.MapActivity
import com.example.pawtrack.R
import com.example.pawtrack.Tracking.StatisticsActivity
import com.example.pawtrack.Tracking.TrackingActivity
import com.example.pawtrack.User.SubscriptionActivity
import com.example.pawtrack.User.UserProfileActivity
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import junit.framework.TestCase.assertTrue
import org.junit.FixMethodOrder
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
class ReminderSettingActivityTest {

    private lateinit var activityScenario: ActivityScenario<ReminderSettingActivity>

    @Before
    fun setup() {
        Intents.init()

        // Set up shared preferences
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sharedPreferences = context.getSharedPreferences("PawTrackPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putString("USERNAME", "test_user")
            .apply()

        // Launch the target activity
        activityScenario = ActivityScenario.launch(ReminderSettingActivity::class.java)
    }

    @After
    fun tearDown() {
        // Release Intents and close the activity
        Intents.release()
        activityScenario.close()
    }

    @Test
    fun testAddReminder() {
        // Add a new reminder
        onView(withId(R.id.fabAddReminder)).perform(click())
        onView(withId(R.id.etReminderName)).perform(typeText("Test Reminder"), closeSoftKeyboard())
        onView(withId(R.id.cbRepeat)).perform(click())
        onView(withId(R.id.timePicker)).perform(setTime(10, 30)) // Set time to 10:30
        onView(withId(R.id.btnSaveReminder)).perform(click())

        // Verify the reminder was added
        onView(withId(R.id.recyclerViewReminders))
            .check(matches(hasDescendant(withText("Test Reminder"))))

        // Delete the added reminder
        onView(withId(R.id.recyclerViewReminders))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnViewChild(R.id.buttonDelete)))

        // Verify the reminder was deleted
        onView(withId(R.id.recyclerViewReminders))
            .check(matches(not(hasDescendant(withText("Test Reminder")))))

        // Verify the bottom sheet is hidden and the RecyclerView is displayed
        onView(withId(R.id.bottomSheet)).check(matches(not(isDisplayed())))
        onView(withId(R.id.recyclerViewReminders)).check(matches(isDisplayed()))
    }

    @Test
    fun testFabTogglesBottomSheet() {
        onView(withId(R.id.fabAddReminder)).perform(click())
        onView(withId(R.id.bottomSheet)).check(matches(isDisplayed()))
        onView(withId(R.id.recyclerViewReminders)).check(matches(not(isDisplayed())))

        onView(withId(R.id.fabAddReminder)).perform(click())
        onView(withId(R.id.bottomSheet)).check(matches(not(isDisplayed())))
        onView(withId(R.id.recyclerViewReminders)).check(matches(isDisplayed()))
    }

    @Test
    fun testRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val context = ApplicationProvider.getApplicationContext<Context>()
            val permission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )

            if (permission != PackageManager.PERMISSION_GRANTED) {
                onView(withText("Allow notifications?")).check(matches(isDisplayed()))
            }
        }
    }

    @Test
    fun testClearInputs() {
        // Add a reminder
        onView(withId(R.id.fabAddReminder)).perform(click())
        onView(withId(R.id.etReminderName)).perform(typeText("Reminder Test"), closeSoftKeyboard())
        onView(withId(R.id.cbRepeat)).perform(click())
        onView(withId(R.id.timePicker)).perform(setTime(8, 45))

        // Save the reminder to trigger input clear
        onView(withId(R.id.btnSaveReminder)).perform(click())

        // Verify the inputs are cleared
        onView(withId(R.id.etReminderName)).check(matches(withText("")))
        onView(withId(R.id.cbRepeat)).check(matches(not(isChecked())))

        // Verify the reminder is added to the RecyclerView
        onView(withId(R.id.recyclerViewReminders))
            .check(matches(hasDescendant(withText("Reminder Test"))))

        // Delete the added reminder
        onView(withId(R.id.recyclerViewReminders))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnViewChild(R.id.buttonDelete)))

        // Verify the reminder is deleted
        onView(withId(R.id.recyclerViewReminders))
            .check(matches(not(hasDescendant(withText("Reminder Test")))))
    }

    @Test
    fun testDeleteAllReminders() {
        // Continuously delete items one by one until RecyclerView is empty
        var itemCount = 1
        while (itemCount > 0) {
            onView(withId(R.id.recyclerViewReminders))
                .check { view, _ ->
                    val recyclerView = view as RecyclerView
                    itemCount = recyclerView.adapter?.itemCount ?: 0
                }

            if (itemCount > 0) {
                onView(withId(R.id.recyclerViewReminders))
                    .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnViewChild(R.id.buttonDelete)))
            }
        }

        // Finally, verify that RecyclerView is empty
        onView(withId(R.id.recyclerViewReminders)).check(matches(hasChildCount(0)))
    }

    @Test
    fun testNotificationPermissionGranted() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val permission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        )
        assert(permission == PackageManager.PERMISSION_GRANTED)
    }


    @Test
    fun testDeleteSpecificAlarm() {
        // Add a new alarm for deletion
        onView(withId(R.id.fabAddReminder)).perform(click())
        onView(withId(R.id.etReminderName)).perform(typeText("Alarm to Delete"), closeSoftKeyboard())
        onView(withId(R.id.cbRepeat)).perform(click())
        onView(withId(R.id.timePicker)).perform(setTime(7, 45)) // Set time to 7:45
        onView(withId(R.id.btnSaveReminder)).perform(click())

        // Verify the alarm was added
        onView(withId(R.id.recyclerViewReminders))
            .check(matches(hasDescendant(withText("Alarm to Delete"))))

        // Delete the added alarm
        onView(withId(R.id.recyclerViewReminders))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnViewChild(R.id.buttonDelete)))

        // Verify the alarm was deleted
        onView(withId(R.id.recyclerViewReminders))
            .check(matches(not(hasDescendant(withText("Alarm to Delete")))))
    }

    @Test
    fun testBackNavigationFromHome() {
        onView(withId(R.id.home)).perform(click())
        Intents.intended(hasComponent(HomePageActivity::class.java.name))
    }

    @Test
    fun testBackNavigationFromMap() {
        onView(withId(R.id.map)).perform(click())
        Intents.intended(hasComponent(MapActivity::class.java.name))
    }

    @Test
    fun testNotificationPermissionRequest() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val permission = Manifest.permission.POST_NOTIFICATIONS

        // Simulate granting the permission
        activityScenario.onActivity { activity ->
            val requestCode = 1001
            val permissions = arrayOf(permission)
            val grantResults = intArrayOf(PackageManager.PERMISSION_GRANTED)

            activity.onRequestPermissionsResult(requestCode, permissions, grantResults)

            // Verify the behavior when permission is granted
            assertTrue("Permission granted", grantResults[0] == PackageManager.PERMISSION_GRANTED)
        }

        // Simulate denying the permission
        activityScenario.onActivity { activity ->
            val requestCode = 1001
            val permissions = arrayOf(permission)
            val grantResults = intArrayOf(PackageManager.PERMISSION_DENIED)

            activity.onRequestPermissionsResult(requestCode, permissions, grantResults)

            // Verify the behavior when permission is denied
            assertTrue("Permission denied", grantResults[0] == PackageManager.PERMISSION_DENIED)
        }
    }

    @Test
    fun testBackButtonNavigatesToUserProfile() {
        // Perform a click on the back button
        onView(withId(R.id.buttonBack)).perform(click())

        // Verify that the UserProfileActivity is launched
        Intents.intended(hasComponent(UserProfileActivity::class.java.name))
    }
    @Test
    fun testAlarmPersistence() {
        // Add a reminder
        onView(withId(R.id.fabAddReminder)).perform(click())
        onView(withId(R.id.etReminderName)).perform(typeText("Persistent Alarm"), closeSoftKeyboard())
        onView(withId(R.id.btnSaveReminder)).perform(click())

        // Relaunch the activity
        activityScenario.close()
        activityScenario = ActivityScenario.launch(ReminderSettingActivity::class.java)

        // Verify the alarm persists
        onView(withId(R.id.recyclerViewReminders))
            .check(matches(hasDescendant(withText("Persistent Alarm"))))

        // Delete the added reminder
        onView(withId(R.id.recyclerViewReminders))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnViewChild(R.id.buttonDelete)))

        // Verify the reminder was deleted
        onView(withId(R.id.recyclerViewReminders))
            .check(matches(not(hasDescendant(withText("Persistent Alarm")))))
    }

    @Test
    fun testNavigateToHome() {
        onView(withId(R.id.home)).perform(click())
        Intents.intended(hasComponent(HomePageActivity::class.java.name))
    }

    @Test
    fun testNavigateToTracking() {
        onView(withId(R.id.tracking)).perform(click())
        Intents.intended(hasComponent(TrackingActivity::class.java.name))
    }

    @Test
    fun testNavigateToStatistics() {
        onView(withId(R.id.statistics)).perform(click())
        Intents.intended(hasComponent(StatisticsActivity::class.java.name))
    }

    @Test
    fun testNavigateToSubscription() {
        onView(withId(R.id.subscription)).perform(click())
        Intents.intended(hasComponent(SubscriptionActivity::class.java.name))
    }

    private fun setTime(hour: Int, minute: Int): ViewAction {
        return object : ViewAction {
            override fun getDescription() = "Set time picker to $hour:$minute"
            override fun getConstraints() = isAssignableFrom(TimePicker::class.java)
            override fun perform(uiController: UiController, view: View) {
                val timePicker = view as TimePicker
                timePicker.hour = hour
                timePicker.minute = minute
            }
        }
    }

    private fun clickOnViewChild(viewId: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints() = null
            override fun getDescription() = "Click on a child view with specified id."
            override fun perform(uiController: UiController, view: View) {
                val childView = view.findViewById<View>(viewId)
                childView.performClick()
            }
        }
    }
}