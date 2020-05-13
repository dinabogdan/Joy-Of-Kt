package com.freesoft.joykt.chapter4

fun <T, U> foldLeft(list: List<T>, startingValue: U, f: (U, T) -> U): U {
    tailrec fun foldLeft(list: List<T>, acc: U): U =
            if (list.isEmpty())
                acc
            else foldLeft(list.tail(), f(acc, list.head()))
    return foldLeft(list, startingValue)
}

fun sumF(list: List<Int>) = foldLeft(list, 0, Int::plus)

fun string(list: List<Char>) = foldLeft(list, "", String::plus)

fun <T> makeStringF(list: List<T>, delim: String) = foldLeft(list, "") { s, t -> if (s.isEmpty()) "$t" else "$s$delim$t" }

fun main() {
    println(sumF(listOf(1, 2, 3, 4)))
    println(string(listOf('1', '2', '3')))
    println(makeString(listOf(1, 2, 3), ","))
}