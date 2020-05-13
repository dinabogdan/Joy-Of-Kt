package com.freesoft.joykt.chapter4

fun append(s: String, c: Char): String = "$s$c"

fun toString(list: List<Char>, s: String): String =
        if (list.isEmpty())
            s
        else
            toString(list.drop(1), append(s, list.first()))


fun main() {
    println(toString(listOf('a', 'b', 'c'), "x"))
}