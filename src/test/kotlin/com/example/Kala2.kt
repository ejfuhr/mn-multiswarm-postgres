package com.example

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.TestInstance.Lifecycle

// Produces a flow of Unit
// For instance producingUnits(5) -> [Unit, Unit, Unit, Unit, Unit]
fun producingUnitsFromInt(numx: MutableList<Unit>): Flow<Unit> =
    numx.asFlow()
    //flowOf(numx)

//    numx.asFlow()
//    List(numx) {}.asFlow()
//flow { repeat(num) { emit(Unit) } }

// Adds a delay of time `timeMillis` between elements
fun <T> Flow<T>.delayBetweenEach(timeMillis: Long): Flow<T> =
    flow { collect { delay(timeMillis); emit(it) } }

// Should transform values, where transformation value should have index of the element
// flowOf("A", "B").mapIndexed { index, value -> "$index$value" } -> ["0A", "0B"]
fun <T, R> Flow<T>.mapAsIndexed(
    transformation: suspend (index: Int, T) -> R
): Flow<R> = flow {
    var index = 0
    collect {
        emit(transformation(index, it))
        index++
    }
}

// Should transform Unit's to next numbers starting from 1
// For instance flowOf(Unit, Unit, Unit, Unit).toNextNumbersOnIndex() -> [1, 2, 3, 4]
// Example:
// Input   --------U------UU---------U------
// Result  --------1------23---------4------
fun Flow<*>.toNextNumbersOnIndex(): Flow<Int> =
    flow {
        var value = 1
        collect {
            emit(value++)
        }
    }

// Produces not only elements, but the whole history till now
// For instance flowOf(1, "A", 'C').withHistoryOnFlow() -> [[], [1], [1, A], [1, A, C]]
fun <T> Flow<T>.withHistoryOnFlow(): Flow<List<T>> =
    flow {
        var history = listOf<T>()
        emit(history)
        collect {
            history += it
            emit(history)
        }
    }


@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class MoreFlowTests {

    @Test
    @Order(1)
    fun testUnitsFromInt() = runTest{
        val p = PlayId("no1")
        //producingUnitsFromInt(1)
        producingUnitsFromInt(mutableListOf( p.no1()))
            .collect{
                println("whats here $it")
                assertTrue(2>1)
            }
    }
}

class PlayId(val id:String){
    fun no1(){
        id.plus("no1")
    }
    fun no2(){
        id.plus("no2")
    }
    fun complete():String{
        return id
    }
}


