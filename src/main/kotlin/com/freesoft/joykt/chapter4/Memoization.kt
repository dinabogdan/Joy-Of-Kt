package com.freesoft.joykt.chapter4

import java.lang.IllegalArgumentException
import java.lang.StringBuilder
import java.math.BigInteger

fun fibo(limit: Int): String =
        when {
            limit < 1 -> throw IllegalArgumentException()
            limit == 1 -> "1"
            else -> {
                var fibo1 = BigInteger.ONE
                var fibo2 = BigInteger.ONE
                var fibonacci: BigInteger
                val builder = StringBuilder("1, 1")
                for (i in 2 until limit) {
                    fibonacci = fibo1.add(fibo2)
                    builder.append(", ").append(fibonacci)
                    fibo1 = fibo2
                    fibo2 = fibonacci
                }
                builder.toString()
            }
        }

fun recursiveFibo(number: Int): String {
    tailrec fun fibo(acc: List<BigInteger>, acc1: BigInteger, acc2: BigInteger, x: BigInteger): List<BigInteger> =
            when (x) {
                BigInteger.ZERO -> acc
                BigInteger.ONE -> acc + (acc1 + acc2)
                else -> fibo(acc + (acc1 + acc2), acc2, acc1 + acc2, x - BigInteger.ONE)
            }

    val list = fibo(listOf(), BigInteger.ONE, BigInteger.ZERO, BigInteger.valueOf(number.toLong()))
    return makeString(list, ", ")
}

fun main() {
    println(recursiveFibo(10))
}