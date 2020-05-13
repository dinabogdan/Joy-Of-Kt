package com.freesoft.joykt.chapter4

fun toStringTailRec(list: List<Char>): String {
    tailrec fun toString(list: List<Char>, s: String): String =
            if (list.isEmpty())
                s
            else toString(list.subList(1, list.size), append(s, list[0]))
    return toString(list, "")
}

// Phase 1
//fun sum(n: Int, sum: Int, idx: Int): Int = if (idx < 1) sum else sum(n, sum + idx, idx - 1)
//fun sum(n: Int) = sum(n, 0, n)

// phase 2 - local recursive helper function
//fun sum(n: Int): Int {
//    fun sum(sum: Int, idx: Int): Int =
//            if (idx < 1) sum else -sum(sum + idx, idx - 1)
//    return sum(0, n)
//}

//phase 3 - local tail-recursive helper function
fun sum(n: Int): Int {
    tailrec fun sum(s: Int, i: Int): Int =
            if (i > n) s else sum(s + i, i + 1)
    return sum(0, 0)
}

fun main() {
    println(sum(4))
}