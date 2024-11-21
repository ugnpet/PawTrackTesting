package com.example.pawtrack

import android.os.AsyncTask
import com.example.pawtrack.Map.OverpassQueryTask
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.junit.Assert.*
import org.robolectric.Robolectric
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class OverpassQueryTaskTest {

    private lateinit var mockListener: OverpassQueryTask.OverpassQueryListener
    private lateinit var overpassQueryTask: OverpassQueryTask

    @Before
    fun setUp() {
        mockListener = mock(OverpassQueryTask.OverpassQueryListener::class.java)
        overpassQueryTask = OverpassQueryTask(mockListener, 123)
    }

    @Test
    @Throws(JSONException::class)
    fun testParseResponse() {
        val jsonResponse = "{\"elements\": [{\"id\": 1, \"name\": \"Place 1\"}, {\"id\": 2, \"name\": \"Place 2\"}] }"
        val result = overpassQueryTask.parseResponse(jsonResponse)

        assertEquals(2, result.size)
        assertEquals(1, result[0].getInt("id"))
        assertEquals("Place 1", result[0].getString("name"))
        assertEquals(2, result[1].getInt("id"))
        assertEquals("Place 2", result[1].getString("name"))
    }

    @Test
    @Throws(Exception::class)
    fun testDoInBackgroundAndOnPostExecute() {
        val jsonResponse = "{\"elements\": [{\"id\": 1, \"name\": \"Place 1\"}] }"

        // Mock the parseResponse method to return a predefined response
        val overpassQueryTaskSpy = spy(overpassQueryTask)
        val mockPlaces = ArrayList<JSONObject>()
        mockPlaces.add(JSONObject(jsonResponse).getJSONArray("elements").getJSONObject(0))
        doReturn(mockPlaces).`when`(overpassQueryTaskSpy).parseResponse(anyString())

        // Execute the AsyncTask synchronously using Robolectric
        val result = overpassQueryTaskSpy.doInBackground(Pair("http://example.com", 123))
        overpassQueryTaskSpy.onPostExecute(result)

        // Verify that the listener's onPlaceFound method was called with the correct parameters
        verify(mockListener, times(1)).onPlaceFound(result[0], 123)
    }
}
