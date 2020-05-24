package com.freesoft.joykt.chapter10

import com.freesoft.joykt.chapter5.List
import java.lang.Integer.max

sealed class Tree<out A : Comparable<@kotlin.UnsafeVariance A>> {

    abstract val size: Int
    abstract val height: Int

    abstract fun isEmpty(): Boolean

    abstract fun size(): Int

    abstract fun height(): Int

    fun contains(a: @UnsafeVariance A): Boolean = when (this) {
        is Empty -> false
        is T -> when {
            a < value -> left.contains(a)
            a > value -> right.contains(a)
            else -> value == a
        }
    }

    operator fun plus(element: @UnsafeVariance A): Tree<A> = when (this) {
        is Empty -> T(Empty, element, Empty)
        is T -> when {
            element < this.value -> T(left + element, this.value, right)
            element > this.value -> T(left, this.value, right + element)
            else -> T(this.left, element, this.right)
        }
    }

    internal object Empty : Tree<Nothing>() {

        override val size: Int
            get() = 0
        override val height: Int
            get() = -1

        override fun isEmpty(): Boolean = true

        override fun toString(): String = "E"

        override fun size(): Int = 0

        override fun height(): Int = -1
    }

    internal class T<out A : Comparable<@kotlin.UnsafeVariance A>>(
            internal val left: Tree<A>,
            internal val value: A,
            internal val right: Tree<A>
    ) : Tree<A>() {

        override val size: Int
            get() = 1 + left.size + right.size
        override val height: Int
            get() = 1 + max(left.height, right.height)

        override fun isEmpty(): Boolean = false

        override fun toString(): String = "(T $left $value $right)"

        override fun size(): Int = 1 + this.left.size() + this.right.size()

        override fun height(): Int = 1 + max(this.left.height(), this.right.size())
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

    println(tree.size())
    println(treeFromVararg.size())

    println(tree.height())

    println(treeFromList.height())

}