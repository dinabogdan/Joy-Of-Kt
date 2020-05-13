package com.freesoft.joykt.chapter4

fun inc(n: Int) = n + 1
fun dec(n: Int) = n - 1

tailrec fun add(a: Int, b: Int): Int {
    return if (a == 0) b
    else add(dec(a), inc(b))
}

fun factorial(n: Int): Int = if (n == 0) 1 else n * factorial(n - 1)

object Factorial {
    private lateinit var fact: (Int) -> Int

    init {
        fact = { n -> if (n <= 1) n else n * fact(n - 1) }
    }

    val factorial = fact
}

fun main() {
    println(add(5, 6))

    println(factorial(10))
    println(Factorial.factorial(10))
}