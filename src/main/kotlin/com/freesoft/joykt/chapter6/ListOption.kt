package com.freesoft.joykt.chapter6

import com.freesoft.joykt.chapter5.List

fun <A> sequence(list: List<Option<A>>): Option<List<A>> =
        list.foldRight(Option(List())) { x: Option<A> ->
            { y: Option<List<A>> ->
                map2(x, y) { a ->
                    { b: List<A> -> b.cons(a) }
                }
            }
        }