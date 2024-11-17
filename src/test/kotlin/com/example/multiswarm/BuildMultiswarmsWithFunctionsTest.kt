package com.example.multiswarm

import com.example.lazocoder.MyPSOFunction
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.TestInstance.Lifecycle
import kotlin.random.Random
import kotlin.test.assertNotNull

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)

class ThreeHumpFunction1()
    :FitnessFunction {
    override fun getFitness(particlePosition: LongArray?): Double {
        val x = particlePosition!![0]
        val y = particlePosition[1]

        return MyPSOFunction.threeHumpCamelFunction(x.toDouble(), y.toDouble())
    }
}


class BuildMultiswarmsWithFunctionsTest {

    val hump3 = ThreeHumpFunction1()
    private val random: Random = Random.Default
    var bestFitness: Double = Double.NEGATIVE_INFINITY
    var bestPosition: LongArray = longArrayOf()

    @Test
    @Order(1)
    fun `build Multiswarms from lists then flow`() = runTest {

        val swarms = mutableListOf<Swarm>()
        val numSwarms = 100
        val particlesPerSwarm = 1000
        var swarmList: MutableList<Swarm> = mutableListOf()
        for (i in 0..numSwarms) {
            swarms.add(Swarm(particlesPerSwarm))
        }

        val flowSwarm: Flow<Swarm> = swarmList.asFlow()
        for (i in 0..25) {
            swarmList.addAll(doMainLoopWithFunction(swarms, hump3))
            flowSwarm.combine(swarmList.asFlow()) { a, b ->
                a.bestFitness = b.bestFitness
                a.bestPosition = b.bestPosition
                a
            }

            println("flowSwarm size ${flowSwarm.toList().size}")
            var n = 0
            for(swarm in flowSwarm.toList() ) {
                n++
                println(
                    ("${n++} Best fitness found: " + swarm.bestFitness + "[" + swarm.bestPosition[0]
                            + "," + swarm.bestPosition[1] + "]")
                )
            }

/*            println(
                ("Best fitness found: " + bestFitness + "[" + bestPosition[0]
                        + "," + bestPosition[1] + "]")
            )*/

            val myMapAsync: List<Swarm> = swarmList.mapAsync {
                it.bestFitness = hump3.getFitness(it.bestPosition)
                it
            }
            println("myMapAsync size ${myMapAsync.size}")
            val lastSwarm = myMapAsync.last()
            println("lastSwarm bestFitness ${lastSwarm.bestFitness}")
            println("lastSwarm bestPosition[0] & [1] ${lastSwarm.bestPosition[0]} ${lastSwarm.bestPosition[1]}")
            println("lastSwarm lastSwarm.particles.size ${lastSwarm.particles.size} ")
        }
    }
}