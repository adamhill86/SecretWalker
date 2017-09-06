package com.adamshillmusic.secretwalkerkotlin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.adamshillmusic.secretwalkerkotlin.adapter.DateViewAdapter
import com.adamshillmusic.secretwalkerkotlin.adapter.StudentViewAdapter
import com.adamshillmusic.secretwalkerkotlin.data.Record
import com.adamshillmusic.secretwalkerkotlin.database.SecretWalkerDBHandler
import kotlinx.android.synthetic.main.activity_student.*

class StudentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student)

        // Get the intent that started this activity and extract the strings
        val intent = intent
        val fullName = intent.getStringArrayExtra(StudentViewAdapter.STUDENT_NAME)
        val firstName = fullName[0] // the first element will be the first name
        val lastName = fullName[1] // the second will be the last name
        val fullNameText = "$firstName $lastName"

        // Get a reference to the secret walker table and find any records for this student
        val walkerDBHandler = SecretWalkerDBHandler(applicationContext)
        val recordsList = walkerDBHandler.getRecords(firstName, lastName)
        Log.i(TAG, recordsList.toString())

        student_name_view.text = fullNameText

        updateListView(recordsList, walkerDBHandler)
    }

    private fun updateListView(recordsList: List<Record>, walkerDBHandler: SecretWalkerDBHandler) {
        val adapter = DateViewAdapter(
                this,
                R.layout.date_card_view,
                recordsList,
                walkerDBHandler)
        dates_list_view.adapter = adapter
    }

    companion object {
        private val TAG = "StudentActivity"
    }
}
