package com.freesoft.joykt.chapter4

fun <T> prependF(t: T, list: List<T>): List<T> = listOf(t) + list

fun <T> reverse(list: List<T>): List<T> = foldLeft(list, listOf()) { _list, el -> prependF(el, _list) }

fun <T> copy(list: List<T>): List<T> = foldLeft(list, listOf()) { _list, el -> _list + el }

fun <T> prependFold(list: List<T>, elem: T): List<T> = foldLeft(list, listOf(elem)) { _list, el -> _list + el }

fun <T> reverse2(list: List<T>): List<T> = foldLeft(list, listOf()) { _list, t -> prependFold(_list, t) }

fun main() {
    println(reverse(listOf(1, 2, 3, 4)))
    println(reverse2(listOf(1, 2, 3, 4)))
}