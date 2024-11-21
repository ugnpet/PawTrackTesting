package com.example.pawtrack

import com.example.pawtrack.Map.OverpassQueryTask
import org.json.JSONException
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
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

        // Validate the size of the parsed list
        assertEquals(2, result.size)

        // Validate the first element
        assertEquals(1, result[0].getInt("id"))
        assertEquals("Place 1", result[0].getString("name"))

        // Validate the second element
        assertEquals(2, result[1].getInt("id"))
        assertEquals("Place 2", result[1].getString("name"))
    }

    @Test
    @Throws(Exception::class)
    fun testDoInBackground() {
        val jsonResponse = "{\"elements\": [{\"id\": 1, \"name\": \"Place 1\"}] }"

        // Mock the parseResponse method to return a predefined response
        val overpassQueryTaskSpy = spy(overpassQueryTask)
        val mockPlaces = ArrayList<JSONObject>()
        mockPlaces.add(JSONObject(jsonResponse).getJSONArray("elements").getJSONObject(0))
        doReturn(mockPlaces).`when`(overpassQueryTaskSpy).parseResponse(anyString())

        // Execute the doInBackground method
        val result = overpassQueryTaskSpy.doInBackground(Pair("http://example.com", 123))

        // Validate the result
        assertEquals(1, result.size)
        assertEquals(1, result[0].getInt("id"))
        assertEquals("Place 1", result[0].getString("name"))
    }

    @Test
    @Throws(Exception::class)
    fun testOnPostExecute() {
        val jsonResponse = "{\"elements\": [{\"id\": 1, \"name\": \"Place 1\"}] }"

        // Mock the parseResponse method to return a predefined response
        val overpassQueryTaskSpy = spy(overpassQueryTask)
        val mockPlaces = ArrayList<JSONObject>()
        mockPlaces.add(JSONObject(jsonResponse).getJSONArray("elements").getJSONObject(0))
        doReturn(mockPlaces).`when`(overpassQueryTaskSpy).parseResponse(anyString())

        // Execute onPostExecute with a predefined result
        overpassQueryTaskSpy.onPostExecute(mockPlaces)

        // Verify that the listener's onPlaceFound method was called with the correct parameters
        verify(mockListener, times(1)).onPlaceFound(mockPlaces[0], 123)
    }
}