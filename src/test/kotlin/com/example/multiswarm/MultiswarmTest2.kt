package com.example.multiswarm


import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.junit.jupiter.api.TestMethodOrder
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import kotlin.random.Random

interface FitnessFunction {
    fun getFitness(particlePosition: LongArray?): Double
}

class ExampleFitnessFunctionX : FitnessFunction {
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

/**
 * another implementation of Multiswarm
 */

class MultiswarmX(
    val numSwarms: Int,
    val particlesPerSwarm: Int,
    val fitnessFunction: FitnessFunction
) {

    var bestFitness: Double = Double.NEGATIVE_INFINITY
    lateinit var bestPosition: LongArray

    fun mainLoop() {
        // Example main loop where swarms update their particles
        // and evaluate fitness using the provided fitnessFunction
        for (i in 0 until numSwarms) {
            for (j in 0 until particlesPerSwarm) {
                val particlePosition = longArrayOf(
                    Random.nextLong(1, 100),
                    Random.nextLong(1, 100),
                    Random.nextLong(1, 100)
                ) // Example particle position
                val fitness = fitnessFunction.getFitness(particlePosition)

                // Update the best fitness if the current fitness is better
                if (fitness > bestFitness) {
                    bestFitness = fitness
                    bestPosition = particlePosition
                }
            }
        }
    }

    fun evaluateSwarm() {
        // Additional logic if needed to evaluate swarms
    }
}

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class MultiswarmTest2 {
    @Test
    @Order(1)
    fun `test multiswarm with ExampleFunctionX`() {
        val rFunction = ExampleFitnessFunctionX()
        val multiswarm = MultiswarmX(numSwarms = 10, particlesPerSwarm = 1000, fitnessFunction = rFunction)

        for (i in 0..999) {
            multiswarm.mainLoop()
        }

        println("Best fitness found: ${multiswarm.bestFitness} [${multiswarm.bestPosition[0]}, ${multiswarm.bestPosition[1]}, ${multiswarm.bestPosition[2]}]")
    }
}