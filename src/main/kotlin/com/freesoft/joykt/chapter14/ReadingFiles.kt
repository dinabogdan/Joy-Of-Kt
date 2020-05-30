package com.freesoft.joykt.chapter14

import java.util.*
import com.freesoft.joykt.chapter7.Result
import java.io.IOException
import java.lang.NumberFormatException
import java.lang.invoke.MethodHandles

class PropertyReader(configFileName: String) {


// this code works, but it relies on Java reading file mechanism which can throw exceptions
//    internal val properties: Result<Properties> =
//            Result.of {
//                MethodHandles.lookup()
//                        .lookupClass()
//                        .getResourceAsStream(configFileName)
//                        .use { inputStream ->
//                            Properties().let {
//                                it.load(inputStream)
//                                it
//                            }
//                        }
//            }

    // a better alternative to read properties from file
    private val properties: Result<Properties> =
            try {
                MethodHandles.lookup()
                        .lookupClass()
                        .getResourceAsStream(configFileName)
                        .use { inputStream ->
                            when (inputStream) {
                                null -> Result.failure("File $configFileName not found in classpath")
                                else -> Properties().let {
                                    it.load(inputStream)
                                    Result(it)
                                }
                            }
                        }
            } catch (ex: IOException) {
                Result.failure("IOException reading classpath resource $configFileName")
            } catch (e: Exception) {
                Result.failure("Exception: ${e.message} while reading classpath resource $configFileName")
            }

    fun readAsString(name: String) = properties.flatMap {
        Result.of {
            it.getProperty(name)
        }.mapFailure("Property $name not found")
    }

    fun readAsInt(name: String): Result<Int> = readAsString(name).flatMap {
        try {
            Result(it.toInt())
        } catch (e: NumberFormatException) {
            Result.failure<Int>("Invalid value while parsing property '$name' to Int: '$it'")
        }
    }

    fun <T> readAsType(f: (String) -> Result<T>, name: String) =
            readAsString(name).flatMap {
                try {
                    f(it)
                } catch (ex: Exception) {
                    Result.failure<T>(
                            "Invalid value while parsing property '$name': '$it'"
                    )
                }
            }

    inline fun <reified T : Enum<T>> readAsEnum(name: String, enumClass: Class<T>): Result<T> {
        val f: (String) -> Result<T> = {
            try {
                val value = enumValueOf<T>(it)
                Result(value)
            } catch (ex: Exception) {
                Result.failure("Error parsing property: '$name' value '$it' can't be parsed to ${enumClass.name}")
            }
        }
        return readAsType(f, name)
    }
}

enum class Type { SERIAL, PARALLEL }

fun main() {
    val propertyReader = PropertyReader("/config.properties")

    val person = propertyReader.readAsInt("id")
            .flatMap { id ->
                propertyReader.readAsString("firstName")
                        .flatMap { firstName ->
                            propertyReader.readAsString("lastName")
                                    .map { lastName -> Person.of(id, firstName, lastName) }
                        }
            }

    person.forEach(
            onSuccess = { println(it) },
            onFailure = { println(it) }
    )

    propertyReader.readAsString("host")
            .forEach(
                    onSuccess = { println(it) },
                    onFailure = { println(it) }
            )

    propertyReader.readAsString("name")
            .forEach(
                    onSuccess = { println(it) },
                    onFailure = { println(it) }
            )

    propertyReader.readAsString("year")
            .forEach(
                    onSuccess = { println(it) },
                    onFailure = { println(it) }
            )

    val type = propertyReader.readAsEnum("type", Type::class.java)
    println(type)


//    propertyReader.properties.forEach(
//            onSuccess = { println(it) },
//            onFailure = { println(it) }
//    )
}