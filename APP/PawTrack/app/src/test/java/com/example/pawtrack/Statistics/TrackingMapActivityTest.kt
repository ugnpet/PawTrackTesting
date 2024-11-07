package com.example.pawtrack.Tracking

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.osmdroid.util.GeoPoint
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [android.os.Build.VERSION_CODES.O])
class TrackingMapActivityTest {

    private lateinit var context: Context
    private lateinit var trackingMapActivity: TrackingMapActivity

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        trackingMapActivity = TrackingMapActivity()
    }

    @Test
    fun testHaversineDistanceCalculation() {
        val start = GeoPoint(54.6872, 25.2797)
        val end = GeoPoint(54.6982, 25.2989)

        val distance = trackingMapActivity.haversine(start, end)

        assertTrue(distance > 0)
        assertEquals(1.74, distance, 0.1)
    }

    @Test
    fun testFormatDate() {
        val timestamp = 1697036632000L
        val formattedDate = trackingMapActivity.formatDate(timestamp)
        val expectedDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val expectedDate = expectedDateFormat.format(timestamp)

        assertEquals(expectedDate, formattedDate)
    }
}
