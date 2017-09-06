package com.adamshillmusic.secretwalkerkotlin.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.adamshillmusic.secretwalkerkotlin.data.Student
import java.io.Serializable

/**
 * Created by Adam on 8/31/2017.
 * Helper class to handle the students SQLite database
 */
class StudentDBHandler(context: Context?) :
        SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION), Serializable {

    // these are essentially the equivalent of private static final constants from what I understand
    companion object {
        const val DB_NAME = "students.db"
        const val DB_VERSION = 1
        const val TABLE_NAME = "students"
        const val KEY_ID = "id"
        const val KEY_FIRST_NAME = "firstname"
        const val KEY_LAST_NAME = "lastname"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ${TABLE_NAME} " +
                "(${KEY_ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "${KEY_FIRST_NAME} TEXT, " +
                "${KEY_LAST_NAME} TEXT);"
        db?.execSQL(CREATE_TABLE)
        Log.i("StudentDBHandler", "Inside onCreate")
    }

    fun createTable() {
        val db: SQLiteDatabase = this.writableDatabase as SQLiteDatabase
        val CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ${TABLE_NAME} " +
                "(${KEY_ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "${KEY_FIRST_NAME} TEXT, " +
                "${KEY_LAST_NAME} TEXT);"
        db.execSQL(CREATE_TABLE)
        Log.i("StudentDBHandler", "Inside createTable")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Drop older table if exists
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")

        // Create the table again
        onCreate(db)
    }

    // Add new row
    fun addStudent(student: Student) {
        val db: SQLiteDatabase = this.writableDatabase

        val values = ContentValues()
        values.put(KEY_FIRST_NAME, student.firstName)
        values.put(KEY_LAST_NAME, student.lastName)

        // Insert row
        db.insert(TABLE_NAME, null, values)
        db.close() // close the database
    }

    // Get all students
    fun getAllStudents(): MutableList<Student> {
        val studentList = mutableListOf<Student>()

        val query = "SELECT * FROM ${TABLE_NAME}"

        val db: SQLiteDatabase = this.writableDatabase
        val cursor: Cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val firstName = cursor.getString(1) // position 0: id, position 1: first name
                val lastName = cursor.getString(2)  // position 2: last name
                val student = Student(firstName, lastName)
                studentList.add(student)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return studentList
    }

    // Remove student
    fun removeStudent(student: Student) {
        val whereClause = "${KEY_FIRST_NAME}=? and ${KEY_LAST_NAME}=?"
        val whereArgs = arrayOf(student.firstName, student.lastName)

        val db: SQLiteDatabase = this.writableDatabase
        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
    }

    // Only for testing purposes
    fun dropTable() {
        val db: SQLiteDatabase = this.writableDatabase
        db.execSQL("DROP TABLE IF EXISTS ${TABLE_NAME}")
        db.close()
    }
}