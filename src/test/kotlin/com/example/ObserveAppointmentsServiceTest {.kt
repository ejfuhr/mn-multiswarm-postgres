package com.example

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.time.Instant

interface AppointmentRepository {
    fun observeAppointments(): Flow<AppointmentEvent>
}

sealed class AppointmentEvent
data class AppointmentUpdate(
    val appointments: List<Appointment>
) : AppointmentEvent()
data object AppointmentConfirmed : AppointmentEvent()
data class Appointment(val title: String, val time: Instant)
data class ApiException(val code: Int, override val message:String) : Throwable()

class FakeAppointmentRepository(
    private val myflow: Flow<AppointmentEvent>
) : AppointmentRepository {
    override fun observeAppointments() = myflow
}

class ObserveAppointmentsService(
    private val appointmentRepository: AppointmentRepository
) {
    fun observeAppointments(): Flow<List<Appointment>> =
        appointmentRepository
            .observeAppointments()
            .onEach { delay(1000) } // Will not influence
// the above test
            .filterIsInstance<AppointmentUpdate>()
            .map { it.appointments }
            .distinctUntilChanged()
            .retry {
                it is ApiException && it.code in 500..599
            }
}


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ObserveAppointmentsServiceTest {
    val aDate1 = Instant.parse("2020-08-30T18:43:00Z")
    val anAppointment1 = Appointment("APP1", aDate1)
    val aDate2 = Instant.parse("2020-08-31T18:43:00Z")
    val anAppointment2 = Appointment("APP2", aDate2)

    @Test
    @Order(1)
    fun `should keep only appointments from`() = runTest {
// given
        val repo = FakeAppointmentRepository(
            flowOf(
                AppointmentConfirmed,
                AppointmentUpdate(listOf(anAppointment1)),
                AppointmentUpdate(listOf(anAppointment2)),
                AppointmentConfirmed,
            )
        )
        val service = ObserveAppointmentsService(repo)
// when
        val result = service.observeAppointments().toList()
// then
        assertEquals(
            listOf(
                listOf(anAppointment1),
                listOf(anAppointment2),
            ),
            result
        )
        println("here is result:: $result")
    }

    //p367
    @Test
    @Order(2)
    fun `should eliminate elements that are`() = runTest {
// given
        val repo = FakeAppointmentRepository(flow {
            delay(1000)
            emit(AppointmentUpdate(listOf(anAppointment1)))
            emit(AppointmentUpdate(listOf(anAppointment1)))
            delay(1000)
            emit(AppointmentUpdate(listOf(anAppointment2)))
            delay(1000)
            emit(AppointmentUpdate(listOf(anAppointment2)))
            emit(AppointmentUpdate(listOf(anAppointment1)))
        })
        val service = ObserveAppointmentsService(repo)
// when
        val result = service.observeAppointments()
            .map { currentTime to it }
            //.map { "x" to it }
            .toList()
// then
        assertEquals(
            listOf(
                2000L to listOf(anAppointment1),
                5000L to listOf(anAppointment2),
                8000L to listOf(anAppointment1),
            ), result
        )
        println("here is result:: $result")
    }

    @Test
    @Order(3)
    fun `should retry when API exception p368`() = runTest {
// given
        val repo = FakeAppointmentRepository(flow {
            emit(AppointmentUpdate(listOf(anAppointment1)))
            emit(AppointmentUpdate(listOf(anAppointment2)))
            throw ApiException(502, "some message")
        })
        val service = ObserveAppointmentsService(repo)
// when
        val result = service.observeAppointments()
            .take(3)
            .toList()
// then

        val listHere = listOf(
            listOf(anAppointment1)
        )

        println("here is listOf $listHere")
        println("here is result:: $result")

        //assertThrows(ApiException::class.java){}
        assertEquals(
            listOf(
                listOf(anAppointment1),
                listOf(anAppointment2),
                listOf(anAppointment1),
            ), result
        )
    }

    @Test
    @Order(4)
    fun `should retry when API exception with retry p369`() = runTest {
// given
        var retried = false
        val someException = object : Exception() {}
        val repo = FakeAppointmentRepository(
            flow {
            emit(AppointmentUpdate(listOf(anAppointment1)))
            if (!retried) {
                retried = true
                throw ApiException(502, "Some message")
            } else {
                throw someException
            }
        })
        val service = ObserveAppointmentsService(repo)

        // when
        val result = service.observeAppointments()
            .catch<Any> { emit(it) }
            .toList()
// then
        println("here is result:: $result")
        assertTrue(retried)
        assertEquals(
            listOf(
                listOf(anAppointment1),
                listOf(anAppointment1),
                someException,
            ), result
        )
    }


}