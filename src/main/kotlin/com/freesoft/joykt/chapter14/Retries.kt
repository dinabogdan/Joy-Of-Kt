package com.freesoft.joykt.chapter14

import java.io.IOException
import kotlin.random.Random
import com.freesoft.joykt.chapter7.Result
import java.lang.IllegalStateException

fun get(path: String) =
        Random.nextInt(10).let {
            when {
                it < 8 -> throw IOException("Error accessing file $path")
                else -> "content of $path"
            }
        }


// a better implementation of handling retries
fun <A, B> retry(f: (A) -> B,
                 times: Int,
                 delay: Long = 10
): (A) -> Result<B> {
    fun retry(a: A, result: Result<B>, e: Result<B>, tms: Int): Result<B> =
            result.orElse {
                when (tms) {
                    0 -> e
                    else -> {
                        Thread.sleep(delay)
                        // log the number of retries
                        println("retry ${times - tms}")
                        retry(a, Result.of { f(a) }, result, tms - 1)
                    }
                }
            }
    return { a -> retry(a, Result.of { f(a) }, Result(), times - 1) }
}

fun show(message: String) =
        Random.nextInt(10).let {
            when {
                it < 8 -> throw IllegalStateException("Failure !!!")
                else -> println(message)
            }
        }


fun main() {
    retry(::show, 10, 20)("Hello, World!").forEach(
            onFailure = { println(it.message) }
    )
}

// bad solution

//fun main() {
//    var retries = 0
//
//    var result: String? = null
//
//    (0..3).forEach rt@{
//        try {
//            result = get("/my/path")
//            return@rt
//        } catch (ex: IOException) {
//            if (retries < 3) {
//                Thread.sleep(100)
//                retries += 1
//                throw ex
//            }
//        }
//    }
//
//    println(result)
//}