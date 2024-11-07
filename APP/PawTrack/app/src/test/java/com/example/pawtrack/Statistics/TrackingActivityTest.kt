package com.example.pawtrack.Tracking

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [android.os.Build.VERSION_CODES.O])
class TrackingActivityTest {

    private lateinit var context: Context
    private lateinit var trackingActivity: TrackingActivity
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        trackingActivity = TrackingActivity()
        sharedPreferences = context.getSharedPreferences("PawTrackPrefs", Context.MODE_PRIVATE)
        mockWebServer = MockWebServer()
        mockWebServer.start()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testParseStatisticsResponseToList() {
        val response = """
            c_b=300;d_w=5.0;
            c_b=400;d_w=6.0;
        """.trimIndent()

        val parsedList = trackingActivity.parseStatisticsResponseToList(response)

        assertEquals(2, parsedList.size)
        assertEquals("300", parsedList[0]["calories_burned"])
        assertEquals("5.0", parsedList[0]["distance_walked"])
        assertEquals("400", parsedList[1]["calories_burned"])
        assertEquals("6.0", parsedList[1]["distance_walked"])
    }

    @Test
    fun testParseResponse() {
        val response = """
            id=1;date=2024-11-01;distance=5.0;calories=200;active_time=30;
            id=2;date=2024-11-02;distance=6.0;calories=250;active_time=45;
        """.trimIndent()

        val parsedActivities = trackingActivity.parseResponse(response)

        assertEquals(2, parsedActivities.size)
        assertEquals("1", parsedActivities[0]["id"])
        assertEquals("2024-11-01", parsedActivities[0]["date"])
        assertEquals("5.0", parsedActivities[0]["distance"])
        assertEquals("200", parsedActivities[0]["calories"])
        assertEquals("30", parsedActivities[0]["active_time"])
    }
}
