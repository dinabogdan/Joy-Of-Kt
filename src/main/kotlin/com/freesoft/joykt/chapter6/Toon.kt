package com.freesoft.joykt.chapter6

data class Toon(
        val firstName: String,
        val lastName: String,
        val email: Option<String> = Option()
) {

    companion object {
        operator fun invoke(firstName: String, secondName: String, email: String? = null) =
                Toon(firstName, secondName, Option(email))
    }
}

fun <K, V> Map<K, V>.getOption(key: K) = Option(this[key])

fun main() {
    val toons = mapOf(
            "Mickey" to Toon("Mickey", "Mouse", "mickey@disney.com"),
            "Minnie" to Toon("Minnie", "Mouse"),
            "Donald" to Toon("Donald", "Duck", "donald@disney.com")
    )

    val mickey: Option<String> = toons.getOption("Mickey").flatMap { it.email }
    val minnie = toons.getOption("Minnie").flatMap { it.email }
    val goofy = toons.getOption("Goofy").flatMap { it.email }

    println(mickey.getOrElse { "No data" })
    println(minnie.getOrElse { "No data" })
    println(goofy.getOrElse { "No data" })

    println(mickey.orElse { Option.Some("No data from or else") })

    println(minnie.orElse { Option.Some("No data from or else") })


}