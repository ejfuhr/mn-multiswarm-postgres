package com.example.kotlinCoroutinesBook.dispatchers

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

class ExperimentsTest {

    @Test
    //@Disabled
    fun `test main`() = runTest {
        measureTimeMillis {
            coroutineScope {
                repeat(50) {
                    launch(dispatcher) {
                        operation()
                        println("Done $it")
                    }
                }
            }
        }.let {
            Assertions.assertNotNull(it)
            println("Took $it")
        }

    }
}

