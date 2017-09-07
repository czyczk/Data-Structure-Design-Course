package model

import util.UiUtil
import java.security.InvalidParameterException

data class Spot(
        var name: String,
        var introduction: String,
        var popularity: Double,
        var isRestAreaProvided: Boolean,
        var isToiletProvided: Boolean
) {
    init {
        if (popularity < 0) throw InvalidParameterException(UiUtil.getString("cannotBeNegative"))
    }

    constructor(name: String, introduction: String, popularity: Double) :
            this(name, introduction, popularity, false, false)

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(UiUtil.getString("Spot")).append(" - ").appendln(name)
        sb.append('\t').append(UiUtil.getString("Spot.introduction")).appendln(introduction)
        sb.append('\t').append(UiUtil.getString("Spot.popularity")).appendln(popularity)
        sb.append('\t').append(UiUtil.getString("Spot.isRestAreaProvided")).appendln(UiUtil.getString(isRestAreaProvided.toString()))
        sb.append('\t').append(UiUtil.getString("Spot.isToiletProvided")).append(UiUtil.getString(isToiletProvided.toString()))
        return sb.toString()
    }


}