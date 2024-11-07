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
class StatisticsActivityTest {

    private lateinit var context: Context
    private lateinit var statisticsActivity: StatisticsActivity
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        statisticsActivity = StatisticsActivity()
        sharedPreferences = context.getSharedPreferences("PawTrackPrefs", Context.MODE_PRIVATE)
        mockWebServer = MockWebServer()
        mockWebServer.start()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testParseResponseToList() {
        val response = """
            c_b=250;d_w=2.5;
            c_b=300;d_w=3.0;
        """.trimIndent()

        val parsedList = statisticsActivity.parseResponseToList(response)

        assertEquals(2, parsedList.size)
        assertEquals("250", parsedList[0]["calories_burned"])
        assertEquals("2.5", parsedList[0]["distance_walked"])
        assertEquals("300", parsedList[1]["calories_burned"])
        assertEquals("3.0", parsedList[1]["distance_walked"])
    }

    @Test
    fun testPerformGetRequest_successfulResponse() {
        mockWebServer.enqueue(
            MockResponse().setBody("c_b=200;d_w=5.0;\nc_b=150;d_w=3.0;")
        )

        val url = mockWebServer.url("/")

        val onDataFetched = object : StatisticsActivity.OnDataFetched {
            override fun onDataFetched(parsedList: List<Map<String, String?>>) {
                assertEquals(2, parsedList.size)
                assertEquals("200", parsedList[0]["calories_burned"])
                assertEquals("5.0", parsedList[0]["distance_walked"])
            }
        }

        statisticsActivity.performGetRequest(onDataFetched, "test_pet_id")
    }
}
