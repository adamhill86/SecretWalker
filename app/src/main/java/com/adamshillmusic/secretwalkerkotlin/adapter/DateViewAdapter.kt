package com.adamshillmusic.secretwalkerkotlin.adapter

import android.app.AlertDialog
import android.content.Context
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.adamshillmusic.secretwalkerkotlin.R
import com.adamshillmusic.secretwalkerkotlin.data.Record
import com.adamshillmusic.secretwalkerkotlin.database.SecretWalkerDBHandler
import java.text.SimpleDateFormat

/**
 * Created by Adam on 9/4/2017.
 */
class DateViewAdapter(context: Context, val resource: Int,
                      val recordList:List<Record>,
                      private val walkerDBHandler: SecretWalkerDBHandler) :
        ArrayAdapter<Record>(context, resource, recordList) {
    val layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder: ViewHolder
        val retView: View

        val record = getItem(position)

        val dateString = SimpleDateFormat("M/d/yyyy").format(record.date)

        if (convertView == null) {
            retView = layoutInflater.inflate(resource, null)
            holder = ViewHolder()

            holder.date = retView.findViewById(R.id.dateCardDate)
            holder.succeeded = retView.findViewById(R.id.dateCardSuccess)
            holder.notes = retView.findViewById(R.id.dateCardNotes)
            holder.colorStripe = retView.findViewById(R.id.colorStripe)

            retView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
            retView = convertView
        }

        holder.date?.text = dateString

        if (record.succeeded) {
            holder.succeeded?.text = context.getString(R.string.succeeded)
            // also set the color stripe to green
            holder.colorStripe?.setBackgroundColor(ContextCompat.getColor(context, R.color.successGreen))
        }
        else {
            holder.succeeded?.text = context.getString(R.string.unsuccessful)
            // also set the color stripe to red
            holder.colorStripe?.setBackgroundColor(ContextCompat.getColor(context, R.color.dangerRed))
        }

        holder.notes?.text = record.notes

        retView.setOnClickListener { view ->
            val myAdapter = this

            val builder = AlertDialog.Builder(context)
            val editViewLayoutInflater: LayoutInflater = LayoutInflater.from(context)
            val dialogView = editViewLayoutInflater.inflate(R.layout.edit_view, null)

            val dialogTextView = dialogView.findViewById<TextView>(R.id.editStudentNameView)
            val checkBox = dialogView.findViewById<CheckBox>(R.id.checkBox)
            val notesEditText = dialogView.findViewById<EditText>(R.id.notesEditText)

            //val sb = StringBuilder()
            //sb.append(record.student).append(" - ").append(dateString)

            // convert from a String to an Editable
            // this is necessary to fill the notes edit text
            val editable: Editable = SpannableStringBuilder(record.notes)

            dialogTextView.text = dateString
            checkBox.isChecked = record.succeeded
            notesEditText.text = editable

            builder.run {
                setTitle(context.getString(R.string.edit_student_note))
                setView(dialogView)

                setPositiveButton(R.string.ok) { dialogInterface, i ->
                    val succeeded = checkBox.isChecked
                    val notes = notesEditText.text.toString()

                    // update the db
                    walkerDBHandler.updateRecord(record.student, record.id, record.date, succeeded, notes)

                    // update the local resource so it displays properly
                    record.succeeded = succeeded
                    record.notes = notes

                    // tell the adapter that the data set has changed so it re-renders the list
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
        var date: TextView? = null
        var succeeded: TextView? = null
        var notes: TextView? = null
        var colorStripe: ImageView? = null
    }
}