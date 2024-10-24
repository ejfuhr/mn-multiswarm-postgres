package com.example.multiswarm

/**
 * Constants used by the Multi-swarm optimization algorithms.
 *
 * @author Donato Rimenti
 */
object Constants {
    /**
     * The inertia factor encourages a particle to continue moving in its
     * current direction.
     */
    const val INERTIA_FACTOR: Double = 0.729

    /**
     * The cognitive weight encourages a particle to move toward its historical
     * best-known position.
     */
    const val COGNITIVE_WEIGHT: Double = 1.49445

    /**
     * The social weight encourages a particle to move toward the best-known
     * position found by any of the particle’s swarm-mates.
     */
    const val SOCIAL_WEIGHT: Double = 1.49445

    /**
     * The global weight encourages a particle to move toward the best-known
     * position found by any particle in any swarm.
     */
    const val GLOBAL_WEIGHT: Double = 0.3645

    /**
     * Upper bound for the random generation. We use it to reduce the
     * computation time since we can rawly estimate it.
     */
    const val PARTICLE_UPPER_BOUND: Int = 10000000
}