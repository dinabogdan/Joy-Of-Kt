package com.freesoft.joykt.chapter12

import com.freesoft.joykt.chapter7.Result
import java.lang.RuntimeException

fun main() {
    val ra = Result(4)
    val rb = Result(0)

    val inverse: (Int) -> Result<Double> = { x ->
        when (x != 0) {
            true -> Result(1.toDouble() / x)
            false -> Result.failure("Division by 0")
        }
    }

    val showResult: (Double) -> Unit = ::println
    val showError: (RuntimeException) -> Unit = { println("Error -${it.message}") }

    val rt1 = ra.flatMap(inverse)
    val rt2 = rb.flatMap(inverse)

    print("Inverse of 4: ")
    rt1.forEach(showResult, showError)

    print("Inverse of 0: ")
    rt2.forEach(showResult, showError)
}