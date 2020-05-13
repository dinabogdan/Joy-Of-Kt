package com.freesoft.joykt.chapter4

import java.math.BigInteger

// this needs two recursive calls in order to compute the fibonacci number
// so it needs 2n fibonacci calls for computing the first n fibonacci numbers
fun fibonacci(number: Int): Int =
        if (number == 0 || number == 1)
            1
        else fibonacci(number - 1) + fibonacci(number - 2)


tailrec fun tailFibonacci(val1: BigInteger, val2: BigInteger, x: BigInteger): BigInteger =
        when {
            (x == BigInteger.ZERO) -> BigInteger.ONE
            (x == BigInteger.ONE) -> val1 + val2
            else -> tailFibonacci(val2, val1 + val2, x - BigInteger.ONE)
        }

fun fib(x: Int): BigInteger {
    return tailFibonacci(BigInteger.ZERO, BigInteger.ONE, BigInteger.valueOf(x.toLong()))
}

fun main() {
    (0 until 10).forEach { print("${fibonacci(it)} ") }
    println("###")
    (0 until 10_000).forEach { print("${fib(it)}") }

}