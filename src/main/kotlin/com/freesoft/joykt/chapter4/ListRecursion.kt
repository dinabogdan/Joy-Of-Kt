package com.freesoft.joykt.chapter4

import java.lang.IllegalArgumentException

fun sum(list: List<Int>): Int = if (list.isEmpty()) 0 else list[0] + sum(list.drop(1))

fun <T> List<T>.head(): T =
        if (this.isEmpty())
            throw IllegalArgumentException("head was called on empty list")
        else this[0]

fun <T> List<T>.tail(): List<T> =
        if (this.isEmpty())
            throw IllegalArgumentException("tail was called on empty list")
        else this.drop(1)

fun sumR(list: List<Int>): Int =
        if (list.isEmpty())
            0
        else list.head() + sum(list.tail())

fun tailRecSum(list: List<Int>): Int {
    tailrec fun sumTail(list: List<Int>, acc: Int): Int =
            if (list.isEmpty())
                acc
            else sumTail(list.tail(), acc + list.head())
    return sumTail(list, 0)
}

fun main() {
    println(tailRecSum(listOf(1, 2, 3, 4)))
}