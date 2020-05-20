package com.freesoft.joykt.chapter9

import com.freesoft.joykt.chapter5.List
import com.freesoft.joykt.chapter7.Result

private val f = { x: Int ->
    println("Mapping $x")
    x * 3
}

private val p = { x: Int ->
    println("Filtering $x")
    x % 2 == 0
}

fun fibs(): Stream<Int> =
        Stream.iterate(Pair(1, 1)) { (x, y) ->
            y to x + y
        }.map { x -> x.first }

fun unfoldFib(): Stream<Int> =
        Stream.unfold(Pair(1, 1)) { x ->
            Result(Pair(x.first, Pair(x.first, x.first + x.second)))
        }

fun main() {

    val list = List(1, 2, 3, 4, 5)
            .map(f)
            .filter(p)

    println(list)

    val stream = Stream.from(1)
            .takeAtMost(5)
            .map(f)
            .filter(p)

    println("Stream to list: ${stream.toList()}")


}