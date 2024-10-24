package com.example

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.TestInstance.Lifecycle


@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class SharedStateFlowTests {

    val scope = TestScope()

    @Test
    @Order(4)
    @Disabled
    fun `test state flow p358`() = runTest {
        val state = MutableStateFlow("A")
        println(state.value) // A
        launch {
            state.collect { println("Value changed to $it") }
// Value changed to A
        }
        delay(1000)
        state.value = "B" // Value changed to B
        delay(1000)
        launch {
            state.collect { println("and now it is $it") }
// and now it is B
        }
        delay(1000)
        state.value = "C" // Value changed to C and now it is C


    }


    @Test
    @Order(1)
    @Disabled
    fun `test simple String sharedFlow p351`() = runBlocking {
        p351()
        if (isActive) {
            cancel()
        }
    }

    val randomNumbers = MutableStateFlow(0)


    @Test
    @Order(2)
    @Disabled
    fun `test random nos with stateIn`() = runTest {
        val sharedFlow = randomNumbers.stateIn(
            scope = this,
            started = SharingStarted.WhileSubscribed(),
            initialValue = 0
        )
        launch {
            while (true) {
                delay(1000)
                randomNumbers.value = (0..100).random()
            }
        }

        sharedFlow.collect{
            println(" collecting $it")
        }
    }

    @Test
    @Order(3)
    @Disabled
    fun `test simple shareIn p353`() = runTest {
        coroutineScope {
            val flow = flowOf("A", "B", "C")
                .onEach { delay(1000) }
            assertNotNull(flow, "flow is null")
            val sharedFlow: SharedFlow<String> = flow.shareIn(
                scope = backgroundScope,
                started = SharingStarted.WhileSubscribed()
 //replay = 2
            )
            try {
                delay(500)
                launch {
                    sharedFlow.collect {
                        assertNotNull(it)
                        println("#1 $it")
                    }
                }
                delay(1000)
                launch {
                    sharedFlow.collect { println("#2 $it") }
                }
                delay(1000)
                launch {
                    sharedFlow.collect { println("#3 $it") }
                }
            } finally {
                sharedFlow.onCompletion {
                    println("finally com[lete ${it.toString()}")
                }
            }
            sharedFlow
        }

    }

    suspend fun p351(): Unit = coroutineScope {
        val mutableSharedFlow = MutableSharedFlow<String>()
        val sharedFlow: SharedFlow<String> = mutableSharedFlow
        val collector: FlowCollector<String> = mutableSharedFlow
        launch {
            mutableSharedFlow.collect {
                println("#1 received $it")
            }
        }
        launch {
            sharedFlow.collect {
                println("#2 received $it")
            }
        }
        delay(1000)
        mutableSharedFlow.emit("Message1")

        collector.emit("Message2")

    }
    //this.cancel()
}
