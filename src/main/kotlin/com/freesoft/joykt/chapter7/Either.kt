package com.freesoft.joykt.chapter7

import com.freesoft.joykt.chapter5.List

sealed class Either<out A, out B> {

    abstract fun <C> map(f: (B) -> C): Either<A, C>

    abstract fun <C> flatMap(f: (B) -> Either<@UnsafeVariance A, C>): Either<A, C>

    fun getOrElse(defaultValue: () -> @UnsafeVariance B): B = when (this) {
        is Right -> this.value
        is Left -> defaultValue()
    }

    fun orElse(defaultValue: () -> @UnsafeVariance Either<@UnsafeVariance A, @UnsafeVariance B>): Either<A, B> =
            map { this }.getOrElse(defaultValue)

    internal class Left<out A, out B>(internal val value: A) : Either<A, B>() {
        override fun toString(): String = "Left($value)"

        override fun <C> map(f: (B) -> C): Either<A, C> = Left(value)

        override fun <C> flatMap(f: (B) -> Either<@UnsafeVariance A, C>): Either<A, C> = Left(value)
    }

    internal class Right<out A, out B>(internal val value: B) : Either<A, B>() {
        override fun toString(): String = "Right($value)"

        override fun <C> map(f: (B) -> C): Either<A, C> = Right(f(value))

        override fun <C> flatMap(f: (B) -> Either<@UnsafeVariance A, C>): Either<A, C> = f(value)
    }

    companion object {
        fun <A, B> left(value: A): Either<A, B> = Left(value)
        fun <A, B> right(value: B): Either<A, B> = Right(value)
    }
}

fun <A : Comparable<A>> max(list: List<A>): Either<String, A> = when (list) {
    is List.Nil -> Either.left("max called on an empty list")
    is List.Cons -> Either.right(list.foldLeft(list.head) { x ->
        { y ->
            if (x.compareTo(y) == 0) x else y
        }
    })
}