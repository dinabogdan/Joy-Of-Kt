package com.freesoft.joykt.chapter9

import com.freesoft.joykt.chapter7.Result

sealed class Stream<out A> {
    abstract fun isEmpty(): Boolean
    abstract fun head(): Result<A>
    abstract fun tail(): Result<Stream<A>>

    private object Empty : Stream<Nothing>() {
        override fun isEmpty(): Boolean = true

        override fun head(): Result<Nothing> = Result()

        override fun tail(): Result<Stream<Nothing>> = Result()

    }

    private class Cons<out A>(
            internal val _head: Lazy<A>,
            internal val _tail: Lazy<Stream<A>>
    ) : Stream<A>() {
        override fun isEmpty(): Boolean = false

        override fun head(): Result<A> = Result(_head())

        override fun tail(): Result<Stream<A>> = Result(_tail())
    }

    companion object {
        fun <A> cons(head: Lazy<A>,
                     tail: Lazy<Stream<A>>): Stream<A> = Cons(head, tail)

        operator fun <A> invoke(): Stream<A> = Empty

        fun from(i: Int): Stream<Int> = cons(Lazy { i }, Lazy { from(i + 1) })
    }
}

fun main() {
    val stream = Stream.from(1)

    stream.head().forEach({ println(it) })
    stream.tail()
            .flatMap { it.head() }
            .forEach({ println(it) })
    stream.tail()
            .flatMap {
                it.tail().flatMap {
                    it.head()
                }
            }.forEach({ println(it) })
}