package com.freesoft.joykt.chapter9

import com.freesoft.joykt.chapter5.List
import java.lang.IllegalStateException
import kotlin.random.Random

fun main() {
    val name1 = Lazy {
        println("Evaluating name1")
        "Mickey"
    }

    val name2 = Lazy {
        println("Evaluating name2")
        "Donald"
    }

    val name3 = Lazy {
        println("Evaluating name3")
        "Goofy"
    }

    val name4 = Lazy {
        println("Evaluating name4")
        throw IllegalStateException("Exception while evaluating name4")
    }

    val list1 = sequenceResult(List(name1, name2, name3))
    val list2 = sequenceResult(List(name1, name2, name3, name4))

    val defaultMessage = "No greetings when time is odd"

    val condition = Random(System.currentTimeMillis()).nextInt() % 2 == 0

    println(if (condition) list1() else defaultMessage)
    println(if (condition) list1() else defaultMessage)
    println(if (condition) list2() else defaultMessage)
    println(if (condition) list2() else defaultMessage)

}