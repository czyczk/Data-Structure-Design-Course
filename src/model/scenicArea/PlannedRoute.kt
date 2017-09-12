package model.scenicArea

import java.util.*

data class PlannedRoute(val routeList: Array<Route>, val totalDistance: Double) {
    val departure: String?
        get() {
            return if (routeList.isEmpty())
                null
            else
                routeList[0].name1
        }

    val destination: String?
        get() {
            return if (routeList.isEmpty())
                null
            else
                routeList[routeList.count() - 1].name2
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlannedRoute

        if (!Arrays.equals(routeList, other.routeList)) return false
        if (totalDistance != other.totalDistance) return false

        return true
    }

    override fun hashCode(): Int {
        var result = Arrays.hashCode(routeList)
        result = 31 * result + totalDistance.hashCode()
        return result
    }


}