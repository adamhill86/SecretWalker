package com.adamshillmusic.secretwalkerkotlin.data

import java.io.Serializable

/**
 * Created by Adam on 8/31/2017.
 */

data class Student(val firstName: String, val lastName: String) : Serializable {
    // no-args constructor required for Serializable
    // sets the student's first and last names to an empty string
    constructor() : this("", "")

    fun getFullName() = "$firstName $lastName"

    override fun toString(): String = "$firstName $lastName"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Student

        if (firstName != other.firstName) return false
        if (lastName != other.lastName) return false

        return true
    }

    override fun hashCode(): Int {
        var hash = 337 // arbitrary prime number
        hash = 347 * hash + firstName.hashCode()
        hash = 347 * hash + lastName.hashCode()

        return hash
    }
}