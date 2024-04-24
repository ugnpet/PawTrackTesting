package com.example.pawtrack

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Timer
import kotlin.concurrent.timerTask


class CustomLocationOverlay(provider: IMyLocationProvider, map: MapView, private val updatePathCallback: (GeoPoint) -> Unit) : MyLocationNewOverlay(provider, map) {
    override fun onLocationChanged(location: Location?, source: IMyLocationProvider?) {
        super.onLocationChanged(location, source)
        location?.let {
            val geoPoint = GeoPoint(location.latitude, location.longitude)
            updatePathCallback(geoPoint)
        }
    }
}
data class TimedGeoPoint(
    val geoPoint: GeoPoint,
    val timestamp: Long
)
class TrackingMapActivity : AppCompatActivity() {

    private var map: MapView? = null
    private var myLocationOverlay: MyLocationNewOverlay? = null
    private var pathOverlay: Polyline? = null
    private var isTracking = false
    private var startGeoPoint: GeoPoint? = null
    private var endGeoPoint: GeoPoint? = null
    private var timer: Timer? = null
    private var startTime = 0L
    private var endTime = 0L
    private val pathPoints = mutableListOf<TimedGeoPoint>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tracking_map_layout)
        val username = intent.getStringExtra("USERNAME")
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.tracking
        setupMap()

        val petprofileButton = findViewById<FloatingActionButton>(R.id.pet_profile)
        petprofileButton.setOnClickListener(){
            val intent = Intent(applicationContext, PetProfileActivity::class.java)
            startActivity(intent)
            finish()
        }

        val profileButton = findViewById<FloatingActionButton>(R.id.floatingActionButton2)
        profileButton.setOnClickListener(){
            val intent = Intent(applicationContext, UserProfileActivity::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
            finish()
        }

        val startButton = findViewById<Button>(R.id.btnPetStart)
        startButton.setOnClickListener(){
            toggleTracking(startButton)
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    val intent = Intent(applicationContext, HomePageActivity::class.java)
                    intent.putExtra("USERNAME", username)
                    startActivity(intent)
                    true
                }
                R.id.map -> {
                    val intent = Intent(applicationContext, MapActivity::class.java)
                    intent.putExtra("USERNAME", username)
                    startActivity(intent)
                    true
                }
                R.id.tracking -> {
                    val intent = Intent(applicationContext, TrackingActivity::class.java)
                    intent.putExtra("USERNAME", username)
                    startActivity(intent)
                    true
                }
                R.id.statistics -> {
                    val intent = Intent(applicationContext, StatisticsActivity::class.java)
                    intent.putExtra("USERNAME", username)
                    startActivity(intent)
                    true
                }
                R.id.subscription -> {
                    val intent = Intent(applicationContext, SubscriptionActivity::class.java)
                    intent.putExtra("USERNAME", username)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
    private fun setupMap() {
        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))
        map = findViewById(R.id.map)
        map?.setTileSource(TileSourceFactory.MAPNIK)
        map?.setBuiltInZoomControls(false)
        map?.setMultiTouchControls(true)

        val mapController = map?.controller
        mapController?.setZoom(18.0)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            enableLocationTracking()
            setupPathOverlay()
        }
    }

    private fun enableLocationTracking() {
        val locationOverlay =
            map?.let {
                CustomLocationOverlay(GpsMyLocationProvider(this), it) { location ->
                    updatePath(location)
                }
            }
        map?.overlays?.add(locationOverlay)
        locationOverlay?.enableMyLocation()
        locationOverlay?.enableFollowLocation()
    }

    private fun setupPathOverlay() {
        pathOverlay = Polyline()
        pathOverlay?.color = ContextCompat.getColor(this, R.color.app_theme)
        map?.overlays?.add(pathOverlay)
    }

    private fun updatePath(location: GeoPoint) {
        if (isTracking) {
            val currentTime = System.currentTimeMillis()
            val timedGeoPoint = TimedGeoPoint(location, currentTime)
            pathOverlay?.addPoint(location)
            pathPoints.add(timedGeoPoint)
            map?.invalidate()
        }
    }

    private fun toggleTracking(startButton: Button) {
        isTracking = !isTracking

        if (isTracking) {
            startGeoPoint = myLocationOverlay?.myLocation
            pathOverlay?.points?.clear()
            pathPoints.clear()
            startTime = System.currentTimeMillis()
            myLocationOverlay?.enableFollowLocation()
            startLocationPolling()
        } else {
            endGeoPoint = myLocationOverlay?.myLocation
            endTime = System.currentTimeMillis()  // Save the end time
            saveTrip(startGeoPoint, endGeoPoint)
            myLocationOverlay?.disableFollowLocation()
            stopLocationPolling()
        }

        updateButtonAndIcon(startButton)
    }
    /*
    d_w = 0
    c_b = 50
    date = startTime
    active_time = (endTime - startTime) / 1000
     */
    private fun startLocationPolling() {
        val timer = Timer()
        timer.schedule(timerTask {
            runOnUiThread {
                myLocationOverlay?.myLocation?.let {
                    updatePath(it)
                }
            }
        }, 0, 10000)
    }
    private fun stopLocationPolling() {
        timer?.cancel()
        timer = null
    }
    private fun updateButtonAndIcon(startButton: Button) {
        if (isTracking) {
            startGeoPoint = myLocationOverlay?.myLocation
            val stopIcon: Drawable? = ContextCompat.getDrawable(this, R.drawable.stop_icon)
            startButton.setCompoundDrawablesWithIntrinsicBounds(stopIcon, null, null, null)
            startButton.text = getString(R.string.map_stop)
            myLocationOverlay?.disableFollowLocation()
        } else {
            startGeoPoint = myLocationOverlay?.myLocation
            val startIcon: Drawable? = ContextCompat.getDrawable(this, R.drawable.start_icon)
            startButton.setCompoundDrawablesWithIntrinsicBounds(startIcon, null, null, null)
            startButton.text = getString(R.string.map_start)
            myLocationOverlay?.enableFollowLocation()
        }
    }
    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    private fun saveTrip(start: GeoPoint?, end: GeoPoint?) {
        val JSON = "application/json; charset=utf-8".toMediaType()
        val elapsedMillis = endTime - startTime
        val hours = elapsedMillis / 3600000
        val minutes = (elapsedMillis % 3600000) / 60000
        val seconds = (elapsedMillis % 60000) / 1000
        val a_t = String.format("%02d:%02d:%02d", hours, minutes, seconds)
        val d_t = formatDate(startTime)
        val json = JSONObject()
        json.put("type", "r");
        json.put("dt", "$d_t");
        json.put("d_w", "0");
        json.put("c_b", "50");
        json.put("a_t", "$a_t");
        json.put("p", "1");

        Log.d("PostData", json.toString())
        val body: RequestBody = json.toString().toRequestBody(JSON)
        val request = Request.Builder()
            .url("https://pvp.seriouss.am")
            .post(body)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        val responseString = response.body?.string()
                        try {
                            val postJson = JSONObject()
                            postJson.put("type", "g_d")
                            postJson.put("c", pathPoints.size)
                            if (responseString != null) {
                                postJson.put("f_a_r", responseString.toInt())
                            }

                            pathPoints.forEachIndexed { index, timedGeoPoint ->
                                val pointData = JSONArray().apply {
                                    put(timedGeoPoint.geoPoint.latitude)
                                    put(timedGeoPoint.geoPoint.longitude)
                                    put(formatDate(timedGeoPoint.timestamp))
                                }
                                postJson.put((index + 1).toString(), pointData)
                            }
                            Log.d("PostData", postJson.toString())

                            val client = OkHttpClient()
                            val mediaType = "application/json; charset=utf-8".toMediaType()
                            val requestBody = postJson.toString().toRequestBody(mediaType)
                            val request = Request.Builder()
                                .url("https://pvp.seriouss.am")
                                .post(requestBody)
                                .build()

                            client.newCall(request).enqueue(object : okhttp3.Callback {
                                override fun onFailure(call: okhttp3.Call, e: IOException) {
                                    e.printStackTrace()
                                }

                                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                                    val responseBody = response.body?.string()
                                    if (response.isSuccessful)
                                    {
                                        Log.d("PostData",  "Successfully sent" + postJson.toString())
                                    }
                                    else
                                    {
                                        Log.d("PostData",  responseBody.toString())
                                    }
                                }
                            })

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        })
    }
}