package com.adamshillmusic.secretwalkerkotlin

import android.content.Context
import android.util.Log
import com.adamshillmusic.secretwalkerkotlin.data.Student
import com.adamshillmusic.secretwalkerkotlin.database.SecretWalkerDBHandler
import com.adamshillmusic.secretwalkerkotlin.database.StudentDBHandler
import org.apache.commons.lang3.tuple.MutablePair
import java.io.Serializable
import java.util.*

/**
 * Created by Adam on 9/1/2017.
 */
class SecretWalkerPicker(var students: MutableList<Student>,
                         private val dbHandler: StudentDBHandler,
                         context: Context) : Serializable {
    var currentSecretWalker = MutablePair<Student, Date>()
    private val walkerDBHandler = SecretWalkerDBHandler(context)
    //private val pickerDBHandler = PickerDBHandler(context)

    init {
        walkerDBHandler.createTable()
        //pickerDBHandler.createTable()
    }

    fun chooseSecretWalker() : Student? {
        if (students.isEmpty()) {
            Log.i("SecretWalkerPicker", "Empty, refreshing")
            copyStudentsFromDB()
        }

        // this second check for isEmpty() will only execute if there are no students in the database
        if (students.isEmpty()) {
            Log.i("SecretWalkerPicker", "No students in the database")
            return null
        }

        val date = Date()
        val random = Random()
        val index = random.nextInt(students.size)

        val walker = students[index]
        students.removeAt(index)

        Log.i("SecretWalkerPicker", students.toString())
        Log.i("SecretWalkerPicker", "Walker: $walker, date: $date")

        //currentSecretWalker.left?.isSecretWalker = false // reset flag on old walker

        currentSecretWalker.left = walker
        currentSecretWalker.right = date

        //walker.chooseSecretWalker(date)
        //walker.isSecretWalker = true

        // add walker to the database
        walkerDBHandler.addRecord(walker, date, true, "")

        return walker
    }

    fun addStudent(student: Student) {
        students.add(student)
        //pickerDBHandler.addRecord(student, null, false)
    }

    fun removeStudent(student: Student) {
        if (students.contains(student)) {
            students.remove(student)
            // also remove the student from the database
            walkerDBHandler.removeRecord(student)
        }

        // if the student is the current walker, reset current walker
        if (currentSecretWalker.key == student) {
            currentSecretWalker.left = null
            currentSecretWalker.right = null
        }
    }

    /**
     * Method used to save the secret walker and the students list into the database
     */
    /*
    fun save() {
        // erase previous entries to make sure there aren't multiple secret walkers
        pickerDBHandler.dropRecreateTable()

        // Store the current secret walker if there is one
        if (currentSecretWalker.key != null && currentSecretWalker.value != null) {
            Log.i(TAG, "Adding ${currentSecretWalker.key.getFullName()} on ${currentSecretWalker.value}")
            pickerDBHandler.addRecord(currentSecretWalker.key, currentSecretWalker.value, true)
        }

        // Add the remaining students from the list to the db
        if (!students.isEmpty()) {
            students.forEach {
                pickerDBHandler.addRecord(it, null, false)
                Log.i(TAG, "Adding ${it.getFullName()}")
            }
        }
    }

    /**
     * Method used to load secret walker data from the database.
     * Restores the current secret walker if there is one and fills out the student list
     */
    fun load() {
        // get secret walker
        currentSecretWalker.left = pickerDBHandler.getSecretWalker()?.left
        Log.i(TAG, currentSecretWalker.left?.getFullName() + " is walker? ${currentSecretWalker.left?.isSecretWalker}")

        // fill out students list
        val studentList = pickerDBHandler.getStudentsExceptSecretWalker()

        students.removeAll(students) // empty the list
        studentList?.forEach { students.add(it) }

        Log.i("SecretWalkerPicker", "Loaded students: ${students.toString()}")
    }*/

    private fun copyStudentsFromDB() {
        dbHandler.getAllStudents().forEach { students.add(it) }
    }

    companion object {
        const val TAG = "SecretWalkerPicker"
    }
}