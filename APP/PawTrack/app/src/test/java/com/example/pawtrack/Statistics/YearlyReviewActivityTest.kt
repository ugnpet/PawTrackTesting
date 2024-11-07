package com.example.pawtrack.Tracking

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.github.mikephil.charting.data.BarEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [android.os.Build.VERSION_CODES.O])
class YearlyReviewActivityTest {

    private lateinit var yearlyReviewActivity: YearlyReviewActivity

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        yearlyReviewActivity = YearlyReviewActivity()
    }

    @Test
    fun testParseResponse() {
        val sampleResponse = """
        id=1;date=2023-01-10;distance=5.5;calories=200;active_time=30;
        id=2;date=2023-02-15;distance=6.0;calories=250;active_time=40;
        id=3;date=2023-01-20;distance=7.0;calories=220;active_time=35;
    """.trimIndent()

        val parsedData = yearlyReviewActivity.parseResponse(sampleResponse)

        assertEquals(3, parsedData.distanceEntries.size)
        assertEquals(5.5f, parsedData.distanceEntries[0].y, 0.01f)
        assertEquals(6.0f, parsedData.distanceEntries[1].y, 0.01f)
        assertEquals(7.0f, parsedData.distanceEntries[2].y, 0.01f)

        assertEquals(3, parsedData.caloriesEntries.size)
        assertEquals(200f, parsedData.caloriesEntries[0].y, 0.01f)
        assertEquals(250f, parsedData.caloriesEntries[1].y, 0.01f)
        assertEquals(220f, parsedData.caloriesEntries[2].y, 0.01f)

        assertEquals(2, parsedData.activeDaysEntries.size)

        val januaryActiveDays = parsedData.activeDaysEntries.firstOrNull { it.x == 0f }
        assertNotNull("January active days entry should not be null", januaryActiveDays)
        assertEquals(2f, januaryActiveDays?.y ?: 0f, 0.01f)

        val februaryActiveDays = parsedData.activeDaysEntries.firstOrNull { it.x == 1f }
        assertNotNull("February active days entry should not be null", februaryActiveDays)
        assertEquals(1f, februaryActiveDays?.y ?: 0f, 0.01f)
    }


    @Test
    fun testGetMonths() {
        val expectedMonths = arrayOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )
        assertEquals(expectedMonths.toList(), yearlyReviewActivity.getMonths().toList())
    }
}
