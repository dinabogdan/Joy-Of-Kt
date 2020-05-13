package com.freesoft.joykt.chapter4

fun inc(n: Int) = n + 1
fun dec(n: Int) = n - 1

tailrec fun add(a: Int, b: Int): Int {
    return if (a == 0) b
    else add(dec(a), inc(b))
}

fun main() {
    println(add(5, 6))
}