package model

import java.security.InvalidParameterException

open class Vector(
        destination: String,
        distance: Double
) {
    var destination: String
        get set
    var distance: Double
        get set

    init {
        this.destination = destination
        if (distance < 0) throw InvalidParameterException()
        else this.distance = distance
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Vector

        if (destination != other.destination) return false
        if (distance != other.distance) return false

        return true
    }

    override fun hashCode(): Int {
        var result = destination.hashCode()
        result = 31 * result + distance.hashCode()
        return result
    }

    override fun toString(): String {
        return "Vector(destination='$destination', distance=$distance)"
    }
}