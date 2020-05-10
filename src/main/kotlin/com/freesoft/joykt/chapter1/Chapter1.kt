package com.freesoft.joykt.chapter1

import java.lang.IllegalArgumentException
import java.math.BigDecimal

data class CreditCard(
        private val ccNo: String
) {
    fun charge(amount: BigDecimal) {
        println("Credit Card with no. $ccNo was charged with $amount")
    }
}

data class Donut(
        val name: String = "simple donut",
        val price: BigDecimal = BigDecimal.TEN
)

data class Payment(
        val creditCard: CreditCard,
        val amount: BigDecimal
) {
    fun combine(payment: Payment): Payment =
            if (creditCard == payment.creditCard)
                Payment(creditCard, amount + payment.amount)
            else throw IllegalArgumentException("Card's don't match")

    companion object {
        fun groupByCard(payments: List<Payment>): List<Payment> =
                payments.groupBy { it.creditCard }
                        .values
                        .map { it.reduce(Payment::combine) }
    }
}

data class Purchase(
        val payment: Payment,
        val donuts: List<Donut> = emptyList()
)

fun buyDonuts(quantity: Int = 1, creditCard: CreditCard): Purchase {
    val donut = Donut()
    return Purchase(
            payment = Payment(creditCard, donut.price * BigDecimal.valueOf(quantity.toLong())),
            donuts = List(quantity) { donut }
    )
}


fun buyDonut(creditCard: CreditCard): Purchase {
    val donut = Donut()
    val payment = Payment(creditCard, donut.price)
    return Purchase(payment, listOf(donut))
}