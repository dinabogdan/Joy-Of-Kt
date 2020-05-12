package com.freesoft.joykt.chapter3

data class Price(val value: Double) {
    operator fun plus(price: Price) = Price(this.value + price.value)
    operator fun times(num: Int) = Price(this.value * num)
}

data class Weight(val value: Double) {
    operator fun plus(weight: Weight) = Weight(this.value + weight.value)
    operator fun times(num: Int) = Weight(this.value * num)
}

data class Product(
        val name: String,
        val price: Price,
        val weight: Weight
)

data class OrderLine(
        val product: Product,
        val count: Int
) {
    fun weight() = product.weight * count
    fun amount() = product.price * count
}

object Store {

    @JvmStatic
    fun main(args: Array<String>) {
        val toothPaste = Product("Tooth paste", 1.5, 0.5)
        val toothBrush = Product("Tooth brush", 3.5, 0.3)

        val orderLines = listOf(
                OrderLine(toothPaste, 2),
                OrderLine(toothBrush, 3)
        )

        val zeroPrice = Price(0.0)
        val zeroWeight = Weight(0.0)
        
        val weight: Weight = orderLines.fold(zeroWeight) { a, b -> a + b.weight() }
        val price: Price = orderLines.fold(zeroPrice) { a, b -> a + b.amount() }

        println("Total price: $price")
        println("Total weight: $weight")
    }
}