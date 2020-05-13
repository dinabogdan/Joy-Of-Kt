package com.freesoft.joykt.chapter4

fun <T> makeString(list: List<T>, delim: String): String =
        when {
            list.isEmpty() -> ""
            list.tail().isEmpty() -> "${list.head()}${makeString(list.tail(), delim)}"
            else -> "${list.head()}$delim${makeString(list.tail(), delim)}"
        }

fun <T> tailMakeString(list: List<T>, delim: String): String {
    tailrec fun tailMakeString_(list: List<T>, acc: String): String =
            when {
                list.isEmpty() -> acc
                acc.isEmpty() -> tailMakeString_(list.tail(), "${list.head()}")
                else -> tailMakeString_(list.tail(), "$acc$delim${list.head()}")
            }
    return tailMakeString_(list, "")
}

fun main() {
    println(tailMakeString(listOf('a', 'b', 'c'), ","))
}


