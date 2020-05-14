package com.freesoft.joykt.chapter4

fun range(start: Int, end: Int): List<Int> {
    val result: MutableList<Int> = mutableListOf()
    var index = start
    while (index < end) {
        result.add(index)
        index++
    }
    return result
}

fun <T> unfold(seed: T, nextFunction: (T) -> T, predicate: (T) -> Boolean): List<T> {
    val result: MutableList<T> = mutableListOf()
    var elem = seed
    while (predicate(elem)) {
        result.add(elem)
        elem = nextFunction(elem)
    }
    return result
}

fun <T> recursiveUnfold(seed: T, nextFunction: (T) -> T, predicate: (T) -> Boolean): List<T> = if (predicate(seed))
    prependF(seed, recursiveUnfold(nextFunction(seed), nextFunction, predicate))
else
    listOf()

fun <T> tailRecursiveUnfold(seed: T, nextFunction: (T) -> T, predicate: (T) -> Boolean): List<T> {
    tailrec fun unfold(seed: T, acc: List<T>): List<T> =
            if (!predicate(seed)) acc
            else unfold(nextFunction(seed), prependF(seed, acc))

    return unfold(seed, listOf())
}

fun unfoldRange(start: Int, end: Int): List<Int> = unfold(start, { it + 1 }, { it < end })

fun recursiveRange(start: Int, end: Int): List<Int> =
        if (end <= start) listOf()
        else prependF(start, range(start + 1, end))

fun tailRecursiveRange(start: Int, end: Int): List<Int> {
    tailrec fun range(start: Int, end: Int, acc: List<Int>): List<Int> =
            if (start == end) acc
            else range(start + 1, end, prependF(start + 1, acc))
    return range(start, end, listOf())
}

fun main() {
    val res = recursiveUnfold(10, { el -> el - 1 }, { el -> el > 0 })
    println(res)
}