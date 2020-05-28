package com.freesoft.joykt.chapter13

interface ActorContext<T> {

    fun behavior(): MessageProcessor<T>

    fun become(behavior: MessageProcessor<T>)

}