package com.example.multiswarm

/**
 * Interface for a fitness function, used to decouple the main algorithm logic
 * from the specific problem solution.
 *
 * @author Donato Rimenti
 */
interface FitnessFunction {
    /**
     * Returns the fitness of a particle given its position.
     *
     * @param particlePosition
     * the position of the particle
     * @return the fitness of the particle
     */
    fun getFitness(particlePosition: LongArray?): Double
}