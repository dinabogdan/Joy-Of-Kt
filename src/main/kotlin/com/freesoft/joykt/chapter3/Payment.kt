package com.freesoft.joykt.chapter3

import com.freesoft.joykt.chapter1.CreditCard
import com.freesoft.joykt.chapter1.Payment
import java.lang.IllegalArgumentException
import java.math.BigDecimal

// an alternative to the combine function from Payment class
fun combine(payment1: Payment, payment2: Payment): Payment =
        if (payment1.creditCard == payment2.creditCard)
            Payment(payment1.creditCard, payment1.amount + payment2.amount)
        else throw IllegalArgumentException("Cards don't  match ")

fun main() {
    val creditCard = CreditCard("123")
    val payment1 = Payment(creditCard, BigDecimal.TEN)
    val payment2 = Payment(creditCard, BigDecimal.ONE)
    val newPayment = combine(payment1, payment2)

    val anotherNewPayment = payment1.combine(payment2).combine(payment2)

    println("New payment: $newPayment")
    println("Another new payment: $anotherNewPayment")
}