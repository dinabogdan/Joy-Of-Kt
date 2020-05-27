package com.freesoft.joykt.chapter12

import java.io.Closeable
import com.freesoft.joykt.chapter5.List
import com.freesoft.joykt.chapter7.Result
import com.freesoft.joykt.chapter9.Stream
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

interface Input : Closeable {

    fun readString(): Result<Pair<String, Input>>

    fun readInt(): Result<Pair<Int, Input>>

    fun readString(message: String): Result<Pair<String, Input>> = readString()

    fun readInt(message: String): Result<Pair<Int, Input>> = readInt()

}

abstract class AbstractReader(
        private val reader: BufferedReader
) : Input {
    override fun readString(): Result<Pair<String, Input>> = try {
        reader.readLine().let {
            when (it.isEmpty()) {
                true -> Result()
                false -> Result(Pair(it, this))
            }
        }
    } catch (ex: Exception) {
        Result.failure(ex)
    }

    override fun readInt(): Result<Pair<Int, Input>> = try {
        reader.readLine().let {
            when (it.isEmpty()) {
                true -> Result()
                false -> Result(Pair(it.toInt(), this))
            }
        }
    } catch (ex: Exception) {
        Result.failure(ex)
    }

    override fun close() = reader.close()
}

class ConsoleReader(reader: BufferedReader) : AbstractReader(reader) {
    override fun readString(message: String): Result<Pair<String, Input>> {
        print("$message ")
        return readString()
    }

    override fun readInt(message: String): Result<Pair<Int, Input>> {
        print("$message ")
        return readInt()
    }

    companion object {
        operator fun invoke(): ConsoleReader = ConsoleReader(
                BufferedReader(
                        InputStreamReader(System.`in`)
                )
        )
    }
}

data class Person(val id: Int, val firstName: String, val lastName: String)

fun person(input: Input): Result<Pair<Person, Input>> = input.readInt("Enter ID:")
        .flatMap { id ->
            id.second.readString("Enter first name:")
                    .flatMap { firstName ->
                        firstName.second.readString("Enter last name:")
                                .map { lastName ->
                                    Pair(Person(id.first,
                                            firstName.first,
                                            lastName.first), lastName.second)
                                }
                    }
        }

fun readPersonsFromConsole(): List<Person> = Stream.unfold(ConsoleReader(), ::person).toList()

class FileReader private constructor(
        private val reader: BufferedReader
) : AbstractReader(reader), AutoCloseable {

    override fun close() {
        reader.close()
    }

    companion object {
        operator fun invoke(path: String): Result<Input> = try {
            Result(
                    FileReader(
                            File(path).bufferedReader()
                    )
            )
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}

fun readPersonsFromFile(path: String): Result<List<Person>> =
        FileReader(path).map {
            it.use {
                Stream.unfold(it, ::person).toList()
            }
        }

fun main() {
//    val input = ConsoleReader()
//
//    val rString = input.readString("Enter your name:").map { t -> t.first }
//
//    val nameMessage = rString.map { "Hello $it!" }
//
//    nameMessage.forEach(::println, onFailure = { println(it.message) })
//
//    val rInt = input.readInt("Enter your age: ").map { t -> t.first }
//
//    val ageMessage = rInt.map { "You look younger than $it!" }
//
//    ageMessage.forEach(::println, onFailure = { println("Invalid age. Please enter an integer") })
//
//    readPersonsFromConsole().forEach(::println)

    val path = "C:\\Users\\HR11MD\\workspace\\the-joy-of-kt\\src\\main\\kotlin\\com\\freesoft\\joykt\\data.txt"

    readPersonsFromFile(path).forEach({ list: List<Person> -> list.forEach(::println) }, onFailure = ::println)
}