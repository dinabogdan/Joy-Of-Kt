package com.freesoft.joykt.chapter7

import com.freesoft.joykt.chapter6.Option
import java.io.IOException
import java.io.Serializable
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.lang.NullPointerException
import java.lang.RuntimeException

sealed class Result<out A> : Serializable {

    abstract fun <B> map(f: (A) -> B): Result<B>

    abstract fun <B> flatMap(f: (A) -> Result<B>): Result<B>

    abstract fun toOption(): Option<A>

    abstract fun mapFailure(message: String): Result<A>

    abstract fun forEach(effect: (A) -> Unit)

    abstract fun forEach(onSuccess: (A) -> Unit = {},
                         onFailure: (RuntimeException) -> Unit = {},
                         onEmpty: () -> Unit = {})

    fun getOrElse(defaultValue: @UnsafeVariance A): A = when (this) {
        is Success -> this.value
        else -> defaultValue
    }

    fun orElse(defaultValue: () -> Result<@UnsafeVariance A>): Result<A> = when (this) {
        is Success -> this
        else -> try {
            defaultValue()
        } catch (ex: RuntimeException) {
            failure<A>(ex)
        } catch (ex: Exception) {
            failure<A>(RuntimeException(ex))
        }
    }

    fun filter(p: (A) -> Boolean): Result<A> = flatMap {
        if (p(it))
            this
        else
            failure("Condition not matched")
    }

    fun filter(message: String, p: (A) -> Boolean): Result<A> = flatMap {
        if (p(it))
            this
        else
            failure(message)
    }

    fun exists(p: (A) -> Boolean): Boolean = map(p).getOrElse(false)

    internal class Failure<out A>(
            internal val exception: RuntimeException
    ) : Result<A>() {

        override fun toString(): String = "Failure(${exception.message})"

        override fun <B> map(f: (A) -> B): Result<B> = Failure(exception)

        override fun <B> flatMap(f: (A) -> Result<B>): Result<B> = Failure(exception)

        override fun toOption(): Option<A> = Option()

        override fun mapFailure(message: String): Result<A> = Failure(RuntimeException(message, exception))

        override fun forEach(effect: (A) -> Unit) {}

        override fun forEach(onSuccess: (A) -> Unit, onFailure: (RuntimeException) -> Unit, onEmpty: () -> Unit) {
            onFailure(exception)
        }
    }

    internal class Success<out A>(
            internal val value: A
    ) : Result<A>() {
        override fun toString(): String = "Success($value)"

        override fun <B> map(f: (A) -> B): Result<B> = try {
            Success(f(value))
        } catch (ex: RuntimeException) {
            Failure(ex)
        } catch (ex: Exception) {
            Failure(RuntimeException(ex))
        }

        override fun <B> flatMap(f: (A) -> Result<B>): Result<B> = try {
            f(value)
        } catch (ex: RuntimeException) {
            Failure(ex)
        } catch (ex: Exception) {
            Failure(RuntimeException(ex))
        }

        override fun toOption(): Option<A> = Option(value)

        override fun mapFailure(message: String): Result<A> = this

        override fun forEach(effect: (A) -> Unit) {
            effect(value)
        }

        override fun forEach(onSuccess: (A) -> Unit, onFailure: (RuntimeException) -> Unit, onEmpty: () -> Unit) {
            onSuccess(value)
        }
    }

    internal object Empty : Result<Nothing>() {
        override fun <B> map(f: (Nothing) -> B): Result<B> = Empty

        override fun <B> flatMap(f: (Nothing) -> Result<B>): Result<B> = Empty

        override fun toOption(): Option<Nothing> = Option()

        override fun toString(): String = "Empty"

        override fun mapFailure(message: String): Result<Nothing> = this

        override fun forEach(effect: (Nothing) -> Unit) {}

        override fun forEach(onSuccess: (Nothing) -> Unit, onFailure: (RuntimeException) -> Unit, onEmpty: () -> Unit) {
            onEmpty()
        }
    }

    companion object {
        operator fun <A> invoke(a: A? = null): Result<A> =
                when (a) {
                    null -> Failure(NullPointerException())
                    else -> Success(a)
                }

        operator fun <A> invoke(): Result<A> = Empty

        operator fun <A> invoke(a: A? = null, message: String): Result<A> = when (a) {
            null -> Failure(NullPointerException(message))
            else -> Success(a)
        }

        operator fun <A> invoke(a: A? = null, p: (A) -> Boolean): Result<A> = when (a) {
            null -> Failure(NullPointerException())
            else -> when {
                p(a) -> Success(a)
                else -> Empty
            }
        }

        operator fun <A> invoke(a: A? = null, message: String, p: (A) -> Boolean): Result<A> = when (a) {
            null -> Failure(NullPointerException())
            else -> when {
                p(a) -> Success(a)
                else -> Failure(IllegalArgumentException("Argument $a does not match condition: $message"))
            }
        }

        fun <A> failure(message: String): Result<A> = Failure(IllegalStateException(message))

        fun <A> failure(exception: RuntimeException): Result<A> = Failure(exception)

        fun <A> failure(exception: Exception): Result<A> = Failure(IllegalStateException(exception))

    }
}

fun <K, V> Map<K, V>.getResult(key: K) = when {
    this.containsKey(key) -> Result(this[key])
    else -> Result.Empty
}

data class Toon private constructor(
        val firstName: String,
        val lastName: String,
        val email: Result<String>
) {
    companion object {
        operator fun invoke(
                firstName: String,
                lastName: String) = Toon(firstName, lastName, Result.Empty)

        operator fun invoke(
                firstName: String,
                lastName: String,
                email: String) = Toon(firstName, lastName, Result(email))
    }
}

fun main() {
    val toons: Map<String, Toon> = mapOf(
            "Mickey" to Toon("Mickey", "Mouse", "mickey@disney.com"),
            "Minnie" to Toon("Minnie", "Mouse"),
            "Donald" to Toon("Donald", "Duck", "donald@disney.com")
    )

    val toon = getName()
            .flatMap { toons.getResult(it) }
            .flatMap { it.email }

    println(toon)

    val z = 5

    val result = if (z % 2 == 0) Result(z) else Result()

    result.forEach(
            onSuccess = { println("$it is even") },
            onEmpty = { println("This one is odd") }
    )
}

fun getName(): Result<String> = try {
    validate(readLine())
} catch (e: IOException) {
    Result.failure(e)
}

fun validate(name: String?): Result<String> = when {
    name?.isNotEmpty() ?: false -> Result(name)
    else -> Result.failure(IOException())
}