package com.example

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.TestInstance.Lifecycle
import java.util.concurrent.atomic.AtomicInteger

// Produces a flow of Unit
// For instance producingUnits(5) -> [Unit, Unit, Unit, Unit, Unit]
fun producingUnits(num: Int): Flow<Unit> =
    flow { repeat(num) { emit(Unit) } }
// or
// List(num) {}.asFlow()
// or
// (1..num).map { }.asFlow()


// Adds a delay of time `timeMillis` between elements
fun <T> Flow<T>.delayEach(timeMillis: Long): Flow<T> =
    onEach { delay(timeMillis) }
// or
// flow { collect { delay(timeMillis); emit(it) } }

// Should transform values, where transformation value should have index of the element
// flowOf("A", "B").mapIndexed { index, value -> "$index$value" } -> ["0A", "0B"]
fun <T, R> Flow<T>.mapIndexed(
    transformation: suspend (index: Int, T) -> R
): Flow<R> =
    withIndex().map { transformation(it.index, it.value) }
// or
// flow {
// var index = 0
// collect {
// emit(transformation(index, it))
// index++
// }
// }
// or
// flow {
// collectIndexed { index, value ->
// emit(transformation(index, value))
// }
// }

// Should transform Unit's to next numbers starting from 1
// For instance flowOf(Unit, Unit, Unit, Unit).toNextNumbers() -> [1, 2, 3, 4]
// Example:
// Input   --------U------UU---------U------
// Result  --------1------23---------4------
fun Flow<*>.toNextNumbers(): Flow<Int> =
    mapIndexed { index, _ -> index + 1 }
// or
// withIndex().map { it.index + 1 }
// or
// scan(0) { acc, _ -> acc + 1 }.drop(1)
// or
// flow {
// var value = 1
// collect {
// emit(value++)
// }
// }
// or
// flow {
// collectIndexed { index, _ ->
// emit(index + 1)
// }
// }


// Produces not only elements, but the whole history till now
// For instance flowOf(1, "A", 'C').withHistory() -> [[], [1], [1, A], [1, A, C]]
fun <T> Flow<T>.withHistory(): Flow<List<T>> =
    scan(emptyList()) { acc, value -> acc + value }
// or
// flow {
// var history = listOf<T>()
// emit(history)
// collect {
// history += it
// emit(history)
// }
// }


// Based on two light switches, should decide if the general light should be switched on.
// Should be if one is true and another is false.
// The first state should be false, and then with each even from any switch, new state should be emitted.
// Example:
// switch1 -------t-----f----------t-t-------
// switch2 ----------------f-t-f--------t-f-t
// Result  f------t-----f--f-t-f---t-t--f-t-f
fun makeLightSwitch(
    switch1: Flow<Boolean>,
    switch2: Flow<Boolean>
): Flow<Boolean> =
    switch1.onStart { emit(false) }
        .combine(switch2.onStart { emit(false) }) { e1, e2 ->
            e1 xor e2
        }

// Based on two light switches, should decide if the general light should be switched on.
// Should be if one is turned on and another is off
// Each event from a switch emits news state. The first state is true, each new state is toggled.
// Example:
// switch1 -------U-----U--------------------------U---------------UU-----
// switch2 ----------------U-------------------U-------------U------------
// Result  -------t-----f--t-------------------f---t---------f-----tf-----
fun makeLightSwitchToggle(
    switch1: Flow<*>,
    switch2: Flow<*>
): Flow<Boolean> =
    merge(switch1, switch2)
        .mapIndexed { index, _ -> index % 2 == 0 }


fun polonaisePairing(
    track1: Flow<Person>,
    track2: Flow<Person>
): Flow<Pair<Person, Person>> =
    track1.zip(track2) { p1, p2 -> Pair(p1, p2) }

data class Person(val name: String)

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@Suppress("FunctionName")
class FlowTests {

    @Test()
    @Order(1)
    fun producingUnitsTests() = runTest {
        //assertEquals(listOf(""), producingUnits(0).toList())
        assertEquals(listOf(Unit), producingUnits(1).toList())
        assertEquals(listOf(Unit, Unit), producingUnits(2).toList())
        assertEquals(listOf(Unit, Unit, Unit), producingUnits(3).toList())
        for (i in 1..100 step 7) {
            assertEquals(List(i) { Unit }, producingUnits(i).toList())
        }
    }

    @Test()
    @Order(2)
    fun flowDelayEachTests() = runTest {
        val emittedNum = AtomicInteger()

        producingUnits(100)
            .delayEach(1000)
            .onEach { emittedNum.incrementAndGet() }
            .launchIn(this)

        assertEquals(0, emittedNum.get())

        delay(1_500)
        assertEquals(1, emittedNum.get())

        delay(2_000)
        assertEquals(3, emittedNum.get())

        delay(12_000)
        assertEquals(15, emittedNum.get())
    }

    @Test()
    @Order(3)
    fun mapIndexedTests() = runTest {
        assertEquals(
            listOf("0 A", "1 B", "2 C", "3 D"),
            ('A'..'D').asFlow()
                .mapIndexed { index, letter -> "$index $letter" }
                .toList()
        )

        val actual: List<ValueAndTime<Pair<Int, Any>>> = flow<Any> {
            delay(10)
            emit(10)
            delay(100)
            emit("A")
            delay(1000)
            emit('C')
        }.mapIndexed { index, any -> Pair(index, any) }
            .withVirtualTime(this)
            .toList()
        val expected: List<ValueAndTime<Pair<Int, Any>>> = listOf(
            ValueAndTime(Pair(0, 10), 10),
            ValueAndTime(Pair(1, "A"), 110),
            ValueAndTime(Pair(2, 'C'), 1110),
        )
        assertEquals(expected, actual)
    }

    @Test()
    @Order(4)
    fun toNextNumbersTests() = runTest {
        //assertEquals(listOf(0), producingUnits(0).toNextNumbers().toList())
        assertEquals(listOf(1), producingUnits(1).toNextNumbers().toList())
        assertEquals(listOf(1, 2), producingUnits(2).toNextNumbers().toList())
        assertEquals(listOf(1, 2, 3), producingUnits(3).toNextNumbers().toList())
        for (i in 1..100 step 7) {
            val list = List(i) { it + 1 }
            assertEquals(list, list.map {}.asFlow().toNextNumbers().toList())
        }
    }

    @Test()
    @Order(5)
    fun withHistoryTests() = runTest {
        //assertEquals(listOf(listOf(Unit)), producingUnits(0).withHistory().toList())
        assertEquals(listOf(listOf(), listOf(Unit)), producingUnits(1).withHistory().toList())
        assertEquals(listOf(listOf(), listOf(Unit), listOf(Unit, Unit)), producingUnits(2).withHistory().toList())

        assertEquals(
            listOf(listOf(), listOf(1), listOf(1, 2)),
            flowOf(1, 2).withHistory().toList()
        )
        assertEquals(
            listOf(listOf(), listOf(true), listOf(true, false)),
            flowOf(true, false).withHistory().toList()
        )

        val flow = flow {
            delay(10)
            emit("A")
            delay(100)
            emit(10)
            delay(1000)
            emit("C")
        }

        assertEquals(
            listOf(
                ValueAndTime(listOf(), 0),
                ValueAndTime(listOf("A"), 10),
                ValueAndTime(listOf("A", 10), 110),
                ValueAndTime(listOf("A", 10, "C"), 1110),
            ),
            flow.withHistory()
                .withVirtualTime(this)
                .toList()
        )
        val x = flow.withHistory()
            .toList()
            println("here is list $x")
    }

    @Test()
    @Order(6)
    fun makeLightSwitchTests() = runTest {
        val switchOne = flow<Boolean> {
            emit(true)
            delay(1000)
            emit(false)
            delay(10)
            emit(true)
            delay(500) // 1500
            emit(false)
        }
        val switchTwo = flow<Boolean> {
            emit(false)
            delay(200)
            emit(true)
            delay(1000) // 1200
            emit(false)
        }

        var lightOn = false
        launch {
            makeLightSwitch(switchOne, switchTwo).collect {
                lightOn = it
            }
        }

        delay(50)
        assertEquals(true, lightOn)
        delay(200) // 250
        assertEquals(false, lightOn)
        delay(800) // 1050
        assertEquals(false, lightOn)
        delay(200) // 1250
        assertEquals(true, lightOn)
        delay(300) // 1550
        assertEquals(false, lightOn)
    }

    @Test
    @Order(7)
    fun makeLightSwitchExampleTests() = runTest {
        val switch1 = flow {
            delay(7_000)
            emit(true)
            delay(6_000)
            emit(false)
            delay(11_000)
            emit(true)
            delay(2_000)
            emit(true)
        }
        val switch2 = flow {
            delay(16_000)
            emit(false)
            delay(2_000)
            emit(true)
            delay(2_000)
            emit(false)
            delay(9_000)
            emit(true)
            delay(2_000)
            emit(false)
            delay(2_000)
            emit(true)
        }

        val result = makeLightSwitch(switch1, switch2)
            .fold(mapOf<Long, Boolean>()) { acc, e -> acc + (currentTime to e) }

        val expected = mapOf(
            0L to false,
            7_000L to true,
            13_000L to false,
            16_000L to false,
            18_000L to true,
            20_000L to false,
            24_000L to true,
            26_000L to true,
            29_000L to false,
            31_000L to true,
            33_000L to false,
        )
        assertEquals(expected, result)
    }

    @Test()
    @Order(8)
    fun makeLightSwitchToggleTests() = runTest {
        val switchOne = flow<Unit> {
            emit(Unit)
            delay(1000)
            emit(Unit)
            delay(10)
            emit(Unit)
            delay(500) // 1500
            emit(Unit)
        }
        val switchTwo = flow<Unit> {
            emit(Unit)
            delay(200)
            emit(Unit)
            delay(1000) // 1200
            emit(Unit)
        }

        var lightOn = false
        launch {
            makeLightSwitchToggle(switchOne, switchTwo).collect {
                lightOn = it
            }
        }

        delay(50)
        assertEquals(false, lightOn)
        delay(200) // 250
        assertEquals(true, lightOn)
        delay(800) // 1050
        assertEquals(true, lightOn)
        delay(200) // 1250
        assertEquals(false, lightOn)
        delay(300) // 1550
        assertEquals(true, lightOn)
    }

    @Test()
    @Order(9)
    fun makeLightSwitchToggleExampleTests() = runTest {
        val switch1 = flow {
            delay(7_000)
            emit(Unit)
            delay(6_000)
            emit(Unit)
            delay(27_000)
            emit(Unit)
            delay(16_000)
            emit(Unit)
            delay(1_000)
            emit(Unit)
        }
        val switch2 = flow {
            delay(16_000)
            emit(Unit)
            delay(20_000)
            emit(Unit)
            delay(17_000)
            emit(Unit)
        }

        val result = makeLightSwitchToggle(switch1, switch2)
            .fold(mapOf<Long, Boolean>()) { acc, e -> acc + (currentTime to e) }

        val expected = mapOf(
            7_000L to true,
            13_000L to false,
            16_000L to true,
            36_000L to false,
            40_000L to true,
            53_000L to false,
            56_000L to true,
            57_000L to false,
        )
        assertEquals(expected, result)
    }

    @Test()
    @Order(10)
    fun polonaisePairingTests() = runTest {
        val track1 = flow<Person> {
            emit(Person("A"))
            emit(Person("B"))
            delay(1000)
            emit(Person("C"))
            emit(Person("D"))
        }
        val track2 = flow<Person> {
            emit(Person("1"))
            delay(600)
            emit(Person("2"))
            delay(1000)
            emit(Person("3"))
        }

        val res = polonaisePairing(track1, track2).toList()
        val expected = listOf("A" to "1", "B" to "2", "C" to "3").map { Person(it.first) to Person(it.second) }
        assertEquals(expected, res)

        var lastPair: Pair<Person, Person>? = null
        launch {
            polonaisePairing(track1, track2).collect { lastPair = it }
        }

        runCurrent()
        assertEquals(Person("A") to Person("1"), lastPair)
        delay(200) // 200
        assertEquals(Person("A") to Person("1"), lastPair)

        delay(500) // 700
        assertEquals(Person("B") to Person("2"), lastPair)
        delay(500) // 1200
        assertEquals(Person("B") to Person("2"), lastPair)

        delay(500) // 1700
        assertEquals(Person("C") to Person("3"), lastPair)
    }
}

fun <T> Flow<T>.withVirtualTime(testScope: TestScope): Flow<ValueAndTime<T>> =
    map { ValueAndTime(it, testScope.currentTime) }

data class ValueAndTime<T>(val value: T, val timeMillis: Long)