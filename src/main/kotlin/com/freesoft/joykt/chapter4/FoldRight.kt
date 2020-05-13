package com.freesoft.joykt.chapter4

fun <T, U> foldRight(list: List<T>, startingValue: U, f: (T, U) -> U): U =
        when {
            list.isEmpty() -> startingValue
            else -> f(list.head(), foldRight(list.tail(), startingValue, f))
        }

fun stringFr(list: List<Char>): String = foldRight(list, "") { c, s -> prepend(c, s) }

fun main() {
    println(stringFr(listOf('a', 'b', 'c')))
}