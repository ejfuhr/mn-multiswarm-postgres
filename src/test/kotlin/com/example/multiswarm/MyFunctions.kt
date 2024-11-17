package com.example.multiswarm


import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlin.random.Random


var bestFitness: Double = Double.NEGATIVE_INFINITY
var bestPosition: LongArray = longArrayOf()

private val random: Random = Random.Default

/**
 * from Marcin Moskała's coroutines book
 */
suspend fun <T, R> Iterable<T>.mapAsync(
    transformation: suspend (T) -> R
): List<R> = coroutineScope {
    this@mapAsync.map { async { transformation(it) } }
        .awaitAll()
}

fun doMainLoopWithFunction(swarms: MutableList<Swarm>, fitnessFunc: FitnessFunction): MutableList<Swarm> {
    for (swarm in swarms) {
        for (particle in swarm!!.particles) {
            val particleOldPosition = particle!!.position.clone()

            particle.fitness = fitnessFunc.getFitness(particleOldPosition)
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