package com.example.elizarov

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.util.concurrent.Executors

val virtualThreadDispatcher: ExecutorCoroutineDispatcher =
    Executors.newVirtualThreadPerTaskExecutor()
        .asCoroutineDispatcher()

suspend fun myNiceAsyncCode() {
    withContext(virtualThreadDispatcher) {
        existingBlockinComplexCode()
    }
}

fun existingBlockinComplexCode() {
    val channel = Channel<Int>()
    runBlocking {
        launch {
            for (x in 1..5)
                channel.send(x * x)
            channel.close() // we're done sending
        }

        for (y in channel) {
            println(y)
            //Assertions.assertNotNull(y, "y is null")
        }

        println("done with printing Int...")
    }

}

