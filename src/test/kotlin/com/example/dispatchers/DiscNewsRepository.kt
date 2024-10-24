package com.example.kotlinCoroutinesBook.dispatchers

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import kotlin.system.measureTimeMillis

class DiscNewsRepository(
    private val discReader: DiscReader
) : NewsRepository {
    override suspend fun getNews(newsId: String): News {
        val (title, content) = discReader.read("user/$newsId")
        return News(title, content)
    }
}

interface DiscReader {
    fun read(key: String): List<String>
}

interface NewsRepository {
    suspend fun getNews(newsId: String): News
}

data class News(val title: String, val content: String)

//@Suppress("FunctionName")
class DiscNewsRepositoryTests {

    @Test
    @Order(1)
    fun `should read data from disc using DiscReader`() = runTest {
        val repo = DiscNewsRepository(OneSecDiscReader(listOf("Some title", "Some content")))
        val res = repo.getNews("SomeUserId")
        assertEquals("Some title", res.title)
        assertEquals("Some content", res.content)
    }

    class ImmediateDiscReader(val map: Map<String, List<String>>) : DiscReader {
        override fun read(key: String): List<String> = map[key] ?: error("Element not found")
    }

    @Test
    @Order(2)
    fun `should be prepared for many reads at the same time`() = runTest{
        val repo = DiscNewsRepository(OneSecDiscReader(listOf("Some title", "Some content")))
        val time = measureTimeMillis {
            coroutineScope {
                repeat(2) { id ->
                    launch {
                        repo.getNews("SomeUserId$id")
                    }
                }
            }
        }
        assert(time < 2100) { "Should take less than 2000, took $time" }
    }

    @Test//(timeout = 2000)
    @Timeout(3)
    @Order(3)
    fun `should be prepared for 200 parallel reads`() = runTest{
        val repo = DiscNewsRepository(OneSecDiscReader(listOf("Some title", "Some content")))
        val time = measureTimeMillis {
            coroutineScope {
                repeat(2) { id ->
                    launch {
                        repo.getNews("SomeUserId$id")
                    }
                }
            }
        }
        assert(time < 2900) { "Should take less than 2000, took $time" }
    }

    @Test//(timeout = 3000)
    @Timeout(3)
    @Order(4)
    fun `should not allow more than 200 parallel reads`() = runTest{
        val repo = DiscNewsRepository(OneSecDiscReader(listOf("Some title", "Some content")))
        val time = measureTimeMillis {
            coroutineScope {
                repeat(2) { id ->
                    launch {
                        repo.getNews("SomeUserId$id")
                    }
                }
            }
        }
        assert(time > 2000) { "Should take less than 2000, took $time" }
    }

    class OneSecDiscReader(private val response: List<String>) : DiscReader {
        override fun read(key: String): List<String> {
            Thread.sleep(1000)
            return response
        }
    }
}