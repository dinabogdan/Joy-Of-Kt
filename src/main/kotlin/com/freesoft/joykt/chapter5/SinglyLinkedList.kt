package com.freesoft.joykt.chapter5

import java.lang.IllegalStateException

sealed class List<A> {

    abstract fun isEmpty(): Boolean

    fun cons(a: A): List<A> = Cons(a, this)

    fun setHead(a: A): List<A> = when (this) {
        is Nil -> throw IllegalStateException("setHead call on empty list is not allowed")
        is Cons -> tail.cons(a)
    }

    fun drop(n: Int): List<A> {
        tailrec fun drop(n: Int, list: List<A>): List<A> =
                if (n <= 0) list else when (list) {
                    is Cons -> drop(n - 1, list.tail)
                    is Nil -> list
                }
        return drop(n, this)
    }

    fun dropWhile(p: (A) -> Boolean): List<A> {
        tailrec fun dropWhile_(list: List<A>): List<A> = when (list) {
            is Nil -> list
            is Cons -> if (p(list.head)) dropWhile_(list.tail) else list
        }
        return dropWhile_(this)
    }

    internal object Nil : List<Nothing>() {
        override fun isEmpty(): Boolean = true

        override fun toString(): String = "[NIL]"
    }

    internal class Cons<A>(
            internal val head: A,
            internal val tail: List<A>
    ) : List<A>() {
        override fun isEmpty(): Boolean = false

        override fun toString(): String = "[${toString("", this)}NIL]"

        private tailrec fun toString(acc: String, list: List<A>): String =
                when (list) {
                    is Nil -> acc
                    is Cons -> toString("$acc${list.head}, ", list.tail)
                }
    }

    companion object {
        operator fun <A> invoke(vararg az: A): List<A> =
                az.foldRight(Nil as List<A>) { a: A, list: List<A> -> Cons(a, list) }
    }
}

fun main() {
    val list: List<Int> = List(1, 2, 3)

    println("The initial list: $list")

    val newList = list.cons(6)

    println("The initial list after adding 6 to it: $list")
    println("The new list obtained after adding 6 to the initial list: $newList")

    val newSetHeadList = newList.setHead(1)

    println(newSetHeadList)

    val listAfterdrop = list.drop(1)
    println(listAfterdrop)

    println(list.dropWhile { it < 3 })

}


