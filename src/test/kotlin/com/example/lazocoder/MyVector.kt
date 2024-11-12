
package com.example.lazocoder

import kotlin.math.sqrt

/**
 * Can represent a position as well as a velocity.
 */
class MyVector constructor(var x: Double = 0.0, var y: Double = 0.0, var z: Double = 0.0) {
    private var limit = Double.MAX_VALUE

/*    fun getX(): Double {
        return x
    }

    fun getY(): Double {
        return y
    }

    fun getZ(): Double {
        return z
    }*/

    fun set(x: Double, y: Double, z: Double) {
        this.x = x
        this.y = y
        this.z = z
    }

    fun add(v: MyVector) {
        x += v.x
        y += v.y
        z += v.z
        limit()
    }

    fun sub(v: MyVector) {
        x -= v.x
        y -= v.y
        z -= v.z
        limit()
    }

    fun mul(s: Double) {
        x *= s
        y *= s
        z *= s
        limit()
    }

    fun div(s: Double) {
        x /= s
        y /= s
        z /= s
        limit()
    }

    fun normalize() {
        val m = mag()
        if (m > 0) {
            x /= m
            y /= m
            z /= m
        }
    }

    private fun mag(): Double {
        return sqrt(x * x + y * y)
    }

    fun limit(l: Double) {
        limit = l
        limit()
    }

    private fun limit() {
        val m = mag()
        if (m > limit) {
            val ratio = m / limit
            x /= ratio
            y /= ratio
        }
    }

    fun clone(): MyVector {
        return MyVector(x, y, z)
    }

    override fun toString(): String {
        return "($x, $y, $z)"
    }
}