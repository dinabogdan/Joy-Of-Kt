package com.freesoft.joykt.chapter4

import java.math.BigInteger

val f = { x: Pair<BigInteger, BigInteger> -> Pair(x.second, x.first + x.second) }

fun <T> iterate(seed: T, nextFunction: (T) -> T, n: Int): List<T> {
    tailrec fun iterate_(acc: List<T>, seed: T): List<T> =
            if (acc.size < n)
                iterate_(acc + seed, nextFunction(seed))
            else acc
    return iterate_(listOf(), seed)
}

fun <T, U> map(list: List<T>, applyFunction: (T) -> U): List<U> =
        foldLeft(list, listOf()) { _list, el -> _list + applyFunction(el) }

fun <T, U> tailMap(list: List<T>, applyFunction: (T) -> U): List<U> {
    tailrec fun map_(acc: List<U>, list: List<T>): List<U> =
            if (list.isEmpty()) acc
            else map_(acc + applyFunction(list.head()), list.tail())
    return map_(listOf(), list)
}

fun fiboCorecursive(number: Int): String {
    val seed = Pair(BigInteger.ZERO, BigInteger.ONE)
    val f = { x: Pair<BigInteger, BigInteger> -> Pair(x.second, x.first + x.second) }
    val listOfPairs = iterate(seed, f, number + 1)
    val list = map(listOfPairs) { p -> p.first }
    return makeString(list, ", ")
}

fun main() {
    println(map(listOf(1, 2, 3, 4)) { x -> x * 2 })
    println(fiboCorecursive(10))
}