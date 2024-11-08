package com.example.pawtrack.Alarms

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O])
class AndroidAlarmSchedulerTest {

    private lateinit var context: Context
    private lateinit var alarmScheduler: AndroidAlarmScheduler
    private lateinit var alarmManager: AlarmManager

    // Initialize the context and intent before each test
    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        alarmScheduler = AndroidAlarmScheduler(context)
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    // Test to check if an alarm is scheduled correctly
    @Test
    fun testScheduleAlarm() {
        // Schedule an alarm
        val alarmItem = AlarmItem(id = 1, message = "Alarm test", time = "10:00", repeat = false)
        alarmScheduler.schedule(alarmItem)

        // Simulate Android Framework class (AlarmManager) and get notifications
        val shadowAlarmManager = Shadows.shadowOf(alarmManager)
        val scheduledAlarms = shadowAlarmManager.scheduledAlarms

        // Assert that an alarm was scheduled
        assert(scheduledAlarms.isNotEmpty()) { "No alarms were scheduled." }

        // Verify the alarm is set to the expected time
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val expectedTime = sdf.parse(alarmItem.time) ?: Date()
        val calendar = Calendar.getInstance().apply {
            time = expectedTime
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1) // Move to next day if time is in the past
            }
        }
        assertEquals(calendar.timeInMillis, scheduledAlarms[0].triggerAtTime)
        assertEquals(AlarmManager.RTC_WAKEUP, scheduledAlarms[0].type)

        // Verify the message extra in alarm
        val pendingIntent = scheduledAlarms[0].operation
        val shadowPendingIntent = Shadows.shadowOf(pendingIntent)
        val intent = shadowPendingIntent.savedIntent
        val actualMessage = intent.getStringExtra("EXTRA_MESSAGE")
        assertEquals(alarmItem.message, actualMessage)
    }

    @Test
    fun testScheduleRecurringAlarm() {
        // Define a recurring alarm
        val alarmItem = AlarmItem(id = 2, message = "Daily alarm test", time = "10:30", repeat = true)
        alarmScheduler.schedule(alarmItem)

        // Retrieve the shadow of AlarmManager and check the scheduled alarms
        val shadowAlarmManager = Shadows.shadowOf(alarmManager)
        val scheduledAlarms = shadowAlarmManager.scheduledAlarms

        // Ensure that at least one alarm is scheduled
        assert(scheduledAlarms.isNotEmpty()) { "No alarm was scheduled." }

        // Verify the first scheduled alarm is set for the correct time with repetition enabled
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val expectedTime = sdf.parse(alarmItem.time) ?: Date()
        val calendar = Calendar.getInstance().apply {
            time = expectedTime
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1) // Move to the next day if the time is in the past
            }
        }
        val expectedTimeInMillis = calendar.timeInMillis

        // Check the alarm trigger time and repeat interval
        assertEquals(expectedTimeInMillis, scheduledAlarms[0].triggerAtTime)
        assertEquals(AlarmManager.INTERVAL_DAY, scheduledAlarms[0].interval)

        // Verify the message passed to the alarm
        val pendingIntent = scheduledAlarms[0].operation
        val shadowPendingIntent = Shadows.shadowOf(pendingIntent)
        val intent = shadowPendingIntent.savedIntent
        val actualMessage = intent.getStringExtra("EXTRA_MESSAGE")
        assertEquals(alarmItem.message, actualMessage)
    }

    // Test to check if you can schedule two alarms at the same time
    @Test
    fun testScheduleMultipleAlarmsAtSameTime() {
        // Schedule two alarms with the same time but different messages
        val alarmItem1 = AlarmItem(id = 3, message = "Test Alarm 1", time = "08:00", repeat = false)
        val alarmItem2 = AlarmItem(id = 4, message = "Test Alarm 2", time = "08:00", repeat = false)

        alarmScheduler.schedule(alarmItem1)
        alarmScheduler.schedule(alarmItem2)

        // Retrieve the shadow of AlarmManager and check scheduled alarms
        val shadowAlarmManager = Shadows.shadowOf(alarmManager)
        val scheduledAlarms = shadowAlarmManager.scheduledAlarms

        // Assert that two alarms were scheduled
        assertEquals("Expected two alarms to be scheduled.", 2, scheduledAlarms.size)

        // Verify that each alarm has the correct time and message
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val expectedTime = sdf.parse("08:00") ?: Date()
        val calendar = Calendar.getInstance().apply {
            time = expectedTime
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1) // Move to the next day if the time is in the past
            }
        }
        val expectedTimeInMillis = calendar.timeInMillis

        // Check alarm 1
        val pendingIntent1 = scheduledAlarms[0].operation
        val shadowPendingIntent1 = Shadows.shadowOf(pendingIntent1)
        val intent1 = shadowPendingIntent1.savedIntent
        val actualMessage1 = intent1.getStringExtra("EXTRA_MESSAGE")

        assertEquals(expectedTimeInMillis, scheduledAlarms[0].triggerAtTime)
        assertEquals("Test Alarm 1", actualMessage1)

        // Check alarm 2
        val pendingIntent2 = scheduledAlarms[1].operation
        val shadowPendingIntent2 = Shadows.shadowOf(pendingIntent2)
        val intent2 = shadowPendingIntent2.savedIntent
        val actualMessage2 = intent2.getStringExtra("EXTRA_MESSAGE")

        assertEquals(expectedTimeInMillis, scheduledAlarms[1].triggerAtTime)
        assertEquals("Test Alarm 2", actualMessage2)
    }

    @Test
    fun testScheduleTwoAlarmsAtDifferentTimes() {
        // Schedule two alarms at different times
        val alarmItem1 = AlarmItem(id = 5, message = "Morning alarm test", time = "07:30", repeat = false)
        val alarmItem2 = AlarmItem(id = 6, message = "Evening alarm test", time = "19:45", repeat = false)

        alarmScheduler.schedule(alarmItem1)
        alarmScheduler.schedule(alarmItem2)

        // Retrieve the shadow of AlarmManager and check scheduled alarms
        val shadowAlarmManager = Shadows.shadowOf(alarmManager)
        val scheduledAlarms = shadowAlarmManager.scheduledAlarms

        // Assert that two separate alarms were scheduled
        assertEquals("Expected two alarms to be scheduled.", 2, scheduledAlarms.size)

        // Verify the first alarm is set to 07:30
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val expectedTime1 = sdf.parse("07:30") ?: Date()
        val calendar1 = Calendar.getInstance().apply {
            time = expectedTime1
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1) // Move to next day if time is in the past
            }
        }
        assertEquals(calendar1.timeInMillis, scheduledAlarms[0].triggerAtTime)

        // Verify the message for the first alarm
        val intent1 = Shadows.shadowOf(scheduledAlarms[0].operation).savedIntent
        val actualMessage1 = intent1.getStringExtra("EXTRA_MESSAGE")
        assertEquals("Morning alarm test", actualMessage1)

        // Verify the second alarm is set to 19:45
        val expectedTime2 = sdf.parse("19:45") ?: Date()
        val calendar2 = Calendar.getInstance().apply {
            time = expectedTime2
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1) // Move to next day if time is in the past
            }
        }
        assertEquals(calendar2.timeInMillis, scheduledAlarms[1].triggerAtTime)

        // Verify the message for the second alarm
        val intent2 = Shadows.shadowOf(scheduledAlarms[1].operation).savedIntent
        val actualMessage2 = intent2.getStringExtra("EXTRA_MESSAGE")
        assertEquals("Evening alarm test", actualMessage2)
    }

    @Test
    fun testCancelScheduledAlarm() {
        // Schedule an alarm
        val alarmItem = AlarmItem(id = 7, message = "Cancelled alarm test", time = "08:00", repeat = false)
        alarmScheduler.schedule(alarmItem)

        // Simulate Android Framework class (AlarmManager) and get notifications
        val shadowAlarmManager = Shadows.shadowOf(alarmManager)
        var scheduledAlarms = shadowAlarmManager.scheduledAlarms

        // Assert that an alarm was scheduled
        assert(scheduledAlarms.isNotEmpty()) { "No alarms were scheduled." }

        // Cancel the scheduled alarm
        alarmScheduler.cancel(alarmItem.id)

        // Check that the alarm has been canceled
        scheduledAlarms = shadowAlarmManager.scheduledAlarms
        assert(scheduledAlarms.isEmpty()) { "The alarm was not canceled successfully." }
    }

    @Test
    fun testCancelRecurringAlarm() {
        // Schedule a recurring alarm
        val alarmItem = AlarmItem(id = 8, message = "Recurring alarm test", time = "09:00", repeat = true)
        alarmScheduler.schedule(alarmItem)

        // Check that the alarm was scheduled
        val shadowAlarmManager = Shadows.shadowOf(alarmManager)
        var scheduledAlarms = shadowAlarmManager.scheduledAlarms
        assert(scheduledAlarms.isNotEmpty()) { "No recurring alarm was scheduled." }

        // Cancel the recurring alarm
        alarmScheduler.cancel(alarmItem.id)

        // Verify that the alarm was removed
        scheduledAlarms = shadowAlarmManager.scheduledAlarms
        assert(scheduledAlarms.isEmpty()) { "The recurring alarm was not canceled successfully." }
    }

    @Test
    fun testRescheduleAlarm() {
        // Schedule an alarm
        val alarmItem = AlarmItem(id = 9, message = "Reschedule test", time = "06:00", repeat = false)
        alarmScheduler.schedule(alarmItem)

        // Retrieve the shadow of AlarmManager and check the scheduled alarm
        val shadowAlarmManager = Shadows.shadowOf(alarmManager)
        var scheduledAlarms = shadowAlarmManager.scheduledAlarms
        assert(scheduledAlarms.isNotEmpty()) { "No alarm was scheduled." }

        // Reschedule the alarm with a new time
        val newAlarmItem = alarmItem.copy(time = "09:00")
        alarmScheduler.schedule(newAlarmItem)

        // Check that the old alarm was replaced with the new one
        scheduledAlarms = shadowAlarmManager.scheduledAlarms
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val expectedTime = sdf.parse(newAlarmItem.time) ?: Date()
        val calendar = Calendar.getInstance().apply {
            time = expectedTime
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1) // Move to the next day if the time is in the past
            }
        }
        assertEquals(calendar.timeInMillis, scheduledAlarms[0].triggerAtTime)
    }
}