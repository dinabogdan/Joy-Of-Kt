package com.freesoft.joykt.chapter5

import com.freesoft.joykt.chapter5.List.Cons
import com.freesoft.joykt.chapter5.List.Nil
import com.freesoft.joykt.chapter7.Result

sealed class List<out A> {

    abstract fun isEmpty(): Boolean

    abstract fun lengthMemoized(): Int

    abstract fun headSafe(): Result<A>

    abstract fun lastSafe(): Result<A>

    abstract fun <B> foldLeft(identity: B, zero: B, f: (B) -> (A) -> B): B

    fun cons(a: @UnsafeVariance A): List<A> = Cons(a, this)

    fun setHead(a: @UnsafeVariance A): List<A> = when (this) {
        is Nil -> throw IllegalStateException("setHead call on empty list is not allowed")
        is Cons -> tail.cons(a)
    }

    fun init(): List<A> =
            when (this) {
                is Nil -> throw IllegalStateException("init call on empty list is not allowed")
                is Cons -> this.reverse().drop(1).reverse()
            }

    fun reverse(): List<A> = reverse(this, invoke())

    fun reverseFold(): List<A> = foldLeft(Nil as List<A>) { acc -> { acc.cons(it) } }

    fun <B> foldRightViaFoldLeft(identity: B, f: (A) -> (B) -> B): B =
            this.reverse().foldLeft(identity) { x -> { y -> f(y)(x) } }

    fun drop(n: Int): List<A> {
        tailrec fun drop(n: Int, list: List<A>): List<A> =
                if (n <= 0) list else when (list) {
                    is Cons -> drop(n - 1, list.tail)
                    is Nil -> list
                }
        return drop(n, this)
    }

    fun concat(list: List<@UnsafeVariance A>): List<A> = concat(this, list)

    fun dropWhile(p: (A) -> Boolean): List<A> {
        tailrec fun dropWhile_(list: List<A>): List<A> = when (list) {
            is Nil -> list
            is Cons -> if (p(list.head)) dropWhile_(list.tail) else list
        }
        return dropWhile_(this)
    }

    fun <B> coFoldRight(identity: B, f: (A) -> (B) -> B): B = coFoldRight(identity, this.reverse(), identity, f)

    fun <B> foldRight(identity: B, f: (A) -> (B) -> B): B = foldRight(this, identity, f)

    fun <B> foldLeft(identity: B, f: (B) -> (A) -> B): B = foldLeft(identity, this, f)

    fun length(): Int = foldLeft(0) { i -> { i + 1 } }

    fun <B> map(f: (A) -> B): List<B> = foldLeft(Nil) { acc: List<B> ->
        { h: A -> Cons(f(h), acc) }
    }.reverse()

    fun filter(p: (A) -> Boolean): List<A> = foldRight(Nil) { x ->
        { y: List<A> -> if (p(x)) Cons(x, y) else y }
    }

    fun <B> flatMap(f: (A) -> List<B>): List<B> = flatten(map(f))

    fun <A1, A2> unzip(f: (A) -> Pair<A1, A2>): Pair<List<A1>, List<A2>> =
            this.coFoldRight(Pair(Nil, Nil)) { a ->
                { listPair: Pair<List<A1>, List<A2>> ->
                    val pair = f(a)
                    Pair(listPair.first.cons(pair.first), listPair.second.cons(pair.second))
                }
            }

    fun getAt(index: Int): Result<A> {
        tailrec fun <A> getAt(list: List<A>, index: Int): Result<A> =
                when (list) {
                    Nil -> Result.failure("Dead code. Should never execute.")
                    is Cons -> if (index == 0) Result(list.head) else getAt(list.tail, index - 1)
                }
        return if (index < 0 || index >= length()) Result.failure("Index out of bound") else getAt(this, index)
    }

    fun getAtViaFoldLeft(index: Int): Result<A> =
            Pair(Result.failure<A>("Index out of bound"), index).let {
                if (index < 0 || index >= length()) it else foldLeft(it) { ta ->
                    { a ->
                        if (ta.second < 0) ta else Pair(Result(a), ta.second - 1)
                    }

                }
            }.first

    fun splitAt(index: Int): Pair<List<A>, List<A>> {
        tailrec fun splitAt(acc: List<A>, list: List<A>, i: Int): Pair<List<A>, List<A>> =
                when (list) {
                    Nil -> Pair(list.reverse(), acc)
                    is Cons -> if (i == 0) Pair(list.reverse(), acc) else splitAt(acc.cons(list.head), list.tail, i - 1)
                }

        return when {
            index < 0 -> splitAt(0)
            index > length() -> splitAt(length())
            else -> splitAt(Nil, this.reverse(), this.length() - index)
        }
    }

    fun hasSubList(sub: List<@UnsafeVariance A>): Boolean {
        tailrec fun <A> hasSubList(list: List<A>, sub: List<A>): Boolean =
                when (list) {
                    is Nil -> sub.isEmpty()
                    is Cons -> if (list.startsWith(sub)) true else hasSubList(list.tail, sub)
                }
        return hasSubList(this, sub)
    }

    fun startsWith(sub: List<@UnsafeVariance A>): Boolean {
        tailrec fun startsWith(list: List<A>, sub: List<A>): Boolean =
                when (sub) {
                    is Nil -> true
                    is Cons -> when (list) {
                        is Nil -> false
                        is Cons -> if (list.head == sub.head) startsWith(list.tail, sub.tail) else false
                    }
                }
        return startsWith(this, sub)
    }

    fun <B> groupBy(f: (A) -> B): Map<B, List<A>> = reverse().foldLeft(mapOf()) { mt: Map<B, List<A>> ->
        { t ->
            f(t).let {
                mt + (it to (mt.getOrDefault(it, Nil)).cons(t))
            }
        }
    }
    
    internal object Nil : List<Nothing>() {
        override fun isEmpty(): Boolean = true

        override fun toString(): String = "[NIL]"

        override fun lengthMemoized(): Int = 0

        override fun headSafe(): Result<Nothing> = Result()

        override fun lastSafe(): Result<Nothing> = Result()

        override fun <B> foldLeft(identity: B, zero: B, f: (B) -> (Nothing) -> B): B = identity
    }

    internal class Cons<A>(
            internal val head: A,
            internal val tail: List<A>
    ) : List<A>() {

        private val length: Int = tail.lengthMemoized() + 1

        override fun isEmpty(): Boolean = false

        override fun toString(): String = "[${toString("", this)}NIL]"

        override fun lengthMemoized(): Int = length

        override fun headSafe(): Result<A> = Result(head)

        override fun lastSafe(): Result<A> = foldLeft(Result()) { { y: A -> Result(y) } }

        override fun <B> foldLeft(identity: B, zero: B, f: (B) -> (A) -> B): B {
            fun <B> foldLeft(acc: B, zero: B, list: List<A>, f: (B) -> (A) -> B): B = when (list) {
                is Nil -> acc
                is Cons -> if (acc == zero) acc else foldLeft(f(acc)(list.head), zero, list.tail, f)
            }
            return foldLeft(identity, zero, this, f)
        }

        private tailrec fun toString(acc: String, list: List<A>): String =
                when (list) {
                    is Nil -> acc
                    is Cons -> toString("$acc${list.head}, ", list.tail)
                }
    }

    companion object {
        operator fun <A> invoke(vararg az: A): List<A> =
                az.foldRight(Nil as List<A>) { a: A, list: List<A> -> Cons(a, list) }

        fun <A> concat(list1: List<A>, list2: List<A>): List<A> = when (list1) {
            is Nil -> list2
            is Cons -> concat(list1.tail, list2).cons(list1.head)
        }

        tailrec fun <A> reverse(list: List<A>, acc: List<A>): List<A> =
                when (list) {
                    is Nil -> acc
                    is Cons -> reverse(list.tail, acc.cons(list.head))
                }

        fun <A, B> foldRight(list: List<A>, identity: B, f: (A) -> (B) -> B): B =
                when (list) {
                    is Nil -> identity
                    is Cons -> f(list.head)(foldRight(list.tail, identity, f))
                }

        tailrec fun <A, B> foldLeft(acc: B, list: List<A>, f: (B) -> (A) -> B): B =
                when (list) {
                    is Nil -> acc
                    is Cons -> foldLeft(f(acc)(list.head), list.tail, f)
                }

        private tailrec fun <A, B> coFoldRight(acc: B, list: List<A>, identity: B, f: (A) -> (B) -> B): B =
                when (list) {
                    is Nil -> acc
                    is Cons -> coFoldRight(f(list.head)(acc), list.tail, identity, f)
                }

        fun <A> concatViaFoldRight(list1: List<A>, list2: List<A>): List<A> =
                foldRight(list1, list2) { x -> { y -> Cons(x, y) } }

        fun <A> flatten(list: List<List<A>>): List<A> = list.foldRight(Nil) { x -> x::concat }
    }
}

fun sum(list: List<Int>): Int = when (list) {
    is Nil -> 0
    is Cons -> list.head + sum(list.tail)
}

fun product(list: List<Double>): Double = when (list) {
    is Nil -> 1.0
    is Cons -> if (list.head == 0.0) 0.0 else list.head * product(list.tail)
}


fun sumFold(list: List<Int>): Int = list.foldLeft(0) { x -> { y -> x + y } }
fun productFold(list: List<Double>): Double = list.foldLeft(1.0) { x -> { y -> x * y } }

fun triple(list: List<Int>): List<Int> = List.foldRight(list, List()) { x ->
    { y: List<Int> -> y.cons(x * 3) }
}

fun doubleToString(list: List<Double>): List<String> = List.foldRight(list, List()) { x ->
    { y: List<String> -> y.cons(x.toString()) }
}

fun main() {
    val list: List<Int> = List(1, 2, 3)

    println("The initial list: $list")

    val newList = list.cons(6)

    println("The initial list after adding 6 to it: $list")
    println("The new list obtained after adding 6 to the initial list: $newList")

    val newSetHeadList = newList.setHead(1)

    println(newSetHeadList)

    val listAfterdrop = list.drop(1)
    println(listAfterdrop)

    println(list.dropWhile { it < 3 })

    val list2 = List(4, 5, 6)

    println(list.concat(list2))

    println("Init call: ${List(1, 2, 3).init()}")

    println("Sum: ${sum(list)}")
    println("Product: ${product(List(1.0, 2.0, 3.0))}")

    println("Length of $list is ${list.length()}")

    println("Has subList: ${List(1, 2, 3, 4).hasSubList(List(3, 4))}")

}


