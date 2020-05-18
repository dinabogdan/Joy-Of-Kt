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