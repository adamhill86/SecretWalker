package com.adamshillmusic.secretwalkerkotlin.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.adamshillmusic.secretwalkerkotlin.data.Student
import com.adamshillmusic.secretwalkerkotlin.data.Record
import org.apache.commons.lang3.BooleanUtils
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Adam on 9/1/2017.
 * Helper class to handle the secret walker database
 */
class SecretWalkerDBHandler(context: Context?) :
        SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION), Serializable {
    companion object {
        const val DB_NAME = "students.db"
        const val DB_VERSION = 1
        const val TABLE_NAME = "secretWalkers"
        const val KEY_ID = "id"
        const val KEY_FIRST_NAME = "firstname"
        const val KEY_LAST_NAME = "lastname"
        const val KEY_DATE = "date"
        const val KEY_SUCCEEDED = "succeeded"
        const val KEY_NOTES = "notes"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ${TABLE_NAME} (" +
                "${KEY_ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "${KEY_FIRST_NAME} TEXT, " +
                "${KEY_LAST_NAME} TEXT, " +
                "${KEY_DATE} TEXT, " +
                "${KEY_SUCCEEDED} BOOLEAN, " +
                "${KEY_NOTES} TEXT);"
        db?.execSQL(CREATE_TABLE)
    }

    fun createTable() {
        val db: SQLiteDatabase = this.writableDatabase

        val CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ${TABLE_NAME} (" +
                "${KEY_ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "${KEY_FIRST_NAME} TEXT, " +
                "${KEY_LAST_NAME} TEXT, " +
                "${KEY_DATE} TEXT, " +
                "${KEY_SUCCEEDED} BOOLEAN, " +
                "${KEY_NOTES} TEXT);"
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Drop older table if exists
        db?.execSQL("DROP TABLE IF EXISTS ${TABLE_NAME}")

        // Create the table again
        onCreate(db)
    }

    fun addRecord(student: Student, date: Date, succeeded: Boolean, notes: String) {
        val db: SQLiteDatabase = this.writableDatabase

        val dateFormat = SimpleDateFormat("M/d/yyyy")
        val dateString = dateFormat.format(date).toString()

        val values = ContentValues()
        values.put(KEY_FIRST_NAME, student.firstName)
        values.put(KEY_LAST_NAME, student.lastName)
        values.put(KEY_DATE, dateString)
        values.put(KEY_SUCCEEDED, succeeded)
        values.put(KEY_NOTES, notes)

        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun dropTable() {
        val db: SQLiteDatabase = this.writableDatabase
        db.execSQL("DROP TABLE IF EXISTS ${TABLE_NAME}")
        db.close()
    }

    fun getAllRecords(): List<Record> {
        val recordList = mutableListOf<Record>()
        val db: SQLiteDatabase = this.writableDatabase
        val query = "SELECT * FROM ${TABLE_NAME}"
        val cursor: Cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0) // id is stored in position 0
                val firstName = cursor.getString(1) // position 0: id, position 1: first name
                val lastName = cursor.getString(2)  // position 2: last name
                val student = Student(firstName, lastName)

                val dateString = cursor.getString(3) // date
                val date = SimpleDateFormat("M/d/yyyy").parse(dateString)
                val succeededInt = cursor.getInt(4) // SQLite stores booleans as 0 or 1
                val succeeded = BooleanUtils.toBoolean(succeededInt) // convert from int to bool
                val notes = cursor.getString(5) // notes

                val record = Record(student, date, succeeded, notes, id)
                recordList.add(record)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return recordList
    }

    fun getRecords(student: Student): List<Record> {
        val recordList = mutableListOf<Record>()
        val db: SQLiteDatabase = this.writableDatabase
        val query = "SELECT * FROM ${TABLE_NAME} WHERE ${KEY_FIRST_NAME}='${student.firstName}' AND ${KEY_LAST_NAME}='${student.lastName}';"
        val cursor: Cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0) // id is stored in position 0
                val dateString = cursor.getString(3) // date
                val date = SimpleDateFormat("M/d/yyyy").parse(dateString)
                val succeededInt = cursor.getInt(4) // SQLite stores booleans as 0 or 1
                val succeeded = BooleanUtils.toBoolean(succeededInt) // convert from int to bool
                val notes = cursor.getString(5) // notes

                val record = Record(student, date, succeeded, notes, id)
                recordList.add(record)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return recordList
    }

    fun getRecords(firstName: String, lastName: String): List<Record> {
        val student = Student(firstName, lastName)
        return getRecords(student)
    }

    fun updateRecord(student: Student, id: Int, date: Date, succeeded: Boolean, notes: String) {
        val db: SQLiteDatabase = this.writableDatabase
        val dateString = SimpleDateFormat("M/d/yyyy").format(date)

        val values = ContentValues()
        values.put(KEY_FIRST_NAME, student.firstName)
        values.put(KEY_LAST_NAME, student.lastName)
        values.put(KEY_DATE, dateString)
        values.put(KEY_SUCCEEDED, succeeded)
        values.put(KEY_NOTES, notes)

        val whereClause = "${KEY_FIRST_NAME}=? AND ${KEY_LAST_NAME}=? AND ${KEY_ID}=?"
        val whereArgs = arrayOf(student.firstName, student.lastName, id.toString())
        db.update(TABLE_NAME, values, whereClause, whereArgs)
    }

    /**
     * This method will remove ALL entries in the secret walker table associated with a student
     * @param student The student to be removed from the table
     */
    fun removeRecord(student: Student) {
        val whereClause = "${KEY_FIRST_NAME}=? and ${KEY_LAST_NAME}=?"
        val whereArgs = arrayOf(student.firstName, student.lastName)

        val db: SQLiteDatabase = this.writableDatabase
        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
    }
}