package com.freesoft.joykt.chapter12

import com.freesoft.joykt.chapter5.List
import com.freesoft.joykt.chapter7.Result

class IO<out A>(private val effect: () -> A) {
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

fun main() {
    script()

    val imperativeScript = List(
            IO { print("Hello, ") },
            IO { print(getName()) },
            IO { print("!\n") }
    )

    val program = imperativeScript.foldRight(IO.empty) { io -> { io + it } }

    program()
}