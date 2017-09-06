package com.adamshillmusic.secretwalkerkotlin.data

import org.apache.commons.lang3.tuple.MutablePair
import java.io.Serializable
import java.util.*

/**
 * Created by Adam on 9/4/2017.
 */
data class SaveData(val secretWalker: MutablePair<Student, Date>?,
                    val studentList: MutableList<Student>?)
    : Serializable {
    constructor() : this(null, null)
}