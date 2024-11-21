package com.example.pawtrack.Alarms

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pawtrack.HomePageActivity
import com.example.pawtrack.Map.MapActivity
import com.example.pawtrack.R
import com.example.pawtrack.Tracking.StatisticsActivity
import com.example.pawtrack.Tracking.TrackingActivity
import com.example.pawtrack.User.SubscriptionActivity
import com.example.pawtrack.User.UserProfileActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ReminderSettingActivity : AppCompatActivity() {

    lateinit var scheduler: AndroidAlarmScheduler
    lateinit var adapter: ReminderAdapter
    lateinit var bottomSheet: LinearLayout
    lateinit var recyclerView: RecyclerView
    lateinit var fabAddReminder: FloatingActionButton
    private lateinit var btnSaveReminder: Button
    lateinit var etReminderName: EditText
    lateinit var cbRepeat: CheckBox
    lateinit var timePicker: TimePicker
    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
    private lateinit var alarmManager: ReminderManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder_setting)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        scheduler = AndroidAlarmScheduler(this)
        alarmManager = ReminderManager(this)

        adapter = ReminderAdapter(emptyList()) { alarm ->
            alarmManager.deleteAlarm(alarm)
            updateAlarms()
        }
        recyclerView = findViewById(R.id.recyclerViewReminders)
        fabAddReminder = findViewById(R.id.fabAddReminder)
        bottomSheet = findViewById(R.id.bottomSheet)
        btnSaveReminder = findViewById(R.id.btnSaveReminder)
        etReminderName = findViewById(R.id.etReminderName)
        cbRepeat = findViewById(R.id.cbRepeat)
        timePicker = findViewById(R.id.timePicker)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val backButton = findViewById<Button>(R.id.buttonBack)
        backButton.setOnClickListener() {
            val intent = Intent(applicationContext, UserProfileActivity::class.java)
            startActivity(intent)
            finish()
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    val intent = Intent(applicationContext, HomePageActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.map -> {
                    val intent = Intent(applicationContext, MapActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.tracking -> {
                    val intent = Intent(applicationContext, TrackingActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.statistics -> {
                    val intent = Intent(applicationContext, StatisticsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.subscription -> {
                    val intent = Intent(applicationContext, SubscriptionActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        fabAddReminder.setOnClickListener {
            if (bottomSheet.visibility == View.VISIBLE) {
                bottomSheet.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            } else {
                bottomSheet.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
        }

        btnSaveReminder.setOnClickListener {
            val message = etReminderName.text.toString()
            val repeat = cbRepeat.isChecked

            val hour = timePicker.hour
            val minute = timePicker.minute
            val time = String.format("%02d:%02d", hour, minute)

            val newAlarm = AlarmItem(
                    id = System.currentTimeMillis().toInt(),
                    message = message,
                    time = time,
                    repeat = repeat
            )

            scheduler.schedule(newAlarm)
            saveAlarm(newAlarm)
            updateAlarms()
            clearInputs()

            bottomSheet.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }

        loadAlarms()
    }
    private fun loadAlarms() {
        val alarms = alarmManager.loadAlarms()
        adapter.updateData(alarms)
    }

    private fun saveAlarm(alarm: AlarmItem) {
        alarmManager.saveAlarm(alarm)
        updateAlarms()
    }

    private fun updateAlarms() {
        val alarms = alarmManager.loadAlarms()
        adapter.updateData(alarms)
    }

    fun clearInputs() {
        etReminderName.text.clear()
        cbRepeat.isChecked = false
        timePicker.hour = 0
        timePicker.minute = 0
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted
            } else {
                // Permission denied
            }
        }
    }
}
