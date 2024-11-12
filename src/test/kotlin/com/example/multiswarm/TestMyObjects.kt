package com.example.multiswarm

import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.junit.jupiter.api.TestMethodOrder
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import kotlin.random.Random

class MyFunction:FitnessFunction {
    override fun getFitness(particlePosition: LongArray?): Double {
        val health = particlePosition!![0]
        val armor = particlePosition[1]
        //val event1 = particlePosition[2]
        val fitness = (health * (100 + armor)) / 100
        return fitness.toDouble()

    }
}


@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TestMyObjects {

    lateinit var myFunction: FitnessFunction
    lateinit var myMultiswarm: Multiswarm
    @Test
    @Order(1)
    fun `myFunction test`() {
        myFunction = MyFunction()
        val longArray = longArrayOf(101.22.toLong(), 202L, -10L)
        assertTrue(myFunction is FitnessFunction)
        assertTrue(myFunction.getFitness(longArray) >1)
        println(myFunction.getFitness(longArray))
        println("Best fitness found: " + myFunction.getFitness(longArray)
        )
    }

    @Test
    @Order(2)
    fun `myMultiswam test`() {
        myFunction = MyFunction()

        myMultiswarm = Multiswarm(numSwarms = 100,
        particlesPerSwarm = 1000, fitnessFunction = myFunction)
        assertNotNull(myFunction)
        assertNotNull(myMultiswarm)

    }

    @Test
    @Order(3)
    fun `check swarm and particles`() {
        myFunction = MyFunction()
        myMultiswarm = Multiswarm(numSwarms = 100,
            particlesPerSwarm = 1000, fitnessFunction = myFunction)
        assertNotNull(myFunction)
        assertNotNull(myMultiswarm)
        for (i in 0..999) {
            myMultiswarm.mainLoop()
        }

        println("Best fitness found: ${myMultiswarm.bestFitness} "+
                "[${myMultiswarm.bestPosition[0]}, ${myMultiswarm.bestPosition[1]},"
/*                +
                "${myMultiswarm.bestPosition[2]}]"*/
        )


    }

}
