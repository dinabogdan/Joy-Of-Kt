package com.freesoft.joykt.chapter6

import kotlin.math.pow

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

    fun <B> flatMap(f: (A) -> Option<B>): Option<B> = when (this) {
        is None -> None
        is Some -> f(value)
    }

    fun orElse(default: () -> Option<@UnsafeVariance A>): Option<A> =
            map { this }.getOrElse(default)

    fun filter(p: (A) -> Boolean): Option<A> =
            flatMap { x -> if (p(x)) this else None }

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

val mean: (List<Double>) -> Option<Double> = { list ->
    when {
        list.isEmpty() -> Option()
        else -> Option(list.sum() / list.size)
    }
}

val variance: (List<Double>) -> Option<Double> = { list ->
    mean(list).flatMap { m ->
        mean(list.map { x ->
            (x - m).pow(2.0)
        })
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

    val five = Option.Some(5).orElse { Option.Some(8) }

    println(five)

    fun f(): Int? = null

//    val none: Option<Int> = Option.Some(f()).orElse { Option.Some(10) }

//    println(none)


    println("Variance: ${variance(listOf(1.0, 2.0, 5.0, 7.0, 5.5, 6.0, 8.0, 10.0, 11.4))}")
}