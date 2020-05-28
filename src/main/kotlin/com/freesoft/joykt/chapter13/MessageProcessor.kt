package com.freesoft.joykt.chapter13

import com.freesoft.joykt.chapter7.Result

interface MessageProcessor<T> {

    fun process(message: T, sender: Result<Actor<T>>)
}