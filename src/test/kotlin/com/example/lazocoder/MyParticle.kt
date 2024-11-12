package com.example.lazocoder

import java.util.*


/**
 * Represents a particle from the Particle Swarm Optimization algorithm.
 */
class MyParticle(function: FunctionType, beginRange: Int, endRange: Int) {
    private val position: MyVector // Current position.
    private var velocity: MyVector
    private var bestPosition: MyVector // Personal best solution.

    /**
     * Get the value of the personal best solution.
     * @return  the evaluation
     */
    var bestEval: Double // Personal best value.
        private set
    private val function: FunctionType // The evaluation function to use.

    /**
     * Construct a Particle with a random starting position.
     * @param beginRange    the minimum xyz values of the position (inclusive)
     * @param endRange      the maximum xyz values of the position (exclusive)
     */
    init {
        require(beginRange < endRange) { "Begin range must be less than end range." }
        this.function = function
        position = MyVector()
        velocity = MyVector()
        setRandomPosition(beginRange, endRange)
        bestPosition = velocity.clone()
        bestEval = eval()
    }

    /**
     * The evaluation of the current position.
     * @return      the evaluation
     */
    private fun eval(): Double {
        return if (function == FunctionType.FunctionA) {
            MyPSOFunction.functionA(position.x)
        } else if (function == FunctionType.Ackleys) {
            MyPSOFunction.ackleysFunction(position.x, position.y)
        } else if (function == FunctionType.Booths) {
            MyPSOFunction.boothsFunction(position.x, position.y)
        } else {
            MyPSOFunction.threeHumpCamelFunction(position.x, position.y)
        }
    }

    private fun setRandomPosition(beginRange: Int, endRange: Int) {
        val x = rand(beginRange, endRange).toDouble()
        val y = rand(beginRange, endRange).toDouble()
        val z = rand(beginRange, endRange).toDouble()
        position.set(x, y, z)
    }

    /**
     * Update the personal best if the current evaluation is better.
     */
    fun updatePersonalBest() {
        val eval = eval()
        if (eval < bestEval) {
            bestPosition = position.clone()
            bestEval = eval
        }
    }

    /**
     * Get a copy of the position of the particle.
     * @return  the x position
     */
    fun getPosition(): MyVector {
        return position.clone()
    }

    /**
     * Get a copy of the velocity of the particle.
     * @return  the velocity
     */
    fun getVelocity(): MyVector {
        return velocity.clone()
    }

    /**
     * Get a copy of the personal best solution.
     * @return  the best position
     */
    fun getBestPosition(): MyVector {
        return bestPosition.clone()
    }

    /**
     * Update the position of a particle by adding its velocity to its position.
     */
    fun updatePosition() {
        position.add(velocity)
    }

    /**
     * Set the velocity of the particle.
     * @param velocity  the new velocity
     */
    fun setVelocity(velocity: MyVector) {
        this.velocity = velocity.clone()
    }

    enum class FunctionType {
        FunctionA,
        Ackleys,
        Booths,
        ThreeHumpCamel
    }

    companion object {
        /**
         * Generate a random number between a certain range.
         * @param beginRange    the minimum value (inclusive)
         * @param endRange      the maximum value (exclusive)
         * @return              the randomly generated value
         */
        private fun rand(beginRange: Int, endRange: Int): Int {
            val r = Random()
            return r.nextInt(endRange - beginRange) + beginRange
        }
    }
}