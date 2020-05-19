package com.freesoft.joykt.chapter9

import java.lang.IllegalStateException

class Lazy<out A>(function: () -> A) : () -> A {
    private val value: A by lazy(function)

    override operator fun invoke(): A = value
}

fun or(a: Lazy<Boolean>, b: Lazy<Boolean>): Boolean = if (a()) true else b()

fun main() {

    val first = Lazy {
        println("evaluating first")
        true
    }

    val second = Lazy {
        println("evaluating second")
        throw IllegalStateException()
    }

    println(first() || second())
    println(first() || second())
    println(or(first, second))

}