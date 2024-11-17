package com.example.multiswarm

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.TestInstance.Lifecycle
import kotlin.random.Random
import kotlin.test.assertNotNull

data class MultiswarmFromLists(
    val swarms: MutableList<Swarm> = mutableListOf(),
    val numSwarms: Int = 100,
    val particlesPerSwarm: Int = 1000,
    val fitnessFunction: FitnessFunction = LolFitnessFunction()
) {
    fun build() {
        for (i in 0..numSwarms) {
            swarms.add(Swarm(particlesPerSwarm))
        }
    }
}

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class BuildMultiswarmFromLists {

    val lol = LolFitnessFunction()
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
        for (i in 0..999) {
            swarmList.addAll(doMainLoop(swarms))
            flowSwarm.combine(swarmList.asFlow()) { a, b ->
                a.bestFitness = b.bestFitness
                a.bestPosition = b.bestPosition
                a
            }
        }
        println("flowSwarm size ${flowSwarm.toList().size}")

        println(
            ("Best fitness found: " + bestFitness + "[" + bestPosition[0]
                    + "," + bestPosition[1] + "]")
        )

/*        flowSwarm.collect {
            val n=0
            if(it.bestPosition[0] == 1080L && n>500){
                println("bestPo[0] ${it.bestPosition[0]} bestPo[1] ${it.bestPosition[1]}")
                println("Best fitness found: " + it.bestFitness + "[" + it.bestPosition[0]
                            + "," + it.bestPosition[1] + "]")

            }
        }*/
        /*        assertEquals(1080, multiswarm.bestPosition[0])
                assertEquals(50, multiswarm.bestPosition[1])
                assertEquals(1620, multiswarm.bestFitness.toInt())*/

        val myMapAsync: List<Swarm> = swarmList.mapAsync {
            it.bestFitness = lol.getFitness(it.bestPosition)
            it
        }
        println("myMapAsync size ${myMapAsync.size}")
    val lastSwarm = myMapAsync.last()
        println("lastSwarm bestFitness ${lastSwarm.bestFitness}")
        println("lastSwarm bestPosition[0] & [1] ${lastSwarm.bestPosition[0]} ${lastSwarm.bestPosition[1]}")
        println("lastSwarm lastSwarm.particles.size ${lastSwarm.particles.size} ")

    }

    fun doMainLoop(swarms: MutableList<Swarm>): MutableList<Swarm> {
        for (swarm in swarms) {
            for (particle in swarm!!.particles) {
                val particleOldPosition = particle!!.position.clone()

                particle.fitness = lol.getFitness(particleOldPosition)
                //particle.setFitness(fitnessFunction.getFitness(particleOldPosition))

                // Check if a new best position has been found for the particle
                // itself, within the swarm and the multiswarm.
                if (particle.fitness!! > particle.bestFitness) {
                    particle.bestFitness = particle.fitness!!
                    particle.bestPosition = particleOldPosition


                    if (particle.fitness!! > swarm.bestFitness) {
                        swarm.bestFitness = particle.fitness!!
                        swarm.bestPosition = particleOldPosition

                        if (swarm.bestFitness > bestFitness) {
                            bestFitness = swarm.bestFitness
                            bestPosition = swarm.bestPosition.clone()
                        }
                    }
                }

                // Updates the particle position by adding the speed to the
                // actual position.
                val position = particle.position
                val speed = particle.speed

                position[0] += speed[0]
                position[1] += speed[1]

                // Updates the particle speed.
                speed[0] = getNewParticleSpeedForIndex(particle, swarm, 0).toLong()
                speed[1] = getNewParticleSpeedForIndex(particle, swarm, 1).toLong()
            }
        }

        return swarms
        /*        val xx: Flow<Swarm> = swarms
                    .asFlow()

                    //.flatMapMerge(concurrency = 20) {
                        //flow { emit(api.requestOffers(it)) }
                return xx*/
    }

    private fun getNewParticleSpeedForIndex(particle: Particle, swarm: Swarm, index: Int): Int {

        val num: Double = (((Constants.INERTIA_FACTOR * particle.speed[index])
                + (randomizePercentage(Constants.COGNITIVE_WEIGHT)
                * (particle.bestPosition!![index] - particle.position[index]))
                + (randomizePercentage(Constants.SOCIAL_WEIGHT)
                * (swarm.bestPosition[index] - particle.position[index]))
                + (randomizePercentage(Constants.GLOBAL_WEIGHT)
                * (bestPosition[index] - particle.position[index]))))
        return num.toInt()
    }

    private fun randomizePercentage(value: Double): Double {
        return random.nextDouble() * value
    }
}
