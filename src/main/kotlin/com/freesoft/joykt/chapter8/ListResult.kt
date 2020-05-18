package com.freesoft.joykt.chapter8

import com.freesoft.joykt.chapter5.List
import com.freesoft.joykt.chapter7.Result
import com.freesoft.joykt.chapter7.map2

fun <A> flattenResult(list: List<Result<A>>): List<A> =
        list.flatMap { result -> result.map { List(it) }.getOrElse(List()) }

fun <A> sequence(list: List<Result<A>>): Result<List<A>> =
        list.foldRight(Result(List())) { x ->
            { y: Result<List<A>> ->
                map2(x, y) { a -> { b: List<A> -> b.cons(a) } }
            }
        }

fun <A> sequence2(list: List<Result<A>>): Result<List<A>> =
        list.filter { !it.isEmpty() }
                .foldRight(Result(List())) { x ->
                    { y: Result<List<A>> ->
                        map2(x, y) { a ->
                            { b: List<A> -> b.cons(a) }
                        }
                    }
                }

fun <A, B> traverse(list: List<A>, f: (A) -> Result<B>): Result<List<B>> =
        list.foldRight(Result(List())) { x ->
            { y: Result<List<B>> ->
                map2(f(x), y) { a -> { b: List<B> -> b.cons(a) } }
            }
        }

fun <A, B, C> zipWith(list1: List<A>,
                      list2: List<B>,
                      f: (A) -> (B) -> C): List<C> {
    tailrec fun zipWith(acc: List<C>,
                        list1: List<A>,
                        list2: List<B>): List<C> = when (list1) {
        List.Nil -> acc
        is List.Cons -> when (list2) {
            is List.Nil -> acc
            is List.Cons -> zipWith(
                    acc.cons(f(list1.head)(list2.head)),
                    list1.tail,
                    list2.tail)
        }
    }
    return zipWith(List(), list1, list2)
}

fun <A, B, C> product(list1: List<A>,
                      list2: List<B>,
                      f: (A) -> (B) -> C): List<C> =
        list1.flatMap { a -> list2.map { b -> f(a)(b) } }

fun <A, B> unzip(list: List<Pair<A, B>>): Pair<List<A>, List<B>> =
        list.coFoldRight(Pair(List(), List())) { pair ->
            { listPair: Pair<List<A>, List<B>> ->
                Pair(listPair.first.cons(pair.first), listPair.second.cons(pair.second))
            }
        }

fun main() {
    println(product(List(1, 2), List(4, 5, 6)) { x -> { y: Int -> Pair(x, y) } })
    println(zipWith(List(1, 2), List(4, 5, 6)) { x -> { y: Int -> Pair(x, y) } })
}