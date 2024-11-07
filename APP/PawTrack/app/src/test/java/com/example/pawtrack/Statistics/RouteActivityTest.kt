package com.example.pawtrack.Tracking

import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.test.core.app.ApplicationProvider
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O])
class RouteActivityTest {

    private lateinit var context: Context
    private lateinit var routeActivity: RouteActivity
    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        routeActivity = RouteActivity()
        mockWebServer = MockWebServer()
        mockWebServer.start()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testParsePoints() {
        val jsonString = "lat=54.6872;lon=25.2797\nlat=54.6982;lon=25.2989\n"
        val points = routeActivity.parsePoints(jsonString)

        assertEquals(2, points.size)
        assertEquals(54.6872, points[0].latitude, 0.0001)
        assertEquals(25.2797, points[0].longitude, 0.0001)
    }

    @Test
    fun testParseResponse() {
        val response = "id=1;date=2024-11-01;distance=5.0;calories=200;active_time=30\n"
        val parsedData = routeActivity.parseResponse(response)

        assertEquals(1, parsedData.size)
        assertEquals("1", parsedData[0]["id"])
        assertEquals("2024-11-01", parsedData[0]["date"])
        assertEquals("5.0", parsedData[0]["distance"])
        assertEquals("200", parsedData[0]["calories"])
        assertEquals("30", parsedData[0]["active_time"])
    }

    @Test
    fun testPerformGetRequest_withMissingPetId() {
        routeActivity.performGetRequest("test_user", null)
    }

    @Test
    fun testPerformGetRequest_successfulResponse() {
        mockWebServer.enqueue(MockResponse().setBody("id=1;date=2024-11-01;distance=5.0;calories=200;active_time=30\n"))

        val url = HttpUrl.Builder()
            .scheme("http")
            .host(mockWebServer.hostName)
            .port(mockWebServer.port)
            .addQueryParameter("type", "g_a_r")
            .addQueryParameter("p", "test_pet_id")
            .build()

        val intent = Intent(context, RouteActivity::class.java)
        routeActivity.performGetRequest("test_user", "test_pet_id")
    }
}
