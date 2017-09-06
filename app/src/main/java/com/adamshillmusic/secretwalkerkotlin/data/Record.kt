package com.adamshillmusic.secretwalkerkotlin.data

import java.util.*

/**
 * Created by Adam on 9/1/2017.
 * Represents a single row from the SecretWalker database
 * Each row contains a student, the date on which they were chosen, whether they were successful,
 * and a note about their performance
 */
data class Record(val student: Student, val date: Date,
                  var succeeded: Boolean, var notes: String, val id: Int)