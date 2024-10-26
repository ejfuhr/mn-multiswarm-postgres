package com.example.elizarov

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.test.runTest

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * https://www.youtube.com/watch?v=zluKcazgkV4&t=299s
 */

//@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ThreadsTest432 {

    @Test
    @Order(1)
    fun `virtuals threads 1 at 4_35`() {
        val str = "a "
        val threads = List(100) {
            Thread.ofVirtual().unstarted {
                Thread.sleep(1000)
                println(str)
            }
        }
        threads.forEach { it.start() }
        threads.forEach { it.join() }
        assert(str  =="a ")

    }

    @Test
    @Order(2)
    fun `kotlin coroutines at 32_42`() {
        val str = "kotlin "
        runBlocking {
            repeat(100) {
                launch {
                    delay(500)
                    print(str + "::")
                    assertTrue(str==="kotlin ")
                }
            }

        }
    }

    @Test
    @Order(3)
    fun `test fibonacci 35_06`() {
        val fibonacci = sequence {
            yield(1)
            var cur = 1
            var next = 1
            while (true) {
                yield(next)
                val tmp = cur + next
                cur = next
                next = tmp
                assertTrue(next > 1)
                println(next)
            }
        }

        println("fib as string $fibonacci ")
    }

    @Test
    @Order(4)
    fun `as coroutineDispathcher at 41_41`() {
        runBlocking {
            myNiceAsyncCode()
        }
    }

    @Test
    @Order(5)
    fun `as coroutineDispathcher with ArrayBlockingQueue`() = runTest {
        val list = mutableListOf("a", "a dog", "b dog")
        val queue = ConcurrentLinkedQueue<String>()
        val channel = Channel<String>()

        withContext(virtualThreadDispatcher) {
            queue.addAll(list)
            launch {
                queue.forEach {
                    channel.send("$it")
                }
                channel.close()
            }
                for(x in channel){
                    println("x is ::$x")
                }

            assertTrue(queue.isNotEmpty())
            virtualThreadDispatcher.close()
        }
    }





}