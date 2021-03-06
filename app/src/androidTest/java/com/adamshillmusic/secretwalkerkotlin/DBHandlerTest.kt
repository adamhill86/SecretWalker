package com.adamshillmusic.secretwalkerkotlin

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.adamshillmusic.secretwalkerkotlin.database.StudentDBHandler

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class DBHandlerTest {
    @Test
    fun testDB() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        val db = StudentDBHandler(appContext)
        assertEquals(db.databaseName, "students.db")
    }
}
