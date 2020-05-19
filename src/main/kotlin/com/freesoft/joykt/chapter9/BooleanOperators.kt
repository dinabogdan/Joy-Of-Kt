package com.freesoft.joykt.chapter9

import java.lang.IllegalStateException

fun or(a: Boolean, b: Boolean): Boolean = if (a) true else b
fun and(a: Boolean, b: Boolean): Boolean = if (a) b else false

fun getFirst(): Boolean = true
fun getSecond(): Boolean = throw IllegalStateException()

fun lazyOr(a: () -> Boolean, b: () -> Boolean): Boolean = if (a()) true else b()

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
//    println(or(getFirst(), getSecond())) // this will eager evaluate the second operand and will throw an exception

    val first: Boolean by lazy { true }
    val second: Boolean by lazy { throw IllegalStateException() }

    println(first || second) // this will not evaluate second and will not throw the IllegalStateException
//    println(or(first, second)) // this will eager evaluate second because it is passed by value and will throw the Exception

    val firstSupplier: () -> Boolean = { true }
    val secondSupplier: () -> Nothing = { throw IllegalStateException() }

    println(firstSupplier() || secondSupplier()) // secondSupplier will be lazily evaluated and no exception will be thrown
    println(lazyOr(firstSupplier, secondSupplier)) //  secondSupplier will be lazily evaluated and no exception will be thrown

}