package com.example.multiswarm

data class Particle(
    val position: LongArray,
    val speed: LongArray,
    var fitness:Double? = 0.0,
    var bestPosition: LongArray? = LongArray(101),
    var bestFitness: Double = Double.NEGATIVE_INFINITY // constructors and other methods
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Particle

        if (fitness != other.fitness) return false
        if (bestFitness != other.bestFitness) return false
        if (!position.contentEquals(other.position)) return false
        if (!speed.contentEquals(other.speed)) return false
        if (bestPosition != null) {
            if (other.bestPosition == null) return false
            if (!bestPosition.contentEquals(other.bestPosition)) return false
        } else if (other.bestPosition != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fitness?.hashCode() ?: 0
        result = 31 * result + bestFitness.hashCode()
        result = 31 * result + position.contentHashCode()
        result = 31 * result + speed.contentHashCode()
        result = 31 * result + (bestPosition?.contentHashCode() ?: 0)
        return result
    }
}