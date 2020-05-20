package com.freesoft.joykt.chapter9

import com.freesoft.joykt.chapter5.List
import com.freesoft.joykt.chapter7.Result

sealed class Stream<out A> {
    abstract fun isEmpty(): Boolean
    abstract fun head(): Result<A>
    abstract fun tail(): Result<Stream<A>>
    abstract fun takeAtMost(n: Int): Stream<A>
    abstract fun dropAtMost(n: Int): Stream<A>
    abstract fun takeWhile(p: (A) -> Boolean): Stream<A>
    abstract fun dropWhile(p: (A) -> Boolean): Stream<A>
    abstract fun exists(p: (A) -> Boolean): Boolean
    abstract fun <B> foldRight(z: Lazy<B>, f: (A) -> (Lazy<B>) -> B): B
    abstract fun takeWhileViaFoldRight(p: (A) -> Boolean): Stream<A>

    fun <A> repeat(f: () -> A): Stream<A> = cons(Lazy { f() }, Lazy { repeat(f) })

    fun toList(): List<A> = Companion.toList(this)

    fun headSafeViaFoldRight(): Result<A> =
            foldRight(Lazy { Result<A>() }) { a: A -> { Result(a) } }

    fun <B> map(f: (A) -> B): Stream<B> =
            foldRight(Lazy { Empty }) { a ->
                { b: Lazy<Stream<B>> -> cons(Lazy { f(a) }, b) }
            }

    fun filter(p: (A) -> Boolean): Stream<A> =
            foldRight(Lazy { Empty }) { a ->
                { b: Lazy<Stream<A>> ->
                    if (p(a))
                        cons(Lazy { a }, b)
                    else
                        b()
                }
            }

    fun append(stream2: Lazy<Stream<@UnsafeVariance A>>): Stream<A> =
            this.foldRight(stream2) { a: A ->
                { b: Lazy<Stream<A>> -> cons(Lazy { a }, b) }
            }

    private object Empty : Stream<Nothing>() {
        override fun isEmpty(): Boolean = true

        override fun head(): Result<Nothing> = Result()

        override fun tail(): Result<Stream<Nothing>> = Result()

        override fun takeAtMost(n: Int): Stream<Nothing> = this

        override fun dropAtMost(n: Int): Stream<Nothing> = this

        override fun takeWhile(p: (Nothing) -> Boolean): Stream<Nothing> = this

        override fun dropWhile(p: (Nothing) -> Boolean): Stream<Nothing> = this

        override fun exists(p: (Nothing) -> Boolean): Boolean = false

        override fun <B> foldRight(z: Lazy<B>, f: (Nothing) -> (Lazy<B>) -> B): B = z()

        override fun takeWhileViaFoldRight(p: (Nothing) -> Boolean): Stream<Nothing> = this
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

        override fun takeWhile(p: (A) -> Boolean): Stream<A> = when {
            p(_head()) -> cons(_head, Lazy { _tail().takeWhile(p) })
            else -> Empty
        }

        override fun dropWhile(p: (A) -> Boolean): Stream<A> {
            tailrec fun <A> dropWhile_(stream: Stream<A>, p: (A) -> Boolean): Stream<A> =
                    when (stream) {
                        is Empty -> stream
                        is Cons -> when {
                            p(stream._head()) -> dropWhile_(stream._tail(), p)
                            else -> stream
                        }
                    }
            return dropWhile_(this, p)
        }

        override fun exists(p: (A) -> Boolean): Boolean = Companion.exists(this, p)

        override fun <B> foldRight(z: Lazy<B>, f: (A) -> (Lazy<B>) -> B): B =
                f(_head())(Lazy { _tail().foldRight(z, f) })

        override fun takeWhileViaFoldRight(p: (A) -> Boolean): Stream<A> =
                foldRight(Lazy { Empty }) { a ->
                    { b: Lazy<Stream<A>> ->
                        if (p(a))
                            cons(Lazy { a }, b)
                        else
                            Empty
                    }
                }
    }

    companion object {
        fun <A> cons(head: Lazy<A>,
                     tail: Lazy<Stream<A>>): Stream<A> = Cons(head, tail)

        operator fun <A> invoke(): Stream<A> = Empty

        fun <A> iterate(seed: A, f: (A) -> A): Stream<A> =
                cons(Lazy { seed }, Lazy { iterate(f(seed), f) })

        fun from(i: Int): Stream<Int> = iterate(i) { it + 1 }

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

        tailrec fun <A> exists(stream: Stream<A>, p: (A) -> Boolean): Boolean =
                when (stream) {
                    is Empty -> false
                    is Cons -> when {
                        p(stream._head()) -> true
                        else -> exists(stream._tail(), p)
                    }
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

    fun inc(i: Int): Int = (i + 1).let {
        println("generating $it")
        it
    }

    val list = Stream.iterate(0, ::inc)
            .takeAtMost(60_000)
            .dropAtMost(10_000)
            .takeAtMost(10)
            .toList()

    println(list)

    val result = Stream.iterate(0, ::inc)
            .takeAtMost(100)
            .foldRight(Lazy { 0 }) { a ->
                { lz -> a + lz() }
            }

    println(result)

    val resList = Stream.iterate(0, ::inc)
            .takeAtMost(50)
            .foldRight(Lazy { List<Int>() }) { a: Int ->
                { lz: Lazy<List<Int>> ->
                    lz().cons(a)
                }
            }

    println("The resList is: $resList")

    val head = resList.headSafe()

    println("The head is: $head")

    val stream1 = Stream.iterate(0, ::inc)
            .takeAtMost(10)
            .dropAtMost(5)

    println("Stream1: ${stream1.toList()}")

    val appendResult = Stream.iterate(0, ::inc)
            .takeAtMost(5)
            .append(Lazy {
                stream1
            }).toList()

    println("AppendResult: $appendResult")

}