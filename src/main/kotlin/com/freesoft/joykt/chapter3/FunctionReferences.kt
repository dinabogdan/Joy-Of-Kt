package com.freesoft.joykt.chapter3

fun double(n: Int): Int = n * 2

//val multiplyBy2: (Int) -> Int = { n -> double(n) }
//val multiplyBy2: (Int) -> Int = { double(it) }

// function reference to double
val multiplyBy2: (Int) -> Int = ::double

fun square(n: Int) = n * n
fun triple(n: Int) = n * 3

fun <T, U, V> compose(f: (U) -> V, g: (T) -> U): (T) -> V = { x -> f(g(x)) }

fun main() {
    val squareOfTriple = compose(::square, ::triple)
    println(squareOfTriple(5))
}