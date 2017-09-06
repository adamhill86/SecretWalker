package com.adamshillmusic.secretwalkerkotlin

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.adamshillmusic.secretwalkerkotlin.adapter.StudentViewAdapter
import com.adamshillmusic.secretwalkerkotlin.database.StudentDBHandler
import com.adamshillmusic.secretwalkerkotlin.data.SaveData
import com.adamshillmusic.secretwalkerkotlin.data.Student

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.apache.commons.lang3.tuple.MutablePair
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.*

class MainActivity : AppCompatActivity() {
    private val context: Context = this
    private var secretWalkerPicker: SecretWalkerPicker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val student = Student("Adam", "Hill")
        val date = Date()

        val dbHandler = StudentDBHandler(this)
        secretWalkerPicker = SecretWalkerPicker(dbHandler.getAllStudents(), dbHandler, this)
        loadSecretWalkerPicker()

        dbHandler.createTable() // ensure the table's creation

        updateMyAdapter(dbHandler)

        pickSecretWalkerBtn.setOnClickListener {
            pickSecretWalker()
            updateMyAdapter(dbHandler)
        }



        fab.setOnClickListener { view ->
            val builder = AlertDialog.Builder(context)
            val layoutInflater = LayoutInflater.from(context)
            val dialogView = layoutInflater.inflate(R.layout.new_student_dialog, null)

            val firstNameTextView = dialogView.findViewById<TextView>(R.id.dialogFirstName)
            val lastNameTextView = dialogView.findViewById<TextView>(R.id.dialogLastName)

            builder.run {
                setTitle(R.string.dialog_title)
                setView(dialogView)
                setPositiveButton(R.string.ok) { dialogInterface, i ->
                    val firstName = firstNameTextView.text.toString()
                    val lastName = lastNameTextView.text.toString()

                    if (firstName.isNotEmpty() && lastName.isNotEmpty()) {
                        val newStudent = Student(firstName, lastName)
                        dbHandler.addStudent(newStudent) // put the new student in the db

                        Toast.makeText(applicationContext,
                                "$newStudent added to class",
                                Toast.LENGTH_SHORT).show()

                        // Add it the picker so they can be picked as the secret walker,
                        // otherwise they won't be available as a selection until the entire
                        // rest of the class has been chosen
                        val picker = secretWalkerPicker as SecretWalkerPicker
                        picker.addStudent(newStudent)
                        updateMyAdapter(dbHandler)
                    }
                }
                setNegativeButton(R.string.cancel) { dialogInterface, i ->
                    dialogInterface.cancel()
                }
            }

            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
        }
    }

    /*
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
    */
    override fun onStop() {
        // call the superclass method first
        super.onStop()

        Log.i(TAG, "Hello from onStop()")

        // write SecretWalkerPicker to a file so it can be stored and retrieved later
        saveSecretWalkerPicker()
    }

    override fun onPause() {
        super.onPause()

        Log.i(TAG, "Hello from onPause()")
        saveSecretWalkerPicker()
    }

    override fun onResume() {
        super.onResume()

        Log.i(TAG, "Hello from onResume()")
        loadSecretWalkerPicker()
        val tempDbHandler = StudentDBHandler(context)
        updateMyAdapter(tempDbHandler)
    }

    override fun onRestart() {
        super.onRestart()

        Log.i(TAG, "Hello from onRestart()")
        loadSecretWalkerPicker()
        val tempDbHandler = StudentDBHandler(context)
        updateMyAdapter(tempDbHandler)
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.i(TAG, "Hello from onDestroy()")
        saveSecretWalkerPicker()
    }

    private fun updateMyAdapter(dbHandler: StudentDBHandler) {
        val students = dbHandler.getAllStudents()

        for (student in students) {
            if (student == secretWalkerPicker?.currentSecretWalker?.key) {
                Log.i("updateListView", "$student is walker")
            }
        }

        val arrayAdapter = StudentViewAdapter(
                this,
                R.layout.student_card_layout,
                students,
                dbHandler,
                secretWalkerPicker as SecretWalkerPicker
        )
        listView.adapter = arrayAdapter
    }

    private fun pickSecretWalker() {
        val walker = secretWalkerPicker?.chooseSecretWalker()
        if (walker != null) {
            Toast.makeText(this, "$walker is the secret walker", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * This method will store the data from secretWalkerPicker to be loaded later
     */
    private fun saveSecretWalkerPicker() {
        //secretWalkerPicker?.save()
        val walker = secretWalkerPicker?.currentSecretWalker
        val studentList = secretWalkerPicker?.students
        val saveData = SaveData(walker, studentList)

        try {
            val fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE)
            val objectOutputStream = ObjectOutputStream(fileOutputStream)
            objectOutputStream.writeObject(saveData)
            objectOutputStream.close()
            fileOutputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun loadSecretWalkerPicker() {
        //secretWalkerPicker?.load()
        var saveData = SaveData()

        try {
            val fileInputStream = context.openFileInput(filename)
            val objectInputStream = ObjectInputStream(fileInputStream)
            saveData = objectInputStream.readObject() as SaveData
            objectInputStream.close()
            fileInputStream.close()

            secretWalkerPicker?.currentSecretWalker = saveData.secretWalker as MutablePair<Student, Date>
            secretWalkerPicker?.students = saveData.studentList as MutableList<Student>
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun populateDatabase(dbHandler: StudentDBHandler) {
        val adam = Student("Adam", "Hill")
        val tara = Student("Tara", "Uhrich")
        val aaron = Student("Aaron", "Rodgers")
        val lovecraft = Student("H.P.", "Lovecraft")
        val axl = Student("Axl", "Rose")
        val stephen = Student("Stephen", "Curry")

        dbHandler.addStudent(adam)
        dbHandler.addStudent(tara)
        dbHandler.addStudent(aaron)
        dbHandler.addStudent(lovecraft)
        dbHandler.addStudent(axl)
        dbHandler.addStudent(stephen)
    }

    companion object {
        const val filename = "secretWalker"
        const val TAG = "MainActivity"
    }
}
