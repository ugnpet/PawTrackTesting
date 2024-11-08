package com.example.pawtrack.Alarms

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class ReminderManager(private val context: Context) {

    private val sharedPreferences = context.getSharedPreferences("alarms", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val type: Type = object : TypeToken<List<AlarmItem>>() {}.type

    fun loadAlarms(): List<AlarmItem> {
        val json = sharedPreferences.getString("alarms_list", null)
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun saveAlarm(alarm: AlarmItem) {
        val alarms = loadAlarms().toMutableList()
        alarms.add(alarm)
        saveAlarmsToPrefs(alarms)
    }

    fun deleteAlarm(alarm: AlarmItem) {
        val alarms = loadAlarms().toMutableList()
        alarms.remove(alarm)
        saveAlarmsToPrefs(alarms)
    }

    fun updateAlarm(updatedAlarm: AlarmItem) {
        val alarms = loadAlarms().toMutableList()
        val index = alarms.indexOfFirst { it.id == updatedAlarm.id }
        if (index != -1) {
            alarms[index] = updatedAlarm
            saveAlarmsToPrefs(alarms)
        }
    }

    fun clearAlarms() {
        saveAlarmsToPrefs(emptyList())
    }

    private fun saveAlarmsToPrefs(alarms: List<AlarmItem>) {
        val editor = sharedPreferences.edit()
        editor.putString("alarms_list", gson.toJson(alarms))
        editor.apply()
    }
}