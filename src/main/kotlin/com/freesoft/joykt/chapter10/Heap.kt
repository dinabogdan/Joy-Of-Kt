package com.freesoft.joykt.chapter10

import com.freesoft.joykt.chapter6.Option
import com.freesoft.joykt.chapter7.Result
import com.freesoft.joykt.chapter5.List
import java.lang.IllegalStateException
import java.util.NoSuchElementException

sealed class Heap<out A : Comparable<@kotlin.UnsafeVariance A>> {

    internal abstract val left: Result<Heap<A>>
    internal abstract val right: Result<Heap<A>>
    internal abstract val head: Result<A>

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

    operator fun plus(element: @UnsafeVariance A): Heap<A> = merge(this, Heap(element))

    abstract class Empty<out A : Comparable<@kotlin.UnsafeVariance A>> : Heap<A>() {
        override val left: Result<Heap<A>> = Result(E)
        override val right: Result<Heap<A>> = Result(E)
        override val head: Result<A> = Result.failure("head() called on empty heap")
        override val rank: Int = 0
        override val size: Int = 0
        override val isEmpty: Boolean = true

        override fun tail(): Result<Heap<A>> = Result.failure(IllegalStateException("tail() called on empty heap"))
        override fun get(index: Int): Result<A> = Result.failure(NoSuchElementException("Index out of bounds"))
        override fun pop(): Option<Pair<A, Heap<A>>> = Option()
    }

    internal object E : Empty<Nothing>()

    internal class H<out A : Comparable<@kotlin.UnsafeVariance A>>(
            override val rank: Int,
            private val _left: Heap<A>,
            private val _head: A,
            private val _right: Heap<A>
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
        operator fun <A : Comparable<A>> invoke(): Heap<A> = E

        operator fun <A : Comparable<A>> invoke(element: A): Heap<A> = H(1, E, element, E)

        fun <A : Comparable<A>> merge(first: Heap<A>, second: Heap<A>): Heap<A> =
                first.head.flatMap { fh ->
                    second.head.flatMap { sh ->
                        when {
                            fh <= sh -> first.left.flatMap { fl ->
                                first.right.map { fr ->
                                    merge(fh, fl, merge(fr, second))
                                }
                            }
                            else -> second.left.flatMap { sl ->
                                second.right.map { sr ->
                                    merge(sh, sl, merge(first, sr))
                                }
                            }
                        }
                    }
                }.getOrElse(when (first) {
                    E -> second
                    else -> first
                })

        fun <A : Comparable<A>> merge(head: A, first: Heap<A>, second: Heap<A>): Heap<A> =
                when {
                    first.rank >= second.rank -> H(second.rank + 1, first, head, second)
                    else -> H(first.rank + 1, second, head, first)
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
    }
}