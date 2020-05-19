package com.freesoft.joykt.chapter9

import java.lang.IllegalStateException

fun or(a: Boolean, b: Boolean): Boolean = if (a) true else b
fun and(a: Boolean, b: Boolean): Boolean = if (a) b else false

fun getFirst(): Boolean = true
fun getSecond(): Boolean = throw IllegalStateException()


fun main() {
    println(or(a = true, b = true))
    println(or(a = true, b = false))
    println(or(a = false, b = true))
    println(or(a = false, b = false))

    println(and(a = true, b = true))
    println(and(a = true, b = false))
    println(and(a = false, b = true))
    println(and(a = false, b = false))

    //the problem with strictness
    println(getFirst() || getSecond()) // this will lazy evaluate the second operand
    println(or(getFirst(), getSecond())) // this will eager evaluate the second operand and will throw an exception
}