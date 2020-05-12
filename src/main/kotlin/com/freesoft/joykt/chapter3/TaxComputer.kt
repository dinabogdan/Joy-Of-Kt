package com.freesoft.joykt.chapter3

class TaxComputer(
        private val rate: Double
) {

    fun compute(price: Double): Double = price * rate + price
}

val taxRate = 0.09

val addTax = { taxRate: Double ->
    { price: Double ->
        price + price * taxRate
    }
}

fun <A, B, C> partialA(a: A, f: (A) -> (B) -> C): (B) -> C = f(a)

fun <A, B, C> partialB(b: B, f: (A) -> (B) -> C): (A) -> C = { a: A -> f(a)(b) }

fun main() {

    val tc9 = TaxComputer(0.09)
    val price = tc9.compute(12.0)

    val tc9f = addTax(taxRate)
    val pricef = tc9f(12.0)

}