package com.freesoft.joykt.chapter10

import com.freesoft.joykt.chapter7.Result

sealed class Heap<out A : Comparable<@kotlin.UnsafeVariance A>> {

    internal abstract val left: Result<Heap<A>>
    internal abstract val right: Result<Heap<A>>
    internal abstract val head: Result<A>

    protected abstract val rank: Int

    abstract val size: Int
    abstract val isEmpty: Boolean

    operator fun plus(element: @UnsafeVariance A): Heap<A> = merge(this, Heap(element))

    abstract class Empty<out A : Comparable<@kotlin.UnsafeVariance A>> : Heap<A>() {
        override val left: Result<Heap<A>> = Result(E)
        override val right: Result<Heap<A>> = Result(E)
        override val head: Result<A> = Result.failure("head() called on empty heap")
        override val rank: Int = 0
        override val size: Int = 0
        override val isEmpty: Boolean = true
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
    }
}