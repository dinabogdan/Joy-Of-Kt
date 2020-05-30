package com.freesoft.joykt.chapter14

import com.freesoft.joykt.chapter7.Result


// partial function because x=0 it's not a valid input
fun partialInverse(x: Int): Double = 1.0 / x

// partial function, but this time the assertion guards the usage of the invalid input value
fun partialValidatedInverse(x: Int): Double {
    assert(x == 0)
    return 1.0 / x
}

fun totalInverse(x: Int): Result<Double> = when (x) {
    0 -> Result.failure("div. By 0")
    else -> Result(1.0 / x)
}

fun isPositive(i: Int?): Boolean = i != null && i > 0

fun isValidName(name: String?): Boolean = name != null && name[0].toInt() >= 65 && name[0].toInt() <= 91

fun assertPositive(i: Int, message: String): Result<Int> = Result.of(::isPositive, i, message)

fun assertValidName(name: String, message: String): Result<String> = Result.of(::isValidName, name, message)

class Person private constructor(
        val id: Int,
        val firstName: String,
        val lastName: String
) {

    companion object {

        fun of(id: Int, firstName: String, lastName: String) =
                assertPositive(id, "Negative id").flatMap { validId ->
                    assertValidName(firstName, "Invalid first name").flatMap { validFirstName ->
                        assertValidName(lastName, "Invalid last name").map { validtLastName ->
                            Person(validId, validFirstName, validtLastName)
                        }
                    }
                }

//        fun of(id: Int, firstName: String, lastName: String) =
//                Result.of(::isPositive, id, "Negative id").flatMap { validId ->
//                    Result.of(::isValidName, firstName, "Invalid fist name").flatMap { validFirstName ->
//                        Result.of(::isValidName, lastName, "Invalid last name").map { validLastName ->
//                            Person(validId, validFirstName, validLastName)
//                        }
//                    }
//                }

//        operator fun invoke(id: Int?, firstName: String?, lastName: String?): Person =
//                Person(id, firstName, lastName)
    }

}