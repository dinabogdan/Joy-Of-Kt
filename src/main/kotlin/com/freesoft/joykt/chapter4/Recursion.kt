package com.freesoft.joykt.chapter4

fun prepend(c: Char, s: String): String = "$c$s"

//fun toString(list: List<Char>): String {
//    fun toString(list: List<Char>, s: String): String =
//            if (list.isEmpty())
//                s
//            else toString(list.subList(0, list.size - 1),
//                    prepend(list[list.size - 1], s))
//    return toString(list, "")
//}

fun toString(list: List<Char>): String =
        if (list.isEmpty())
            ""
        else
            prepend(list[0], toString(list.subList(1, list.size)))
