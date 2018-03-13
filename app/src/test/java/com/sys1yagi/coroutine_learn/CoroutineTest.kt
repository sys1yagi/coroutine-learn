package com.sys1yagi.coroutine_learn

import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class CoroutineTest {
    @Test
    fun delayTest() = runBlocking<Unit> {
        val start = System.currentTimeMillis()
        println("start")
        delay(100)
        println("end: ${System.currentTimeMillis() - start}")
    }

    @Test
    fun handmadeDelay() {
        val dispatcher = SingleThreadDispatcher()
        val coroutine = HandmadeDelay(dispatcher)
        dispatcher.dispatch {
            coroutine.resume()
        }
        dispatcher.await()
    }

    interface Continuation {
        fun resume()
        fun resumeWithException(e: Exception)
    }

    interface Dispatcher {
        fun dispatch(runnable: () -> Unit)
    }

    class SingleThreadDispatcher : Dispatcher {
        private val executor = Executors.newSingleThreadExecutor()

        override fun dispatch(runnable: () -> Unit) {
            executor.submit(runnable)
        }

        fun await(){
            executor.awaitTermination(1, TimeUnit.SECONDS)
        }
    }

    class Delay(val dispatcher: Dispatcher, val continuation: Continuation) {
        fun delay(time: Long) {
            dispatcher.dispatch {
                Thread.sleep(time)
                continuation.resume()
            }
        }
    }

    class HandmadeDelay(val dispatcher: Dispatcher) : Continuation {
        var state = 0

        // coroutine variable
        var start: Long = 0

        override fun resume() {
            when (state) {
                0 -> {
                    start = System.currentTimeMillis()
                    println("start")
                    state++
                    Delay(dispatcher, this).delay(100)
                }
                1 -> {
                    println("end: ${System.currentTimeMillis() - start}")
                }
            }
        }

        override fun resumeWithException(e: Exception) {

        }
    }
}
