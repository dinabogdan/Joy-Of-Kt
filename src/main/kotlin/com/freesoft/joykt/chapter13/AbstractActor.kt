package com.freesoft.joykt.chapter13

import com.freesoft.joykt.chapter7.Result
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.ThreadFactory

class DaemonThreadFactory : ThreadFactory {

    override fun newThread(runnableTask: Runnable): Thread {
        val thread = Executors.defaultThreadFactory().newThread(runnableTask)
        thread.isDaemon = true
        return thread
    }
}

abstract class AbstractActor<T>(protected val id: String) : Actor<T> {

    private val executor: ExecutorService = Executors.newSingleThreadExecutor(DaemonThreadFactory())

    override val context: ActorContext<T> = object : ActorContext<T> {

        var behavior: MessageProcessor<T> = object : MessageProcessor<T> {

            override fun process(message: T, sender: Result<Actor<T>>) {
                onReceive(message, sender)
            }
        }

        @Synchronized
        override
        fun become(behavior: MessageProcessor<T>) {
            this.behavior = behavior
        }

        override fun behavior() = behavior
    }


    abstract fun onReceive(message: T, sender: Result<Actor<T>>)

    override fun self(): Result<Actor<T>> {
        return Result(this)
    }

    override fun shutdown() {
        this.executor.shutdown()
    }

    @Synchronized
    override fun tell(message: T, sender: Result<Actor<T>>) {
        executor.execute {
            try {
                context.behavior().process(message, sender)
            } catch (e: RejectedExecutionException) {
                /*
                 * This is probably normal and means all pending tasks
                 * were canceled because the actor was stopped.
                 */
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }
}