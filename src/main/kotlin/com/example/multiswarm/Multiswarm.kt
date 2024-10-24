package com.example.multiswarm

import java.util.*
import kotlin.random.Random

/**
 * Represents a collection of [Swarm].
 *
 * @author Donato Rimenti
 */
class Multiswarm(numSwarms: Int, particlesPerSwarm: Int, fitnessFunction: FitnessFunction?) {
    /**
     * The swarms managed by this multiswarm.
     */
    private val swarms = arrayOfNulls<Swarm>(numSwarms)

    /**
     * Gets the [.bestPosition].
     *
     * @return the [.bestPosition]
     */
    /**
     * The best position found within all the [.swarms].
     */
    var bestPosition: LongArray = longArrayOf()
        //private set

    /**
     * Gets the [.bestFitness].
     *
     * @return the [.bestFitness]
     */
    /**
     * The best fitness score found within all the [.swarms].
     */
    var bestFitness: Double = Double.NEGATIVE_INFINITY
        //private set

    /**
     * A random generator.
     */
    private val random: Random = Random.Default

    /**
     * The fitness function used to determine how good is a particle.
     */
    private val fitnessFunction: FitnessFunction? = fitnessFunction

    /**
     * Instantiates a new Multiswarm.
     *
     * @param numSwarms
     * the number of [.swarms]
     * @param particlesPerSwarm
     * the number of particle for each [.swarms]
     * @param fitnessFunction
     * the [.fitnessFunction]
     */
    init {
        for (i in 0 until numSwarms) {
            swarms[i] = Swarm(particlesPerSwarm)
        }
    }

    /**
     * Main loop of the algorithm. Iterates all particles of all
     * [.swarms]. For each particle, computes the new fitness and checks
     * if a new best position has been found among itself, the swarm and all the
     * swarms and finally updates the particle position and speed.
     */
    fun mainLoop() {
        for (swarm in swarms) {
            for (particle in swarm!!.particles) {
                val particleOldPosition = particle!!.position.clone()

                // Calculate the particle fitness.

                particle.fitness = fitnessFunction?.getFitness(particleOldPosition)!!
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
                    val  position = particle.position
                val speed = particle.speed

                position[0] += speed[0]
                position[1] += speed[1]

                // Updates the particle speed.
                speed[0] = getNewParticleSpeedForIndex(particle, swarm, 0).toLong()
                speed[1] = getNewParticleSpeedForIndex(particle, swarm, 1).toLong()
            }
        }
    }

    /**
     * Computes a new speed for a given particle of a given swarm on a given
     * axis. The new speed is computed using the formula:
     *
     * <pre>
     * ([Constants.INERTIA_FACTOR] * [Particle.getSpeed]) +
     * (([Constants.COGNITIVE_WEIGHT] * random(0,1)) * ([Particle.getBestPosition] - [Particle.getPosition])) +
     * (([Constants.SOCIAL_WEIGHT] * random(0,1)) * ([Swarm.getBestPosition] - [Particle.getPosition])) +
     * (([Constants.GLOBAL_WEIGHT] * random(0,1)) * ([.bestPosition] - [Particle.getPosition]))
    </pre> *
     *
     * @param particle
     * the particle whose new speed needs to be computed
     * @param swarm
     * the swarm which contains the particle
     * @param index
     * the index of the particle axis whose speeds needs to be
     * computed
     * @return the new speed of the particle passed on the given axis
     */
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

    /**
     * Returns a random number between 0 and the value passed as argument.
     *
     * @param value
     * the value to randomize
     * @return a random value between 0 and the one passed as argument
     */
    private fun randomizePercentage(value: Double): Double {
        return random.nextDouble() * value
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        val temp = java.lang.Double.doubleToLongBits(bestFitness)
        result = prime * result + (temp xor (temp ushr 32)).toInt()

        result = prime * result + Arrays.hashCode(bestPosition)
        result = prime * result + (if (fitnessFunction == null) 0 else fitnessFunction.hashCode())
        result = prime * result + (if (random == null) 0 else random.hashCode())
        result = prime * result + Arrays.hashCode(swarms)
        return result
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (obj == null) return false
        if (javaClass != obj.javaClass) return false
        val other = obj as Multiswarm
        if (java.lang.Double.doubleToLongBits(bestFitness) != java.lang.Double.doubleToLongBits(other.bestFitness)) return false
        if (!Arrays.equals(bestPosition, other.bestPosition)) return false
        if (fitnessFunction == null) {
            if (other.fitnessFunction != null) return false
        } else if (!fitnessFunction.equals(other.fitnessFunction)) return false
        if (random == null) {
            if (other.random != null) return false
        } else if (!random.equals(other.random)) return false
        if (!Arrays.equals(swarms, other.swarms)) return false
        return true
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
    override fun toString(): String {
        return ("Multiswarm [swarms=" + Arrays.toString(swarms) + ", bestPosition=" + Arrays.toString(bestPosition)
                + ", bestFitness=" + bestFitness + ", random=" + random + ", fitnessFunction=" + fitnessFunction + "]")
    }
}