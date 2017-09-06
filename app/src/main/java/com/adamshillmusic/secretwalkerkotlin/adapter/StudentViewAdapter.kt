package com.adamshillmusic.secretwalkerkotlin.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.adamshillmusic.secretwalkerkotlin.R
import com.adamshillmusic.secretwalkerkotlin.SecretWalkerPicker
import com.adamshillmusic.secretwalkerkotlin.data.Student
import com.adamshillmusic.secretwalkerkotlin.StudentActivity
import com.adamshillmusic.secretwalkerkotlin.database.StudentDBHandler

/**
 * Created by Adam on 9/1/2017.
 */
class StudentViewAdapter(context: Context?, val resource: Int, val studentList: MutableList<Student>?,
                         val dbHandler: StudentDBHandler, val secretWalkerPicker: SecretWalkerPicker) :
        ArrayAdapter<Student>(context, resource, studentList) {

    var layoutInflater: LayoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder: ViewHolder
        val retView: View

        val student = getItem(position)

        if (convertView == null) {
            retView = layoutInflater.inflate(resource, null)
            holder = ViewHolder()

            holder.firstName = retView.findViewById(R.id.firstNameView)
            holder.lastName = retView.findViewById(R.id.lastNameView)
            holder.starView = retView.findViewById(R.id.starView)
            holder.removeBtn = retView.findViewById(R.id.removeBtn)

            retView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
            retView = convertView
        }

        holder.firstName?.text = student.firstName
        holder.lastName?.text = student.lastName

        if (secretWalkerPicker.currentSecretWalker.key != student) {
            // disable the star if a student isn't the secret walker
            // we only want the star visible if the student is the secret walker
            holder.starView?.visibility = View.INVISIBLE
        } else {
            holder.starView?.visibility = View.VISIBLE
        }

        retView.setOnClickListener { view ->
            //Toast.makeText(context, "Clicked on item # $position", Toast.LENGTH_SHORT).show()
            val intent = newIntent(context, studentList!![position])
            context.startActivity(intent)
        }

        holder.removeBtn?.setOnClickListener { view ->
            // store a reference to this object so we can call notifyDataSetChanged()
            // if we delete a student
            val myAdapter = this

            val icon = ContextCompat.getDrawable(context, R.drawable.ic_warning_black_36dp)
            icon.setTint(ContextCompat.getColor(context, R.color.warningOrange))

            val builder = AlertDialog.Builder(context)

            builder.run {
                setTitle(R.string.remove_dialog_title)
                setMessage(R.string.confirm_remove)
                setIcon(icon)

                setPositiveButton(R.string.remove) { dialogInterface, i ->
                    val removedStudent = studentList!![position]
                    dbHandler.removeStudent(removedStudent) // remove the student from the db

                    // Also remove them from the secret walker list
                    // This ensures they won't be chosen to be the secret walker
                    // and we won't have to wait until the whole secret walker student list is empty
                    // This method also removes the student from the secret walker database
                    secretWalkerPicker.removeStudent(removedStudent)

                    // finally, remove them from the internal ArrayList
                    studentList.removeAt(position)

                    Toast.makeText(context,
                            "$removedStudent has been removed from the class",
                            Toast.LENGTH_LONG).show()
                    myAdapter.notifyDataSetChanged()
                }

                setNegativeButton(R.string.cancel) { dialogInterface, i ->
                    dialogInterface.cancel()
                }
            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
        }

        return retView
    }

    internal class ViewHolder {
        var firstName: TextView? = null
        var lastName: TextView? = null
        var starView: ImageView? = null
        var removeBtn: ImageButton? = null
    }

    companion object {
        val STUDENT_NAME = "studentName"

        fun newIntent(context: Context, student: Student): Intent {
            val intent = Intent(context, StudentActivity::class.java)
            val names = arrayOf(student.firstName, student.lastName)
            intent.putExtra(STUDENT_NAME, names)
            return intent
        }
    }

}