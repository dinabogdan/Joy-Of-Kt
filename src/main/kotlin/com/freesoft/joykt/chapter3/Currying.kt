package com.freesoft.joykt.chapter3

fun <A, B, C, D> func(a: A, b: B, c: C, d: D): String = "$a, $b, $c, $d"

fun <A, B, C, D> curried(): (A) -> (B) -> (C) -> (D) -> String = { a ->
    { b ->
        { c ->
            { d -> "$a, $b, $c, $d" }
        }
    }
}