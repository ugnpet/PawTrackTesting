package com.example.pawtrack.Alarms

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.example.pawtrack.Alarms.AlarmItem
import com.example.pawtrack.Alarms.ReminderManager
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O])
class ReminderManagerTest {

    private lateinit var context: Context
    private lateinit var reminderManager: ReminderManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        reminderManager = ReminderManager(context)
        reminderManager.clearAlarms()
    }

    @After
    fun tearDown() {
        reminderManager.clearAlarms()
    }

    @Test
    fun testSaveAlarmSingle() {
        val alarm = AlarmItem(id = 1, message = "Test Alarm", time = "08:00", repeat = false)
        reminderManager.saveAlarm(alarm)

        val savedAlarms = reminderManager.loadAlarms()
        assertEquals(1, savedAlarms.size)
        assertEquals(alarm, savedAlarms[0])
    }

    @Test
    fun testLoadAlarmsNoAlarms() {
        val savedAlarms = reminderManager.loadAlarms()
        assertEquals(0, savedAlarms.size)
    }

    @Test
    fun testSaveMultipleAlarms() {
        val alarm1 = AlarmItem(id = 1, message = "Alarm 1", time = "08:00", repeat = false)
        val alarm2 = AlarmItem(id = 2, message = "Alarm 2", time = "09:00", repeat = true)

        reminderManager.saveAlarm(alarm1)
        reminderManager.saveAlarm(alarm2)

        val savedAlarms = reminderManager.loadAlarms()
        assertEquals(2, savedAlarms.size)
        assertEquals(alarm1, savedAlarms[0])
        assertEquals(alarm2, savedAlarms[1])
    }

    @Test
    fun testDeleteAlarm() {
        val alarm = AlarmItem(id = 1, message = "Alarm to delete", time = "07:00", repeat = false)
        reminderManager.saveAlarm(alarm)

        reminderManager.deleteAlarm(alarm)

        val remainingAlarms = reminderManager.loadAlarms()
        assertEquals(0, remainingAlarms.size)
    }

    @Test
    fun testDeleteNonExistentAlarm() {
        val alarm = AlarmItem(id = 1, message = "Non-existent alarm", time = "07:00", repeat = false)

        reminderManager.deleteAlarm(alarm)

        val remainingAlarms = reminderManager.loadAlarms()
        assertEquals(0, remainingAlarms.size)
    }

    @Test
    fun testUpdateExistingAlarm() {
        val alarm = AlarmItem(id = 1, message = "Initial Alarm", time = "08:00", repeat = false)
        reminderManager.saveAlarm(alarm)

        val updatedAlarm = AlarmItem(id = 1, message = "Updated Alarm", time = "09:00", repeat = true)
        reminderManager.updateAlarm(updatedAlarm)

        val savedAlarms = reminderManager.loadAlarms()
        assertEquals(1, savedAlarms.size)
        assertEquals(updatedAlarm, savedAlarms[0])
    }

    @Test
    fun testUpdateNonExistentAlarm() {
        val alarm = AlarmItem(id = 1, message = "Non-existent Alarm", time = "08:00", repeat = false)

        reminderManager.updateAlarm(alarm)

        val savedAlarms = reminderManager.loadAlarms()
        assertEquals(0, savedAlarms.size)
    }

    @Test
    fun testClearAlarms() {
        val alarm1 = AlarmItem(id = 1, message = "Alarm 1", time = "08:00", repeat = false)
        val alarm2 = AlarmItem(id = 2, message = "Alarm 2", time = "09:00", repeat = true)

        reminderManager.saveAlarm(alarm1)
        reminderManager.saveAlarm(alarm2)

        reminderManager.clearAlarms()

        val savedAlarms = reminderManager.loadAlarms()
        assertEquals(0, savedAlarms.size)
    }

    @Test
    fun testSaveAndDeleteMultipleAlarms() {
        val alarm1 = AlarmItem(id = 1, message = "Alarm 1", time = "08:00", repeat = false)
        val alarm2 = AlarmItem(id = 2, message = "Alarm 2", time = "09:00", repeat = true)
        val alarm3 = AlarmItem(id = 3, message = "Alarm 3", time = "10:00", repeat = false)

        reminderManager.saveAlarm(alarm1)
        reminderManager.saveAlarm(alarm2)
        reminderManager.saveAlarm(alarm3)

        reminderManager.deleteAlarm(alarm2)

        val remainingAlarms = reminderManager.loadAlarms()
        assertEquals(2, remainingAlarms.size)
        assertEquals(alarm1, remainingAlarms[0])
        assertEquals(alarm3, remainingAlarms[1])
    }
}