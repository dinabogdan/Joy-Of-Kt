package com.freesoft.joykt.chapter3

fun double(n: Int): Int = n * 2

//val multiplyBy2: (Int) -> Int = { n -> double(n) }
//val multiplyBy2: (Int) -> Int = { double(it) }

// function reference to double
val multiplyBy2: (Int) -> Int = ::double

fun square(n: Int) = n * n
fun triple(n: Int) = n * 3

fun <T, U, V> compose(f: (U) -> V, g: (T) -> U): (T) -> V = { x -> f(g(x)) }

fun add(n: Int): (Int) -> Int = { x -> x + n }

typealias IntBinOp = (Int) -> (Int) -> Int

val add2: IntBinOp = { a -> { b -> a + b } }
val multiply: IntBinOp = { a -> { b -> a * b } }

typealias IntUnaryOp = (Int) -> Int

val composer: (IntUnaryOp) -> (IntUnaryOp) -> IntUnaryOp = { x -> { y -> { z -> x(y(z)) } } }

fun main() {
    val squareOfTriple = compose(::square, ::triple)
    println(squareOfTriple(5))

    println(add(5)(4))
    println(add2(6)(10))
    println(multiply(4)(5))

    val square: IntUnaryOp = { it * it }
    val triple: IntUnaryOp = { it * 3 }

    val squareOfTriple2 = composer(square)(triple)
    println(squareOfTriple(5))
}