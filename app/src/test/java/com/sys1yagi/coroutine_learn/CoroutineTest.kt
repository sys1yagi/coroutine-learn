package com.sys1yagi.coroutine_learn

import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test
import java.util.concurrent.Executors

class CoroutineTest {
    @Test
    fun delayTest() = runBlocking<Unit> {
        val start = System.currentTimeMillis()
        println("start")
        delay(100)
        println("end: ${System.currentTimeMillis() - start}")
    }


    //    @Test
//    fun handmadeDelay() {
//
//    }
//

    interface Continuation {
        fun resume()
        fun resumeWithException(e: Exception)
    }

    class Delay(val continuation: Continuation) {
        fun delay(time: Long) {
            val callThread = Thread.currentThread()
        }
    }

    class HandmadeDelay : Continuation {
        var state = 0

        // coroutine variable
        var start: Long = 0

        override fun resume() {
            when (state) {
                0 -> {
                    start = System.currentTimeMillis()
                    println("start")
                    state++
                    Delay(this).delay(100)
                }
                1 -> {

                }
            }
        }

        override fun resumeWithException(e: Exception) {

        }
    }
}
