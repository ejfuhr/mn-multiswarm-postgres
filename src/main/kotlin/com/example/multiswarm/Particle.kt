package com.example.multiswarm

data class Particle(
    val position: LongArray,
    val speed: LongArray,
    var fitness:Double? = 0.0,
    var bestPosition: LongArray? = LongArray(101),
    var bestFitness: Double = Double.NEGATIVE_INFINITY // constructors and other methods
)