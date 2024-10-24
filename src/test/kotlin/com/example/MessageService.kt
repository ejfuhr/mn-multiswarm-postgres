package com.example

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

data class MessageThread(val id: String, val name: String)
data class MessageThreadUpdate(val threadId: String, val messages: List<Message>)
data class Message(val fromUserId: String, val text: String, val threadId: String? = null)
data class MessageSendingResponse(val fromUserId: String, val success: Boolean)

class MessagesService(
    messagesSource: Flow<Message>,
    scope: CoroutineScope
) {
    private val source = messagesSource
        .shareIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed()
        )

    fun observeMessages(fromUserId: String) = source
        .filter { it.fromUserId == fromUserId }
}

suspend fun <T> Flow<T>.toListDuring(
    duration: Duration
): List<T> = coroutineScope {
    val result = mutableListOf<T>()
    val job = launch {
        this@toListDuring.collect(result::add)
    }
    delay(duration)
    job.cancel()
    return@coroutineScope result
}

private val infiniteFlow =
    flow<Nothing> {
        while (true) {
            delay(100)
        }
    }


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class MessagesServiceTest {

    @Test
    @Order(1)
    fun `should emit messages from user`() = runTest {
// given
        val source = flowOf(
            Message(fromUserId = "0", text = "A"),
            Message(fromUserId = "1", text = "B"),
            Message(fromUserId = "0", text = "C"),
        )
        val service = MessagesService(
            messagesSource = source,
            scope = backgroundScope,
        )
// when
        /*        val result = service.observeMessages("0")
            .toList() // Here we'll wait forever!
            */

        val result = service.observeMessages("0")
            .take(2)
            .toList()
// then
        assertEquals(
            listOf(
                Message(fromUserId = "0", text = "A"),
                Message(fromUserId = "0", text = "C"),
            ), result
        )
    }

    /**
     * The next approach is to start our flow in backgroundScope and store all the
     * elements it emits in a collection. This approach not only better shows us “what
     * is” and “what should be” in failing cases; it also offers us much more flexibility
     * with testing time. In the example below, I’ve added some delays to verify when
     * messages are sent. p 372
     */

    @Test
    @Order(2)
    fun `should emit messages from user emit to collection `() = runTest {

// given
        val source = flow {
            emit(Message(fromUserId = "0", text = "A"))
            delay(1000)
            emit(Message(fromUserId = "1", text = "B"))
            emit(Message(fromUserId = "0", text = "C"))
            emit(Message(fromUserId = "0", text = "from user 0 - 3rd of 3 messaages"))
        }
        val service = MessagesService(
            messagesSource = source,
            scope = backgroundScope,
        )
// when
        val emittedMessages = mutableListOf<Message>()
        service.observeMessages("0")
            .onEach { emittedMessages.add(it) }
            .launchIn(backgroundScope)
        delay(1)
// then
        assertEquals(
            listOf(
                Message(fromUserId = "0", text = "A"),
            ), emittedMessages
        )

        println("first emitted messages :: $emittedMessages")

        // when
        delay(1000)
// then
        assertEquals(
            listOf(
                Message(fromUserId = "0", text = "A"),
                Message(fromUserId = "0", text = "C"),
                Message(fromUserId = "0", text = "from user 0 - 3rd of 3 messaages")
            ), emittedMessages
        )
        println("last messages :: $emittedMessages")
    }

    @Test
    @Order(3)
    fun `should emit messages from user using toListDuring `() = runTest {
// given
        val source = flow {
            emit(Message(fromUserId = "0", text = "A"))
            emit(Message(fromUserId = "1", text = "B"))
            emit(Message(fromUserId = "0", text = "C"))
            emit(Message(fromUserId = "0", text = "user 0 last message"))
        }
        val service = MessagesService(
            messagesSource = source,
            scope = backgroundScope,
        )
        // when
        val emittedMessages = service.observeMessages("0")
            .toListDuring(1.milliseconds)
// then
        assertEquals(
            listOf(
                Message(fromUserId = "0", text = "A"),
                Message(fromUserId = "0", text = "C"),
                Message(fromUserId = "0", text = "user 0 last message")
            ), emittedMessages
        )
        println("last messages :: ${emittedMessages.get(index = 2).text}")
    }

    @Test
    @Order(4)
    fun `should start at most one connection p 376`() = runTest {
// given
        var connectionsCounter = 0
        val source = infiniteFlow
            .onStart { connectionsCounter++ }
            .onCompletion { connectionsCounter-- }
        val service = MessagesService(
            messagesSource = source,
            scope = backgroundScope,
        )
// when
        service.observeMessages("0")
            .launchIn(backgroundScope)
        service.observeMessages("1")
            .launchIn(backgroundScope)
        service.observeMessages("0")
            .launchIn(backgroundScope)
        service.observeMessages("2")
            .launchIn(backgroundScope)
        delay(1000)
// then
        assertEquals(1, connectionsCounter)
    }


}
