package com.freesoft.joykt.chapter14

import java.util.*
import com.freesoft.joykt.chapter7.Result
import java.io.IOException
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


    fun readProperty(name: String) = properties.flatMap {
        Result.of {
            it.getProperty(name)
        }.mapFailure("Property $name not found")
    }
}

fun main() {
    val propertyReader = PropertyReader("/config.properties")

    val person = propertyReader.readProperty("id")
            .map { s -> s.toInt() }
            .flatMap { id ->
                propertyReader.readProperty("firstName")
                        .flatMap { firstName ->
                            propertyReader.readProperty("lastName")
                                    .map { lastName -> Person.of(id, firstName, lastName) }
                        }
            }

    person.forEach(
            onSuccess = { println(it) },
            onFailure = { println(it) }
    )

    propertyReader.readProperty("host")
            .forEach(
                    onSuccess = { println(it) },
                    onFailure = { println(it) }
            )

    propertyReader.readProperty("name")
            .forEach(
                    onSuccess = { println(it) },
                    onFailure = { println(it) }
            )

    propertyReader.readProperty("year")
            .forEach(
                    onSuccess = { println(it) },
                    onFailure = { println(it) }
            )


//    propertyReader.properties.forEach(
//            onSuccess = { println(it) },
//            onFailure = { println(it) }
//    )
}