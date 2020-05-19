package com.freesoft.joykt.chapter9

import java.lang.IllegalStateException
import kotlin.random.Random
import com.freesoft.joykt.chapter5.List

class Lazy<out A>(function: () -> A) : () -> A {
    private val value: A by lazy(function)

    override operator fun invoke(): A = value

    override fun toString(): String = "$value"

    fun <B> map(f: (A) -> B): Lazy<B> = Lazy { f(value) }

    fun <B> flatMap(f: (A) -> Lazy<B>): Lazy<B> = f(value)

    companion object {

        val lift2: ((String) -> (String) -> String) -> (Lazy<String>) -> (Lazy<String>) -> Lazy<String> =
                { f ->
                    { lz1 ->
                        { lz2 -> Lazy { f(lz1())(lz2()) } }
                    }
                }
    }
}

fun <A> sequence(list: List<Lazy<A>>): Lazy<List<A>> = Lazy { list.map { it() } }

fun <A, B, C> lift2(f: (A) -> (B) -> C): (Lazy<A>) -> (Lazy<B>) -> Lazy<C> =
        { lz1 ->
            { lz2 -> Lazy { f(lz1())(lz2()) } }
        }

fun or(a: Lazy<Boolean>, b: Lazy<Boolean>): Boolean = if (a()) true else b()

fun constructMessage(greetings: String, name: String): String = "$greetings, $name!"

fun lazyConstructMessage(greetings: Lazy<String>, name: Lazy<String>): Lazy<String> = Lazy { "${greetings()}, ${name()}!" }

val lazyCurriedConstructMessage: (Lazy<String>) -> (Lazy<String>) -> Lazy<String> =
        { greetings ->
            { name -> Lazy { "${greetings()}, ${name()}!" } }
        }

fun greetings(): String {
    println("Eager Evaluating greetings")
    return "Hello"
}

fun name(): String {
    println("Eager computing name")
    return "Mickey"
}

val lazyGreetings = Lazy {
    println("Lazy Evaluating greetings")
    "Hello"
}

val lazyName: Lazy<String> = Lazy {
    println("Lazy computing name")
    "Mickey"
}

val lazyDefaultMessage = Lazy {
    println("Lazy Evaluating default message")
    "No greetings when time is odd"
}

val consMessage: (String) -> (String) -> String =
        { greetings ->
            { name -> "$greetings, $name!" }
        }

fun main() {

    val first = Lazy {
        println("evaluating first")
        true
    }

    val second = Lazy {
        println("evaluating second")
        throw IllegalStateException()
    }

    println(first() || second())
    println(first() || second())
    println(or(first, second))

    val lazyMessage = lazyConstructMessage(lazyGreetings, lazyName)
    val eagerMessage = constructMessage(greetings(), name())
    val lazyCurriedMessage = lazyCurriedConstructMessage(lazyGreetings)(lazyName)
    val condition = Random(System.currentTimeMillis()).nextInt() % 2 == 0

    // the following println() will call the greetings() and name() function even if the condition is not true
    println(if (condition) println(eagerMessage) else "No greetings when time is odd")

    // the following println() will not evaluate the lazyGreetings and lazyName if the condition is not true
    println(if (condition) println(lazyMessage) else "No greetings when time is odd")

    // the following println() will not evaluate the lazyGreetings and lazyName if the condition is not true
    println(if (condition) println(lazyCurriedMessage) else "No greetings when time is odd")

    val greets: (String) -> String = { "Hello, $it" }

    val message = lazyName.map(greets)

    println(if (condition) message() else lazyDefaultMessage())
    println(if (condition) message() else lazyDefaultMessage())

    val name1 = Lazy {
        println("Evaluating name1")
        "John"
    }

    val name2 = Lazy {
        println("Evaluating name2")
        "Jane"
    }

    val name3 = Lazy {
        println("Evaluating name3")
        "Joe"
    }

    val list = sequence(List(name1, name2, name3))
    val eagerDefaultMessage = "No greetings when time is odd"

    println("Lazy + List")

    println(if (condition) list() else eagerDefaultMessage)
    println(if (condition) list() else eagerDefaultMessage)

}