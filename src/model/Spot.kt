package model

import java.security.InvalidParameterException

data class Spot(
        var name: String,
        var introduction: String,
        var popularity: Double,
        var isRestAreaProvided: Boolean,
        var isToiletProvided: Boolean
) {
    init {
        if (popularity < 0) throw InvalidParameterException()
    }

    constructor(name: String, introduction: String, popularity: Double) :
            this(name, introduction, popularity, false, false)
}