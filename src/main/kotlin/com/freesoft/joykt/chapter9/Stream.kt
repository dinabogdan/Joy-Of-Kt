package com.freesoft.joykt.chapter9

import com.freesoft.joykt.chapter7.Result
import com.freesoft.joykt.chapter5.List

sealed class Stream<out A> {
    abstract fun isEmpty(): Boolean
    abstract fun head(): Result<A>
    abstract fun tail(): Result<Stream<A>>
    abstract fun takeAtMost(n: Int): Stream<A>
    abstract fun dropAtMost(n: Int): Stream<A>

    fun <A> repeat(f: () -> A): Stream<A> = cons(Lazy { f() }, Lazy { repeat(f) })

    fun toList(): List<A> = Companion.toList(this)

    private object Empty : Stream<Nothing>() {
        override fun isEmpty(): Boolean = true

        override fun head(): Result<Nothing> = Result()

        override fun tail(): Result<Stream<Nothing>> = Result()

        override fun takeAtMost(n: Int): Stream<Nothing> = this

        override fun dropAtMost(n: Int): Stream<Nothing> = this
    }

    private class Cons<out A>(
            internal val _head: Lazy<A>,
            internal val _tail: Lazy<Stream<A>>
    ) : Stream<A>() {
        override fun isEmpty(): Boolean = false

        override fun head(): Result<A> = Result(_head())

        override fun tail(): Result<Stream<A>> = Result(_tail())

        override fun takeAtMost(n: Int): Stream<A> = when {
            n > 0 -> cons(_head, Lazy { _tail().takeAtMost(n - 1) })
            else -> Empty
        }

        override fun dropAtMost(n: Int): Stream<A> = Companion.dropAtMost(n, this)
    }

    companion object {
        fun <A> cons(head: Lazy<A>,
                     tail: Lazy<Stream<A>>): Stream<A> = Cons(head, tail)

        operator fun <A> invoke(): Stream<A> = Empty

        fun from(i: Int): Stream<Int> = cons(Lazy { i }, Lazy { from(i + 1) })

        tailrec fun <A> dropAtMost(n: Int, stream: Stream<A>): Stream<A> = when {
            n > 0 -> when (stream) {
                is Empty -> stream
                is Cons -> dropAtMost(n - 1, stream._tail())
            }
            else -> stream
        }

        fun <A> toList(stream: Stream<A>): List<A> {
            tailrec fun <A> toList_(list: List<A>, stream: Stream<A>): List<A> =
                    when (stream) {
                        is Empty -> list
                        is Cons -> toList_(list.cons(stream._head()), stream._tail())
                    }
            return toList_(List(), stream).reverse()
        }
    }
}

fun main() {
//    val stream = Stream.from(1)
//
//    stream.head().forEach({ println(it) })
//    stream.tail()
//            .flatMap { it.head() }
//            .forEach({ println(it) })
//    stream.tail()
//            .flatMap {
//                it.tail().flatMap {
//                    it.head()
//                }
//            }.forEach({ println(it) })

    val stream = Stream.from(0).dropAtMost(60_000).takeAtMost(60_000)
    println(stream.toList())
}