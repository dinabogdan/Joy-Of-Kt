package com.freesoft.joykt.chapter3

class FunFunctions {
    var percent1 = 5
    private var percent2 = 9
    val percent3 = 13

    // pure function
    fun add(a: Int, b: Int): Int = a + b

    //pure function
    fun mult(a: Int, b: Int?): Int = 5

    //impure function, it will throw an exception when b = 0
    fun div(a: Int, b: Int): Int = a / b

    // pure function
    fun div(a: Double, b: Double): Double = a / b

    // impure function
    fun applyTax1(a: Int): Int = a / 100 * (100 + percent1)

    // pure function because the percent2 it's private
    fun applyTax2(a: Int): Int = a / 100 * (100 + percent2)

    // pure function because percen3 it's immutable
    fun applyTax3(a: Int): Int = a / 100 * (100 + percent3)

    // impure function
    fun append(i: Int, list: MutableList<Int>): List<Int> {
        list.add(i)
        return list
    }


}

fun main() {
    val funFunctions = FunFunctions()

    println(funFunctions.mult(4, null))

    println(funFunctions.div(1.0, 0.0))
}