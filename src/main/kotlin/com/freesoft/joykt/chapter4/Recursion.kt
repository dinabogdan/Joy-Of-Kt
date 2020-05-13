package com.freesoft.joykt.chapter4

import kotlin.system.measureNanoTime

fun prepend(c: Char, s: String): String = "$c$s"

//fun toString(list: List<Char>): String {
//    fun toString(list: List<Char>, s: String): String =
//            if (list.isEmpty())
//                s
//            else toString(list.subList(0, list.size - 1),
//                    prepend(list[list.size - 1], s))
//    return toString(list, "")
//}

fun toString(list: List<Char>): String =
        if (list.isEmpty())
            ""
        else
            prepend(list[0], toString(list.subList(1, list.size)))

fun corecursiveSum(list: List<Int>, sum: Int = 0): Int =
        if (list.isEmpty())
            sum
        else corecursiveSum(list.subList(1, list.size), sum + list.first())

fun recursiveSum(list: List<Int>, sum: Int = 0): Int =
        if (list.isEmpty())
            sum
        else recursiveSum(list.subList(0, list.size - 1), sum + list[list.size - 1])

fun main() {

    //  the following function calls are made in order to "heat up" the processors, otherwise the time measurement will be irrelevant
    println(recursiveSum(listOf(1, 2, 3, 4), 0))
    println(recursiveSum(listOf(1, 2, 3, 4), 0))
    println(recursiveSum(listOf(1, 2, 3, 4), 0))
    println(recursiveSum(listOf(1, 2, 3, 4), 0))
    println(corecursiveSum(listOf(1, 2, 3, 4), 0))
    println(corecursiveSum(listOf(1, 2, 3, 4), 0))
    println(corecursiveSum(listOf(1, 2, 3, 4), 0))
    println(corecursiveSum(listOf(1, 2, 3, 4), 0))


    val recursiveTime = measureNanoTime {
        println(recursiveSum(listOf(1, 2, 3, 4), 0))
    }

    val corecursiveTime = measureNanoTime {
        println(corecursiveSum(listOf(1, 2, 3, 4), 0))
    }



    println("Corecursive time is: $corecursiveTime")
    println("Recursive time is: $recursiveTime")
}