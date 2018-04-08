package com.sys1yagi.coroutine_learn

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // kotlin coroutine
//        launch(UI) {
//            result.text = "loading..."
//            result.text = async { loadData() }.await()
//        }
        CoroutineLaunchImpl(result, UIThreadDispatcher).resume()
    }
}

fun loadData(): String {
    Thread.sleep(1000L)
    return "success!"

}

interface Continuation {
    fun resume(value: Any? = null)
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

object UIThreadDispatcher : Dispatcher {
    private val handler = Handler(Looper.getMainLooper())

    override fun dispatch(runnable: () -> Unit) {
        handler.post(runnable)
    }
}

class CoroutineLaunchImpl(val result: TextView, private val dispatcher: Dispatcher) : Continuation {
    var state = 0
    var start: Long = 0

    override fun resume(value: Any?) {
        dispatcher.dispatch {
            when (state) {
                0 -> {
                    result.text = "loading..."
                    state++
                    CoroutineAsyncImpl(SingleThreadDispatcher, this).resume()
                }
                1 -> {
                    result.text = value as String
                }
            }
        }
    }
}

class CoroutineAsyncImpl(private val dispatcher: Dispatcher, val continuation: Continuation) : Continuation {
    var state = 0

    override fun resume(value: Any?) {
        dispatcher.dispatch {
            when (state) {
                0 -> {
                    val data = loadData()
                    continuation.resume(data)
                }
            }
        }
    }
}
