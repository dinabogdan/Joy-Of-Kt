package com.freesoft.joykt.chapter10

import com.freesoft.joykt.chapter5.List
import com.freesoft.joykt.chapter7.Result
import java.lang.Integer.max

sealed class Tree<out A : Comparable<@kotlin.UnsafeVariance A>> {

    abstract val size: Int
    abstract val height: Int

    abstract fun isEmpty(): Boolean
    abstract fun size(): Int
    abstract fun height(): Int
    abstract fun min(): Result<A>
    abstract fun max(): Result<A>
    abstract fun merge(tree: Tree<@UnsafeVariance A>): Tree<A>
    abstract fun <B> foldLeft(identity: B,
                              f: (B) -> (A) -> B,
                              g: (B) -> (B) -> B): B

    abstract fun <B> foldRight(identity: B,
                               f: (A) -> (B) -> B,
                               g: (B) -> (B) -> B): B

    abstract fun <B> foldInOrder(identity: B, f: (B) -> (A) -> (B) -> B): B
    abstract fun <B> foldPreOrder(identity: B, f: (A) -> (B) -> (B) -> B): B
    abstract fun <B> foldPostOrder(identity: B, f: (B) -> (B) -> (A) -> B): B

    protected abstract fun rotateRight(): Tree<A>
    protected abstract fun rotateLeft(): Tree<A>

    fun contains(a: @UnsafeVariance A): Boolean = when (this) {
        is Empty -> false
        is T -> when {
            a < value -> left.contains(a)
            a > value -> right.contains(a)
            else -> value == a
        }
    }

    fun removeMerge(ta: Tree<@UnsafeVariance A>): Tree<A> = when (this) {
        is Empty -> ta
        is T -> when (ta) {
            is Empty -> this
            is T -> when {
                ta.value < value -> T(left.removeMerge(ta), value, right)
                else -> T(left, value, right.removeMerge(ta))
            }
        }
    }

    fun remove(a: @UnsafeVariance A): Tree<A> = when (this) {
        is Empty -> this
        is T -> when {
            a < value -> T(left.remove(a), value, right)
            a > value -> T(left, value, right.remove(a))
            else -> left.removeMerge(right)
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

    fun <B : Comparable<B>> map(f: (A) -> B): Tree<B> =
            foldInOrder(Empty) { t1: Tree<B> ->
                { i: A ->
                    { t2: Tree<B> ->
                        Tree(t1, f(i), t2)
                    }
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

        override fun min(): Result<Nothing> = Result.Empty

        override fun max(): Result<Nothing> = Result.Empty

        override fun merge(tree: Tree<Nothing>): Tree<Nothing> = tree

        override fun <B> foldLeft(identity: B, f: (B) -> (Nothing) -> B, g: (B) -> (B) -> B): B = identity

        override fun <B> foldRight(identity: B, f: (Nothing) -> (B) -> B, g: (B) -> (B) -> B): B = identity

        override fun <B> foldInOrder(identity: B, f: (B) -> (Nothing) -> (B) -> B): B = identity

        override fun <B> foldPreOrder(identity: B, f: (Nothing) -> (B) -> (B) -> B): B = identity

        override fun <B> foldPostOrder(identity: B, f: (B) -> (B) -> (Nothing) -> B): B = identity

        override fun rotateRight(): Tree<Nothing> = this

        override fun rotateLeft(): Tree<Nothing> = this
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

        override fun size(): Int = this.size

        override fun height(): Int = this.height

        override fun min(): Result<A> = left.min().orElse { Result(value) }

        override fun max(): Result<A> = right.max().orElse { Result(value) }

        override fun merge(tree: Tree<@UnsafeVariance A>): Tree<A> = when (tree) {
            is Empty -> this
            is T -> when {
                tree.value > this.value -> T(left, value, right.merge(T(Empty, tree.value, tree.right))).merge(tree.left)
                tree.value < this.value -> T(left.merge(T(tree.left, tree.value, Empty)), value, right).merge(tree.right)
                else -> T(left.merge(tree.left), value, right.merge(tree.right))
            }
        }

        override fun <B> foldLeft(identity: B, f: (B) -> (A) -> B, g: (B) -> (B) -> B): B =
                g(right.foldLeft(identity, f, g))(f(left.foldLeft(identity, f, g))(this.value))

        override fun <B> foldRight(identity: B, f: (A) -> (B) -> B, g: (B) -> (B) -> B): B =
                g(f(this.value)(left.foldRight(identity, f, g)))(right.foldRight(identity, f, g))

        override fun <B> foldInOrder(identity: B, f: (B) -> (A) -> (B) -> B): B =
                f(left.foldInOrder(identity, f))(value)(right.foldInOrder(identity, f))

        override fun <B> foldPreOrder(identity: B, f: (A) -> (B) -> (B) -> B): B =
                f(value)(left.foldPreOrder(identity, f))(right.foldPreOrder(identity, f))

        override fun <B> foldPostOrder(identity: B, f: (B) -> (B) -> (A) -> B): B =
                f(left.foldPostOrder(identity, f))(right.foldPostOrder(identity, f))(value)

        override fun rotateRight(): Tree<A> = when (left) {
            is Empty -> this
            is T -> T(left.left, left.value, T(left.right, value, right))
        }

        override fun rotateLeft(): Tree<A> = when (right) {
            is Empty -> this
            is T -> T(T(left, value, right.left), right.value, right.right)
        }
    }

    companion object {
        operator fun <A : Comparable<A>> invoke(): Tree<A> = Empty

        operator fun <A : Comparable<A>> invoke(vararg az: A): Tree<A> =
                az.foldRight(Empty) { a: A, tree: Tree<A> -> tree.plus(a) }

        operator fun <A : Comparable<A>> invoke(az: List<A>): Tree<A> =
                az.foldLeft(Empty as Tree<A>) { tree: Tree<A> -> { a: A -> tree.plus(a) } }

        operator fun <A : Comparable<A>> invoke(left: Tree<A>, a: A, right: Tree<A>): Tree<A> =
                when {
                    ordered(left, a, right) -> T(left, a, right)
                    ordered(right, a, left) -> T(right, a, left)
                    else -> Tree(a).merge(left).merge(right)
                }

    }
}

fun <A : Comparable<A>> lt(first: A, second: A): Boolean = first < second
fun <A : Comparable<A>> lt(first: A, second: A, third: A): Boolean = lt(first, second) && lt(second, third)

fun <A : Comparable<A>> ordered(left: Tree<A>, a: A, right: Tree<A>): Boolean =
        (left.max().flatMap { leftMax ->
            right.min().map { rightMin -> lt(leftMax, a, rightMin) }
        }.getOrElse(left.isEmpty() && right.isEmpty()) ||
                left.min().mapEmpty()
                        .flatMap {
                            right.min().map { rightMin ->
                                lt(a, rightMin)
                            }
                        }.getOrElse(false) ||
                right.min()
                        .mapEmpty()
                        .flatMap {
                            left.max().map { leftMax ->
                                lt(leftMax, a)
                            }
                        }.getOrElse(false)
                )

fun main() {

    val tree = Tree<Int>() + 5 + 2 + 8

    println(tree)
    println("Tree after removing ${tree.remove(5)}")

    val treeFromVararg = Tree(1, 2, 3, 4)

    println(treeFromVararg)

    val treeFromList = Tree(List(1, 2, 3, 4))

    println("Tree from list:  $treeFromList")

    println("Tree from list after removing root: ")

    println(tree.size())
    println(treeFromVararg.size())

    println(tree.height())

    println(treeFromList.height())

    println("The max of tree is: ${tree.max()}")
    println("The min of tree is: ${tree.min()}")


}