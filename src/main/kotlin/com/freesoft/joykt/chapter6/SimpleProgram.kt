package com.freesoft.joykt.chapter6

data class Person(val name: String)

fun main() {
    val map: Map<String, Person> = mapOf("Joe" to Person("Joe"))

//  this will not compile because map[] is syntactic sugar over map.get() which returns a nullable 'Type?'
//    val person: Person = map["Joe"]
    val person = map["Joe"]
    val person2 = map["John"]

}