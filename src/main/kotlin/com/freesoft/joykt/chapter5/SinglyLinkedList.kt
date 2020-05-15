package com.freesoft.joykt.chapter5

sealed class List<A> {

    abstract fun isEmpty(): Boolean

    internal object Nil : List<Nothing>() {
        override fun isEmpty(): Boolean = true

        override fun toString(): String = "[NIL]"
    }

    internal class Cons<A>(
            private val head: A,
            private val tail: List<A>
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

    println(list)
}


