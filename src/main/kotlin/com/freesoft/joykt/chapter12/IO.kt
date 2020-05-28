package com.freesoft.joykt.chapter12

import com.freesoft.joykt.chapter5.List
import com.freesoft.joykt.chapter7.Result
import com.freesoft.joykt.chapter9.Lazy
import com.freesoft.joykt.chapter9.Stream
import com.freesoft.joykt.chapter9.Stream.Companion.cons
import com.freesoft.joykt.chapter9.Stream.Companion.toList
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

sealed class IO<out A> {

    internal class Return<out A>(val value: A) : IO<A>()

    internal class Suspend<out A>(val resume: () -> A) : IO<A>()

    internal class Continue<A, out B>(val sub: IO<A>, val f: (A) -> IO<B>) : IO<A>()

    fun <B> map(g: (A) -> B): IO<B> = flatMap { Return(g(it)) }

    fun <B> flatMap(g: (A) -> IO<B>): IO<B> = Continue(this, g) as IO<B>

    class IORef<A>(private var value: A) {
        fun set(a: A): IO<A> {
            value = a
            return unit(a)
        }

        fun get(): IO<A> = unit(value)

        fun modify(f: (A) -> A): IO<A> = get().flatMap { a -> set(f(a)) }
    }

    operator fun invoke(): A = invoke(this)

    operator fun invoke(io: IO<@UnsafeVariance A>): A {
        tailrec fun invokeHelper(io: IO<A>): A =
                when (io) {
                    is Return -> io.value
                    is Suspend -> io.resume()
                    else -> {
                        val ct = io as Continue<A, A>
                        val sub = ct.sub
                        val f = ct.f
                        when (sub) {
                            is Return -> invokeHelper(f(sub.value))
                            is Suspend -> invokeHelper(f(sub.resume()))
                            else -> {
                                val ct2 = sub as Continue<A, A>
                                val sub2 = ct2.sub
                                val f2 = ct2.f
                                invokeHelper(sub2.flatMap { f2(it).flatMap(f) })
                            }
                        }
                    }
                }
        return invokeHelper(io)
    }

    companion object {
        val empty: IO<Unit> = IO.Suspend { Unit }

        internal fun <A> unit(a: A): IO<A> = IO.Suspend { a }

        fun <A, B, C> map2(ioA: IO<A>, ioB: IO<B>, f: (A) -> (B) -> C): IO<C> = ioA.flatMap { a ->
            ioB.map { b -> f(a)(b) }
        }

//        fun <A> repeat(n: Int, io: IO<A>): IO<List<A>> {
//            val stream: Stream<IO<A>> = Stream.fill(n, Lazy { io })
//            val f: (A) -> (List<A>) -> List<A> = { a ->
//                { la: List<A> -> List.Cons(a, la) }
//            }
//            val g: (IO<A>) -> (Lazy<IO<List<A>>>) -> IO<List<A>> =
//                    { ioa ->
//                        { sioLa ->
//                            map2(ioa, sioLa(), f)
//                        }
//                    }
//            val z: Lazy<IO<List<A>>> = Lazy { IO { List<A>() } }
//            return stream.foldRight(z, g)
//        }

        fun <A, B> forever(ioA: IO<A>): IO<B> {
            val t: () -> IO<B> = { forever(ioA) }
            return ioA.flatMap { t() }
        }


//        operator fun <A> invoke(a: A): IO<A> = IO.Suspend { a }
    }

}

//object Console {
//    private val br = BufferedReader(InputStreamReader(System.`in`))
//
//    fun readln(): IO<Result<String>> = IO {
//        try {
//            Result(br.readLine())
//        } catch (ex: IOException) {
//            Result.failure<String>(ex)
//        }
//    }
//
//    fun println(o: Any): IO<Unit> = IO {
//        kotlin.io.println(o.toString())
//    }
//
//    fun print(o: Any): IO<Unit> = IO {
//        kotlin.io.print(o.toString())
//    }
//}

object Console {
    private val br = BufferedReader(InputStreamReader(System.`in`))

    fun readLine(): IO<Result<String>> = IO.Suspend {
        try {
            Result(br.readLine())
        } catch (ex: IOException) {
            Result.failure<String>(ex)
        }
    }

    fun printLine(s: Any): IO<Unit> = IO.Suspend { println(s) }

    fun print(s: Any): IO<Unit> = IO.Suspend<Unit> { print(s) }
}

fun show(message: String): IO<Unit> = Console.printLine(message)

fun <A> toString(rd: Result<A>): String = rd.map { it.toString() }.getOrElse(rd.toString())

fun inverse(i: Int): Result<Double> = when (i) {
    0 -> Result.failure("Div by 0")
    else -> Result(1.0 / i)
}

val computation: IO<Unit> = show(toString(inverse(3)))

fun getName() = "Mickey"

//val instruction1 = IO { print("Hello, ") }
//val instruction2 = IO { print(getName()) }
//val instruction3 = IO { print("!\n") }

//val script: IO<Unit> = instruction1 + instruction2 + instruction3

private fun sayHello(): IO<Unit> = Console.print("Enter your name: ")
        .map { Console.readLine()() }
        .map { "Hello, ${it.getOrElse("")}!" }
        .map { Console.printLine(it)() }

private fun sayFunctionalHellO(): IO<Unit> = Console.print("Enter your name: ")
        .flatMap { Console.readLine() }
        .map { "Hello, ${it.getOrElse("")}!" }
        .flatMap { Console.printLine(it) }

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

//    val script = sayFunctionalHellO()
//
//    script()

//    val program = IO.forever<String, String>(IO { "Hi again!!" })
//            .flatMap { Console.println(it) }
//
//    program()


}