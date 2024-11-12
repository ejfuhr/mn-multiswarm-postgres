package com.example.lazocoder

import com.example.lazocoder.MyParticle.*
import kotlin.random.Random

/**
 * Represents a swarm of particles from the Particle Swarm Optimization algorithm.
 */
class MySwarm(
    function: MyParticle.FunctionType,
    private val numOfParticles: Int,
    private val epochs: Int,
    private val inertia: Double,
    private val cognitiveComponent: Double,
    private val socialComponent: Double
) {
    private var bestPosition: MyVector
    private var bestEval: Double
    private val function: MyParticle.FunctionType = function // The function to search.

    /**
     * When Particles are created they are given a random position.
     * The random position is selected from a specified range.
     * If the begin range is 0 and the end range is 10 then the
     * value will be between 0 (inclusive) and 10 (exclusive).
     */
    private val beginRange: Int
    private val endRange: Int

    /**
     * Construct the Swarm with default values.
     * @param particles     the number of particles to create
     * @param epochs        the number of generations
     */
    constructor(function: MyParticle.FunctionType, particles: Int, epochs: Int) : this(
        function,
        particles,
        epochs,
        DEFAULT_INERTIA,
        DEFAULT_COGNITIVE,
        DEFAULT_SOCIAL
    )

    /**
     * Construct the Swarm with custom values.
     * @param particles     the number of particles to create
     * @param epochs        the number of generations
     * @param inertia       the particles resistance to change
     * @param cognitive     the cognitive component or introversion of the particle
     * @param social        the social component or extroversion of the particle
     */
    init {
        val infinity = Double.POSITIVE_INFINITY
        bestPosition = MyVector(infinity, infinity, infinity)
        bestEval = Double.POSITIVE_INFINITY
        beginRange = DEFAULT_BEGIN_RANGE
        endRange = DEFAULT_END_RANGE
    }

    /**
     * Execute the algorithm.
     */
    fun run() {
        val particles = initialize()

        var oldEval = bestEval
        println("--------------------------EXECUTING-------------------------")
        println("Global Best Evaluation (Epoch 0):\t$bestEval")

        for (i in 0..<epochs) {
            if (bestEval < oldEval) {
                println("Global Best Evaluation (Epoch " + (i + 1) + "):\t" + bestEval)
                oldEval = bestEval
            }

            for (p in particles) {
                p!!.updatePersonalBest()
                updateGlobalBest(p)
            }

            for (p in particles) {
                updateVelocity(p)
                p!!.updatePosition()
            }
        }

        println("---------------------------RESULT---------------------------")
        println("x = " + bestPosition.x)
        if (function !== MyParticle.FunctionType.FunctionA) {
            println("y = " + bestPosition.y)
        }
        println("Final Best Evaluation: $bestEval")
        println("---------------------------COMPLETE-------------------------")
    }

    /**
     * Create a set of particles, each with random starting positions.
     * @return  an array of particles
     */
    private fun initialize(): Array<MyParticle?> {
        val particles = arrayOfNulls<MyParticle>(numOfParticles)
        for (i in 0..<numOfParticles) {
            val particle = MyParticle(function, beginRange, endRange)
            particles[i] = particle
            updateGlobalBest(particle)
        }
        return particles
    }

    /**
     * Update the global best solution if a the specified particle has
     * a better solution
     * @param particle  the particle to analyze
     */
    private fun updateGlobalBest(particle: MyParticle?) {
        if (particle!!.bestEval < bestEval) {
            bestPosition = particle.getBestPosition()
            bestEval = particle.bestEval
        }
    }

    /**
     * Update the velocity of a particle using the velocity update formula
     * @param particle  the particle to update
     */
    private fun updateVelocity(particle: MyParticle?) {
        val oldVelocity: MyVector = particle!!.getVelocity()
        val pBest: MyVector = particle.getBestPosition()
        val gBest: MyVector = bestPosition.clone()
        val pos: MyVector = particle.getPosition()

        val random: Random = Random.Default
        val r1: Double = random.nextDouble()
        val r2: Double = random.nextDouble()

        // The first product of the formula.
        val newVelocity: MyVector = oldVelocity.clone()
        newVelocity.mul(inertia)

        // The second product of the formula.
        pBest.sub(pos)
        pBest.mul(cognitiveComponent)
        pBest.mul(r1)
        newVelocity.add(pBest)

        // The third product of the formula.
        gBest.sub(pos)
        gBest.mul(socialComponent)
        gBest.mul(r2)
        newVelocity.add(gBest)

        particle.setVelocity(newVelocity)
    }

    companion object {
        const val DEFAULT_INERTIA: Double = 0.729844
        const val DEFAULT_COGNITIVE: Double = 1.496180 // Cognitive component.
        const val DEFAULT_SOCIAL: Double = 1.496180 // Social component.

        private const val DEFAULT_BEGIN_RANGE = -100
        private const val DEFAULT_END_RANGE = 101
    }
}