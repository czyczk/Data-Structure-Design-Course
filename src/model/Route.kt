package model

import java.security.InvalidParameterException

data class Route(
        private var destination: String,
        private var distance: Double
) {
    init {
        if (distance < 0) throw InvalidParameterException()
    }
}