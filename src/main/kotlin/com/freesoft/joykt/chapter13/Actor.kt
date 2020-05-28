package com.freesoft.joykt.chapter13

import com.freesoft.joykt.chapter7.Result

interface Actor<T> {

    val context: ActorContext<T>

    fun self(): Result<Actor<T>> = Result(this)

    fun tell(message: T, sender: Result<Actor<T>> = self())

    fun shutdown()

    fun tell(message: T, sender: Actor<T>) = tell(message, Result(sender))

    companion object {
        fun <T> noSender(): Result<Actor<T>> = Result()
    }

}