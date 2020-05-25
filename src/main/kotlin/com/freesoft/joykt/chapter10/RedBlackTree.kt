package com.freesoft.joykt.chapter10

import java.lang.IllegalStateException
import java.lang.Integer.max

sealed class RedBlackTree<out A : Comparable<@kotlin.UnsafeVariance A>> {
    abstract val size: Int
    abstract val height: Int

    internal abstract val color: Color

    internal abstract val isTB: Boolean
    internal abstract val isTR: Boolean

    internal abstract val right: RedBlackTree<A>
    internal abstract val left: RedBlackTree<A>
    internal abstract val value: A

    internal abstract fun blacken(): RedBlackTree<A>

    internal abstract fun add(newVal: @UnsafeVariance A): RedBlackTree<A>

    protected fun balance(color: Color,
                          left: RedBlackTree<@UnsafeVariance A>,
                          value: @UnsafeVariance A,
                          right: RedBlackTree<@UnsafeVariance A>
    ): RedBlackTree<A> = when {
        color == Color.B && left.isTR && left.left.isTR ->
            T(Color.R, left.left.blacken(), left.value, T(Color.B, left.right, value, right))
        color == Color.B && left.isTR && left.right.isTR ->
            T(Color.R, T(Color.B, left.left, left.value, left.right.left), left.right.value, T(Color.B, left.right.right, value, right))
        color == Color.B && right.isTR && right.left.isTR ->
            T(Color.R, T(Color.B, left, value, right.left.left), right.left.value, T(Color.B, right.left.right, right.value, right.right))
        color == Color.B && right.isTR && right.right.isTR ->
            T(Color.R, T(Color.B, left, value, right.left), right.value, right.right.blacken())
        else -> T(color, left, value, right)
    }

    internal abstract class Empty<out A : Comparable<@kotlin.UnsafeVariance A>> : RedBlackTree<A>() {
        override val isTB: Boolean = false
        override val isTR: Boolean = false

        override val right: RedBlackTree<Nothing> by lazy {
            throw IllegalStateException("right called on Empty tree")
        }

        override val left: RedBlackTree<Nothing> by lazy {
            throw IllegalStateException("left called on Empty tree")
        }

        override val value: Nothing by lazy {
            throw IllegalStateException("value called on Empty tree")
        }

        override val size: Int = 0
        override val height: Int = -1
        override val color: Color = Color.B

        override fun toString(): String = "E"

        override fun blacken(): RedBlackTree<Nothing> = E

        override fun add(newVal: @UnsafeVariance A): RedBlackTree<A> = T(Color.R, E, newVal, E)

        operator fun plus(value: @UnsafeVariance A): RedBlackTree<A> = add(value).blacken()
    }

    internal object E : Empty<Nothing>()

    internal class T<out A : Comparable<@kotlin.UnsafeVariance A>>(
            override val color: Color,
            override val left: RedBlackTree<A>,
            override val value: A,
            override val right: RedBlackTree<A>
    ) : RedBlackTree<A>() {

        override val size: Int = left.size + 1 + right.size
        override val height: Int = max(left.height, right.height) + 1

        override val isTB: Boolean = color == Color.B
        override val isTR: Boolean = color == Color.R

        override fun toString(): String = "(T $color $left $value $right)"

        override fun blacken(): RedBlackTree<A> = T(Color.B, left, value, right)

        override fun add(newVal: @UnsafeVariance A): RedBlackTree<A> = when {
            newVal < value -> balance(color, left.add(newVal), value, right)
            newVal > value -> balance(color, left, value, right.add(newVal))
            else -> when (color) {
                Color.B -> T(Color.B, left, newVal, right)
                Color.R -> T(Color.R, left, newVal, right)
            }
        }
    }

    companion object {
        operator fun <A : Comparable<A>> invoke(): RedBlackTree<A> = E
    }
}

sealed class Color {
    internal object R : Color() {
        override fun toString(): String = "R"
    }

    internal object B : Color() {
        override fun toString(): String = "B"
    }
}