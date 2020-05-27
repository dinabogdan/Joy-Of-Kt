package com.freesoft.joykt.chapter10and11

import com.freesoft.joykt.chapter6.Option
import com.freesoft.joykt.chapter7.Result
import com.freesoft.joykt.chapter5.List
import java.lang.IllegalStateException
import java.util.NoSuchElementException

sealed class Heap<out A> {

    internal abstract val left: Result<Heap<A>>
    internal abstract val right: Result<Heap<A>>
    internal abstract val head: Result<A>

    internal abstract val comparator: Result<Comparator<@UnsafeVariance A>>

    protected abstract val rank: Int

    abstract val size: Int
    abstract val isEmpty: Boolean

    abstract fun tail(): Result<Heap<A>>
    abstract fun get(index: Int): Result<A>
    abstract fun pop(): Option<Pair<A, Heap<A>>>

    fun <B> foldLeft(identity: B, f: (B) -> (A) -> B): B = unfold(this, { it.pop() }, identity, f)
    fun toList(): List<A> = this.foldLeft(List<A>()) { list ->
        { a ->
            list.cons(a)
        }
    }.reverse()

    operator fun plus(element: @UnsafeVariance A): Heap<A> = merge(this, Heap(element, comparator))

    internal class Empty<out A>(
            override val comparator: Result<Comparator<@UnsafeVariance A>> = Result.Empty
    ) : Heap<A>() {
        override val left: Result<Heap<A>> = Result(this)
        override val right: Result<Heap<A>> = Result(this)
        override val head: Result<A> = Result.failure("head() called on empty heap")
        override val rank: Int = 0
        override val size: Int = 0
        override val isEmpty: Boolean = true

        override fun tail(): Result<Heap<A>> = Result.failure(IllegalStateException("tail() called on empty heap"))
        override fun get(index: Int): Result<A> = Result.failure(NoSuchElementException("Index out of bounds"))
        override fun pop(): Option<Pair<A, Heap<A>>> = Option()
    }

    internal class H<out A>(
            override val rank: Int,
            private val _left: Heap<A>,
            private val _head: A,
            private val _right: Heap<A>,
            override val comparator: Result<Comparator<@UnsafeVariance A>> =
                    _left.comparator.orElse { _right.comparator }
    ) : Heap<A>() {

        override val left: Result<Heap<A>> = Result(_left)
        override val right: Result<Heap<A>> = Result(_right)
        override val head: Result<A> = Result(_head)
        override val size: Int = _left.size + _right.size + 1
        override val isEmpty: Boolean = false

        override fun tail(): Result<Heap<A>> = Result(merge(_left, _right))
        override fun get(index: Int): Result<A> = when (index) {
            0 -> Result(_head)
            else -> tail().flatMap { heap -> heap.get(index - 1) }
        }

        override fun pop(): Option<Pair<A, Heap<A>>> = Option(Pair(_head, merge(_left, _right)))
    }

    companion object {
        operator fun <A : Comparable<A>> invoke(): Heap<A> = Empty()

        operator fun <A> invoke(comparator: Comparator<A>): Heap<A> = Empty(Result(comparator))

        operator fun <A> invoke(comparator: Result<Comparator<A>>): Heap<A> = Empty(comparator)

        operator fun <A : Comparable<A>> invoke(element: A): Heap<A> =
                invoke(element, Comparator { o1: A, o2: A -> o1.compareTo(o2) })

        operator fun <A> invoke(element: A, comparator: Comparator<A>): Heap<A> =
                H(1, Empty(Result(comparator)), element, Empty(Result(comparator)), Result(comparator))

        operator fun <A> invoke(element: A, comparator: Result<Comparator<A>>): Heap<A> =
                H(1, Empty(comparator), element, Empty(comparator), comparator)

        fun <A> merge(
                first: Heap<A>,
                second: Heap<A>,
                comparator: Result<Comparator<A>> = first.comparator.orElse { second.comparator }): Heap<A> =
                first.head.flatMap { fh ->
                    second.head.flatMap { sh ->
                        when {
                            compare(fh, sh, comparator) <= 0 ->
                                first.left.flatMap { fl ->
                                    first.right.map { fr ->
                                        merge(fh, fl, merge(fr, second, comparator))
                                    }
                                }
                            else -> second.left.flatMap { sl ->
                                second.right.map { sr ->
                                    merge(sh, sl, merge(first, sr, comparator))
                                }
                            }
                        }
                    }
                }.getOrElse(when (first) {
                    is Empty -> second
                    else -> first
                })

        fun <A> merge(head: A, first: Heap<A>, second: Heap<A>): Heap<A> =
                first.comparator.orElse { second.comparator }.let {
                    when {
                        first.rank >= second.rank -> H(second.rank + 1,
                                first, head, second, it)
                        else -> H(first.rank + 1, second, head, first, it)
                    }
                }

        fun <A, S, B> unfold(z: S,
                             getNext: (S) -> Option<Pair<A, S>>,
                             identity: B,
                             f: (B) -> (A) -> B): B {
            tailrec fun unfold(acc: B, z: S): B {
                val next = getNext(z)
                return when (next) {
                    is Option.None -> acc
                    is Option.Some -> unfold(f(acc)(next.value.first), next.value.second)
                }
            }
            return unfold(identity, z)
        }

        private fun <A> compare(first: A, second: A, comparator: Result<Comparator<A>>): Int =
                comparator.map { comp ->
                    comp.compare(first, second)
                }.getOrElse((first as Comparable<A>).compareTo(second))
    }
}

fun main() {
    val heap = Heap(1)
}