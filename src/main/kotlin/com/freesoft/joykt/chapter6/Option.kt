package com.freesoft.joykt.chapter6

import java.lang.RuntimeException

sealed class Option<out A> {

    abstract fun isEmpty(): Boolean

    fun getOrElse(default: @UnsafeVariance A): A = when (this) {
        is None -> default
        is Some -> value
    }

    fun getOrElse(default: () -> @UnsafeVariance A): A = when (this) {
        is None -> default()
        is Some -> value
    }

    fun <B> map(f: (A) -> B): Option<B> = when (this) {
        is None -> None
        is Some -> Some(f(value))
    }

    internal object None : Option<Nothing>() {
        override fun isEmpty(): Boolean = true

        override fun toString(): String = "None"

        override fun equals(other: Any?): Boolean = other === None

        override fun hashCode(): Int = 0
    }

    internal data class Some<out A>(internal val value: A) : Option<A>() {
        override fun isEmpty(): Boolean = false

    }

    companion object {
        operator fun <A> invoke(a: A? = null): Option<A> = when (a) {
            null -> None
            else -> Some(a)
        }
    }
}

fun max(list: List<Int>): Option<Int> = Option(list.max())

fun getDefault(): Int = throw RuntimeException()

fun main() {
    val max1 = max(listOf(3, 5, 1, 2, 7)).getOrElse(0)
    println(max1)

    val max2 = max(listOf()).getOrElse(0)
    println(max2)

//    println(max(listOf(1, 2, 3, 4, 5, 6)).getOrElse(getDefault())) // throw an exception

    println(max(listOf(1, 2, 3)).getOrElse(::getDefault))

//    println(max(listOf()).getOrElse(::getDefault)) // throw an exception
}