package com.freesoft.joykt.chapter12

import com.freesoft.joykt.chapter5.List
import com.freesoft.joykt.chapter7.Result
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class IO<out A>(private val effect: () -> A) {

    fun <B> map(g: (A) -> B): IO<B> = IO { g(this()) }

    fun <B> flatMap(g: (A) -> IO<B>): IO<B> = IO {
        g(this())()
    }

    operator fun invoke() = effect()

    operator fun plus(io: IO<@UnsafeVariance A>): IO<A> = IO {
        effect()
        io.effect()
    }

    companion object {
        val empty: IO<Unit> = IO {}

        operator fun <A> invoke(a: A): IO<A> = IO { a }
    }

}

object Console {
    private val br = BufferedReader(InputStreamReader(System.`in`))

    fun readln(): IO<Result<String>> = IO {
        try {
            Result(br.readLine())
        } catch (ex: IOException) {
            Result.failure<String>(ex)
        }
    }

    fun println(o: Any): IO<Unit> = IO {
        kotlin.io.println(o.toString())
    }

    fun print(o: Any): IO<Unit> = IO {
        kotlin.io.print(o.toString())
    }
}

fun show(message: String): IO<Unit> = IO { println(message) }

fun <A> toString(rd: Result<A>): String = rd.map { it.toString() }.getOrElse(rd.toString())

fun inverse(i: Int): Result<Double> = when (i) {
    0 -> Result.failure("Div by 0")
    else -> Result(1.0 / i)
}

val computation: IO<Unit> = show(toString(inverse(3)))

fun getName() = "Mickey"

val instruction1 = IO { print("Hello, ") }
val instruction2 = IO { print(getName()) }
val instruction3 = IO { print("!\n") }

val script: IO<Unit> = instruction1 + instruction2 + instruction3

private fun sayHello(): IO<Unit> = Console.print("Enter your name: ")
        .map { Console.readln()() }
        .map { "Hello, ${it.getOrElse("")}!" }
        .map { Console.println(it)() }

private fun sayFunctionalHellO(): IO<Unit> = Console.print("Enter your name: ")
        .flatMap { Console.readln() }
        .map { "Hello, ${it.getOrElse("")}!" }
        .flatMap { Console.println(it) }

fun main() {
//    script()
//
//    val imperativeScript = List(
//            IO { print("Hello, ") },
//            IO { print(getName()) },
//            IO { print("!\n") }
//    )
//
//    val program = imperativeScript.foldRight(IO.empty) { io -> { io + it } }
//
//    program()

//    val script = sayHello()

    val script = sayFunctionalHellO()

    script()


}