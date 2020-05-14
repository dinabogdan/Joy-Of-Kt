package com.freesoft.joykt.chapter4

import java.util.concurrent.ConcurrentHashMap
import kotlin.system.measureTimeMillis

class Memoizer<T, U> private constructor() {
    private val cache = ConcurrentHashMap<T, U>()

    private fun doMemoize(function: (T) -> U): (T) -> U =
            { input ->
                cache.computeIfAbsent(input) {
                    function(it)
                }
            }

    companion object {
        fun <T, U> memoize(function: (T) -> U): (T) -> U = Memoizer<T, U>().doMemoize(function)
    }
}

fun longComputation(number: Int): Int {
    Thread.sleep(1000)
    return number
}

fun main() {
    var result1 = 0
    var result2 = 0
    var result3 = 0
    val time1 = measureTimeMillis {
        result1 = longComputation(43)
    }

    val memoizedLongComputation = Memoizer.memoize(::longComputation)

    val time2 = measureTimeMillis {
        result2 = memoizedLongComputation(43)
    }

    val time3 = measureTimeMillis {
        result3 = memoizedLongComputation(43)
    }

    println("Call to nonmemoized functon: result = $result1, time = $time1")
    println("First call to memoized function: result = $result2, time = $time2")
    println("Second call to nonmemoized function: result = $result3, time = $time3")
}