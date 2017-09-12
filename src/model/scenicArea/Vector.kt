package model.scenicArea

import java.security.InvalidParameterException

data class Vector(var destination: String, var distance: Double) {
    init {
        if (distance < 0) throw InvalidParameterException()
    }
}