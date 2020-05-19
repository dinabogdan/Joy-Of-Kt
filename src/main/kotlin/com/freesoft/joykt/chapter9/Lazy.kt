package com.freesoft.joykt.chapter9

import java.lang.IllegalStateException
import kotlin.random.Random

class Lazy<out A>(function: () -> A) : () -> A {
    private val value: A by lazy(function)

    override operator fun invoke(): A = value

    override fun toString(): String = "$value"

    companion object {

        val lift2: ((String) -> (String) -> String) -> (Lazy<String>) -> (Lazy<String>) -> Lazy<String> =
                { f ->
                    { lz1 ->
                        { lz2 -> Lazy { f(lz1())(lz2()) } }
                    }
                }

    }
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


}