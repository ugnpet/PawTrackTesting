package com.example.pawtrack.Alarms

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.test.core.app.ApplicationProvider
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.Shadows

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O])
class AlarmReceiverTest {

    private lateinit var context: Context
    private lateinit var intent: Intent

    // Initialize the context and intent before each test
    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        intent = Intent(context, AlarmReceiver::class.java)
    }

    // Test to verify that the AlarmReceiver correctly triggers a notification
    @Test
    fun testAlarmReceiverTriggersNotification() {
        // Trigger alarm method
        intent.putExtra("EXTRA_MESSAGE", "Alarm test message")
        val alarmReceiver = AlarmReceiver()
        alarmReceiver.onReceive(context, intent)

        // Simulate Android Framework class (NotificationManager) and get notifications
        val shadowNotificationManager = Shadows.shadowOf(context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        val notifications = shadowNotificationManager.allNotifications

        // Assert
        assertTrue(notifications.isNotEmpty(), "No notifications found")
        val notification = notifications[0]
        assertEquals("Alarm test message", notification.extras.getString(NotificationCompat.EXTRA_TEXT), "Notification message does not match")
        assertEquals("Reminder", notification.extras.getString(NotificationCompat.EXTRA_TITLE), "Notification title does not match")
    }

    // Test to ensure that no notification is created when the message is missing
    @Test
    fun testAlarmReceiverHandlesMissingMessage() {
        // Trigger alarm method
        val alarmReceiver = AlarmReceiver()
        alarmReceiver.onReceive(context, intent)

        // Simulate Android Framework class (NotificationManager) and get notifications
        val shadowNotificationManager = Shadows.shadowOf(context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        val notifications = shadowNotificationManager.allNotifications

        // Assert
        assertTrue(notifications.isEmpty(), "Notification should not be created when message is missing")
    }

    // Test the behavior of the AlarmReceiver on devices running below API level O
    @Test
    @Config(sdk = [Build.VERSION_CODES.LOLLIPOP])
    fun testAlarmReceiverWithoutChannelCreation() {
        // Trigger alarm method
        intent.putExtra("EXTRA_MESSAGE", "Test message on older device")
        val alarmReceiver = AlarmReceiver()
        alarmReceiver.onReceive(context, intent)

        // Retrieve data
        val shadowNotificationManager = Shadows.shadowOf(context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        val notifications = shadowNotificationManager.allNotifications


        // Assert
        assertTrue(notifications.isNotEmpty(), "No notifications found on older device")
        val notification = notifications[0]
        assertEquals("Test message on older device", notification.extras.getString(NotificationCompat.EXTRA_TEXT), "Notification message does not match")
        assertEquals("Reminder", notification.extras.getString(NotificationCompat.EXTRA_TITLE), "Notification title does not match")
    }

    // Test notification channel creation
    @Test
    fun testNotificationChannelCreation() {
        // Trigger creation method
        val alarmReceiver = AlarmReceiver()
        alarmReceiver.onReceive(context, intent.putExtra("EXTRA_MESSAGE", "Channel test"))

        // Retrieve data
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = notificationManager.getNotificationChannel(AlarmReceiver.CHANNEL_ID)

        // Assert
        assertNotNull(channel, "Notification channel should be created")
        assertEquals("Reminder Channel", channel.name, "Channel name does not match")
        assertEquals(NotificationManager.IMPORTANCE_HIGH, channel.importance, "Channel importance does not match")
    }
}