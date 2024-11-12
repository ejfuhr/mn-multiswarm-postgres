package com.example.lazocoder

import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt

internal object MyPSOFunction {
    /**
     * Calculate the result of (x^4)-2(x^3).
     * Domain is (-infinity, infinity).
     * Minimum is -1.6875 at x = 1.5.
     * @param x     the x component
     * @return      the y component
     */
    fun functionA(x: Double): Double {
        return x.pow(4.0) - 2 * x.pow(3.0)
    }

    /**
     * Perform Ackley's function.
     * Domain is [5, 5]
     * Minimum is 0 at x = 0 & y = 0.
     * @param x     the x component
     * @param y     the y component
     * @return      the z component
     */
    fun ackleysFunction(x: Double, y: Double): Double {
        val p1 = -20 * exp(-0.2 * sqrt(0.5 * ((x * x) + (y * y))))
        val p2 = exp(0.5 * (cos(2 * Math.PI * x) + cos(2 * Math.PI * y)))
        return p1 - p2 + Math.E + 20
    }

    /**
     * Perform Booth's function.
     * Domain is [-10, 10]
     * Minimum is 0 at x = 1 & y = 3.
     * @param x     the x component
     * @param y     the y component
     * @return      the z component
     */
    fun boothsFunction(x: Double, y: Double): Double {
        val p1 = (x + 2 * y - 7).pow(2.0)
        val p2 = (2 * x + y - 5).pow(2.0)
        return p1 + p2
    }

    /**
     * Perform the Three-Hump Camel function.
     * @param x     the x component
     * @param y     the y component
     * @return      the z component
     */
    fun threeHumpCamelFunction(x: Double, y: Double): Double {
        val p1 = 2 * x * x
        val p2 = 1.05 * x.pow(4.0)
        val p3 = x.pow(6.0) / 6
        return p1 - p2 + p3 + x * y + y * y
    }
}