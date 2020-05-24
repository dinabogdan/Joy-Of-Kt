package com.freesoft.joykt.chapter10

import com.freesoft.joykt.chapter5.List

sealed class Tree<out A : Comparable<@kotlin.UnsafeVariance A>> {

    abstract fun isEmpty(): Boolean

    operator fun plus(element: @UnsafeVariance A): Tree<A> = when (this) {
        is Empty -> T(Empty, element, Empty)
        is T -> when {
            element < this.value -> T(left + element, this.value, right)
            element > this.value -> T(left, this.value, right + element)
            else -> T(this.left, element, this.right)
        }
    }

    internal object Empty : Tree<Nothing>() {
        override fun isEmpty(): Boolean = true

        override fun toString(): String = "E"
    }

    internal class T<out A : Comparable<@kotlin.UnsafeVariance A>>(
            internal val left: Tree<A>,
            internal val value: A,
            internal val right: Tree<A>
    ) : Tree<A>() {
        override fun isEmpty(): Boolean = false

        override fun toString(): String = "(T $left $value $right)"
    }

    companion object {
        operator fun <A : Comparable<A>> invoke(): Tree<A> = Empty

        operator fun <A : Comparable<A>> invoke(vararg az: A): Tree<A> =
                az.foldRight(Empty) { a: A, tree: Tree<A> -> tree.plus(a) }

        operator fun <A : Comparable<A>> invoke(az: List<A>): Tree<A> =
                az.foldLeft(Empty as Tree<A>) { tree: Tree<A> -> { a: A -> tree.plus(a) } }

    }
}

fun main() {

    val tree = Tree<Int>() + 5 + 2 + 8

    println(tree)

    val treeFromVararg = Tree(1, 2, 3, 4)

    println(treeFromVararg)

    val treeFromList = Tree(List(1, 2, 3, 4))

    println(treeFromList)

}