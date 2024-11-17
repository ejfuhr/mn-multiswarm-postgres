package com.example.elizarov.continuationsPlus


import com.example.elizarov.myNiceAsyncCode
import com.example.elizarov.virtualThreadDispatcher
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertNotNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class VirtualThreadDispactherTest {

    @Test
    //@Disabled
    @Order(1)
    fun `as coroutineDispathcher at 41_41`() = runTest {
        assertNotNull(myVirtualThreadDispatcher)
        myNiceAsyncCode2()
    }

    @Test
    @Disabled
    @Order(2)
    fun `as coroutineDispathcher at 41_41 2`() = runTest {
        assertNotNull(virtualThreadDispatcher)
        myNiceAsyncCode()
    }

    @Test
    @Order(3)
    fun `as handling StringFlow`() {
        runBlocking {
            handleStringFlow()
        }
    }
}

private val myVirtualThreadDispatcher: ExecutorCoroutineDispatcher =
    Executors.newVirtualThreadPerTaskExecutor()
        .asCoroutineDispatcher()

private suspend fun myNiceAsyncCode2() {
    withContext(myVirtualThreadDispatcher) {
        myExistingBlockinComplexCode()
    }
}


private fun myExistingBlockinComplexCode(): Unit {
    val channel = Channel<Int>()
    runBlocking {
        launch {
            for (x in 1..5)
                channel.send(x * x)
            channel.close() // we're done sending
        }

        for (y in channel) {
            println(y)
            assertNotNull(y, "y is null")
        }

        println("done with printing Int...")
    }

}

private fun myFlowOfStrings(): Flow<String> = flow {
    val f: Flow<String> = flowOf("one", "two", "three")
    var n = 10
    while (n > 1) {
        f.collect {
            emit("$it plus another ")
            n -= 1
        }
    }
    //return@flow
}

private suspend fun handleStringFlow() {
    myFlowOfStrings()
        .collect {
            println(it)
        }

    //.coll


    //.takeIf { it == "one" }
}

/*
fun fibonacci(): Flow<BigInteger> = flow {
    var x = BigInteger. ZERO     var y = BigInteger. ONE

    while (true) {         emit(x)         x = y. also {             y += x         }     } }
fibonacci().take(100).collect { println(it)*/
