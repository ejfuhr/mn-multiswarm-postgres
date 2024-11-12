package com.example.multiswarm

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.*
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.junit.jupiter.api.TestMethodOrder
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.util.*
import kotlin.random.Random


// Produces a flow of Unit
// For instance producingUnits(5) -> [Unit, Unit, Unit, Unit, Unit]
fun producingMultipleSwarmsToFlowFromSwarm(swarmList: MutableList<Swarm>): Flow<Swarm> =
    swarmList.asFlow()

class RandomSlowFitnessFunction : FitnessFunction {
    override fun getFitness(particlePosition: LongArray?): Double {
        val health = particlePosition!![0]
        val armor = particlePosition[1]
        //val event1 = particlePosition[2]
        val fitness = (health * (100 + armor)) / 100
        return fitness.toDouble()

    }
}

class ExampleFitnessFunction : FitnessFunction {
    override fun getFitness(particlePosition: LongArray?): Double {
        particlePosition ?: return Double.NEGATIVE_INFINITY // Handle null input

        // Example fitness calculation
        // In this case, fitness is defined as the sum of the squares of the position coordinates
        var fitness = 0.0
        for (position in particlePosition) {
            fitness += position * position
        }

        return fitness
    }
}

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class SwarmBuildTest {

    @Test
    @Order(1)
    fun `build fitness Function with random Evenets`() = runTest {
        val rFunction: RandomSlowFitnessFunction = RandomSlowFitnessFunction()
        val longArray = longArrayOf(101.22.toLong(), 202L, -10L)
        val fitness = rFunction.getFitness(longArray)
        assertNotNull(fitness)
        assertTrue(fitness is Double)
        assertTrue(fitness > 1)
        println("fitness $fitness")

    }

    @Test
    @Order(2)
    //@Disabled
    fun `test with current Multiswarm`(){
        val rFunction: RandomSlowFitnessFunction = RandomSlowFitnessFunction()
        val longArray = longArrayOf(101.22.toLong(), 202L + -10L)
        val fitness = rFunction.getFitness(longArray)
        val multiswarmx = Multiswarm(numSwarms = 10, particlesPerSwarm = 1000, rFunction)
        assertNotNull(fitness)
        println(multiswarmx.bestFitness)
        println("particle size ${multiswarmx.bestPosition.size}")

        for (i in 0..999) {
            multiswarmx.mainLoop()
            if(i <= 10  || i > 995){
                fitnessFound(multiswarmx)
            }
        }

        println(
            ("Best fitness found: " + multiswarmx.bestFitness + "[" + multiswarmx.bestPosition[0]
                    + "," + multiswarmx.bestPosition[1] + "]")
        )
        println("1080, ${multiswarmx.bestPosition[0]}")
        println("50, ${multiswarmx.bestPosition[1]}")
                println("1620, ${multiswarmx.bestFitness.toInt()}")
    }

    private fun fitnessFound(multiswarm: Multiswarm) {
        println(
            ("current fitness found: " + multiswarm.bestFitness + "[" + multiswarm.bestPosition[0]
                    + "," + multiswarm.bestPosition[1] + "]")
        )
    }

    @Test
    @Order(3)
    fun `simple sum of sqaures functio`() {
        val rFunction = ExampleFitnessFunction()
        val longArray = longArrayOf(3, 4, 2)
        val fitness = rFunction.getFitness(longArray)
        println("Fitness: $fitness")  // Outputs: Fitness: 29.0
        assertEquals(29.0, fitness)
    }
}

