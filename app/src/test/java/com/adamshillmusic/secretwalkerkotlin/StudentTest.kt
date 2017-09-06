package com.adamshillmusic.secretwalkerkotlin

import com.adamshillmusic.secretwalkerkotlin.data.Student
import org.junit.Test
import java.util.*

/**
 * Created by Adam on 8/31/2017.
 */
class StudentTest {
    @Test
    fun testConstructor() {
        val adam = Student("Adam", "Hill")
        assert(adam.firstName == "Adam")
        assert(adam.lastName == "Hill")
        assert(adam.datesChosen.isEmpty())

        val john = Student("John", "Doe")
        assert(john.firstName == "John")
        assert(john.lastName == "Doe")
        assert(john.datesChosen.isEmpty())
    }

    @Test
    fun testEquals() {
        val adam = Student("Adam", "Hill")
        val adam2 = Student("Adam", "Hill")
        val adam3 = Student("Adam", "Hall")
        val john = Student("John", "Doe")

        assert(adam != john)
        assert(adam == adam2)
        assert(adam == adam)  // structural equality
        assert(adam === adam) // referential equality
        assert(adam != adam3)

        val notAStudent = 42
        assert(!adam.equals(notAStudent))
    }

    @Test
    fun testChooseSecretWalker() {
        val adam = Student("Adam", "Hill")
        val date = Date()
        adam.chooseSecretWalker(date)
        assert(adam.datesChosen.containsKey(date))
    }
}