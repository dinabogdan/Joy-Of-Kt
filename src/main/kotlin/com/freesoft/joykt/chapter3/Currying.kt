package com.freesoft.joykt.chapter3

fun <A, B, C, D> func(a: A, b: B, c: C, d: D): String = "$a, $b, $c, $d"

fun <A, B, C, D> curried(): (A) -> (B) -> (C) -> (D) -> String = { a ->
    { b ->
        { c ->
            { d -> "$a, $b, $c, $d" }
        }
    }
}

fun <A, B, C> curry(f: (A, B) -> C): (A) -> (B) -> C = { a ->
    { b ->
        f(a, b)
    }
}

val addTax2: (Double) -> (Double) -> Double = { x ->
    { y -> y / 100 * x }
}

val add9percentTax: (Double) -> Double = addTax2(9.0)

fun <T, U, V> swapArgs(f: (T) -> (U) -> V): (U) -> (T) -> (V) = { u ->
    { t -> f(t)(u) }
}
