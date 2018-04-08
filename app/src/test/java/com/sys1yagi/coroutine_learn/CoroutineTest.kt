package com.sys1yagi.coroutine_learn

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class CoroutineTest {
    @Test
    fun delayTest() {
        runBlocking<Unit>(CommonPool) {
            val start = System.currentTimeMillis()
            println("start ${Thread.currentThread().id}")
            delay(100)
            println("end: ${System.currentTimeMillis() - start}")
        }
    }

    @Test
    fun handMadeDelayTest() {
        CoroutineImpl(SingleThreadDispatcher).resume()
        Thread.sleep(300)
    }
}

interface Continuation {
    fun resume()
}

interface Dispatcher {
    fun dispatch(runnable: () -> Unit)
}

object SingleThreadDispatcher : Dispatcher {
    private val executor = Executors.newSingleThreadExecutor()

    override fun dispatch(runnable: () -> Unit) {
        executor.submit(runnable)
    }
}

class CoroutineImpl(private val dispatcher: Dispatcher) : Continuation {
    var state = 0
    var start: Long = 0

    override fun resume() {
        dispatcher.dispatch {
            when (state) {
                0 -> {
                    start = System.currentTimeMillis()
                    println("start")
                    state++
                    delay(100, dispatcher, this)
                }
                1 -> {
                    println("end: ${System.currentTimeMillis() - start}")
                }
            }
        }
    }
}

fun delay(milliSeconds: Long, dispatcher: Dispatcher, continuation: Continuation) {
    dispatcher.dispatch {
        Thread.sleep(milliSeconds)
        continuation.resume()
    }
}
