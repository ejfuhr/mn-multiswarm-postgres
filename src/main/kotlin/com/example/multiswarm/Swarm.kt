package com.example.multiswarm

import kotlin.random.Random

class Swarm(numParticles: Int) {
    /**
     * Gets the [.particles].
     *
     * @return the [.particles]
     */
    /**
     * The particles of this swarm.
     */
    val particles: Array<Particle?> = arrayOfNulls(numParticles)

    /**
     * Gets the [.bestPosition].
     *
     * @return the [.bestPosition]
     */
    /**
     * Sets the [.bestPosition].
     *
     * @param bestPosition
     * the new [.bestPosition]
     */
    /**
     * The best position found within the particles of this swarm.
     */
    var bestPosition: LongArray = longArrayOf()

    /**
     * Gets the [.bestFitness].
     *
     * @return the [.bestFitness]
     */
    /**
     * Sets the [.bestFitness].
     *
     * @param bestFitness
     * the new [.bestFitness]
     */
    /**
     * The best fitness score found within the particles of this swarm.
     */
    var bestFitness: Double = Double.NEGATIVE_INFINITY

    /**
     * A random generator.
     */
    private val random: Random = Random.Default

    /**
     * Instantiates a new Swarm.
     *
     * @param numParticles
     * the number of particles of the swarm
     */
    init {
        for (i in 0 until numParticles) {
            val initialParticlePosition = longArrayOf(
                random.nextInt(Constants.PARTICLE_UPPER_BOUND).toLong(),
                random.nextInt(Constants.PARTICLE_UPPER_BOUND).toLong()
            )
            val initialParticleSpeed = longArrayOf(
                random.nextInt(Constants.PARTICLE_UPPER_BOUND).toLong(),
                random.nextInt(Constants.PARTICLE_UPPER_BOUND).toLong()
            )
            particles[i] = Particle(
                initialParticlePosition, initialParticleSpeed
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Swarm

        if (!particles.contentEquals(other.particles)) return false
        if (!bestPosition.contentEquals(other.bestPosition)) return false
        if (bestFitness != other.bestFitness) return false
        if (random != other.random) return false

        return true
    }

    override fun hashCode(): Int {
        var result = particles.contentHashCode()
        result = 31 * result + bestPosition.contentHashCode()
        result = 31 * result + bestFitness.hashCode()
        result = 31 * result + random.hashCode()
        return result
    }




    /*
    * (non-Javadoc)
    *
    * @see java.lang.Object#hashCode()
    */
/*    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        val temp = java.lang.Double.doubleToLongBits(bestFitness)
        result = prime * result + (temp xor (temp ushr 32)).toInt()
        result = prime * result + Arrays.hashCode(bestPosition)
        result = prime * result + Arrays.hashCode(particles)
        result = prime * result + (random?.hashCode() ?: 0)
        return result
    }*/

    /*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
/*    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (obj == null) return false
        if (javaClass != obj.javaClass) return false
        val other = obj as Swarm
        if (java.lang.Double.doubleToLongBits(bestFitness) != java.lang.Double.doubleToLongBits(other.bestFitness)) return false
        if (!Arrays.equals(bestPosition, other.bestPosition)) return false
        if (!Arrays.equals(particles, other.particles)) return false
        if (random == null) {
            if (other.random != null) return false
        } else if (!random.equals(other.random)) return false
        return true
    }*/

    /*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
/*    override fun toString(): String {
        return ("Swarm [particles=" + Arrays.toString(particles) + ", bestPosition=" + Arrays.toString(bestPosition)
                + ", bestFitness=" + bestFitness + ", random=" + random + "]")
    }*/

    override fun toString(): String {
        return "Swarm(particles=${particles.contentToString()}, " +
                "bestPosition=${bestPosition.contentToString()}, " +
                "bestFitness=$bestFitness, random=$random)"
    }

}