package algorithm

import dataStructure.LinkedList
import manager.RouteManager
import model.scenicArea.PlannedRoute
import model.scenicArea.Route
import model.scenicArea.Vector

class BacktrackingGraphIterator(@Suppress("MemberVisibilityCanPrivate") val graph: Map<String, LinkedList<Vector>>,
                                @Suppress("MemberVisibilityCanPrivate") val numSpot: Int,
                                @Suppress("MemberVisibilityCanPrivate") val departure: String,
                                @Suppress("MemberVisibilityCanPrivate") val isCircuitRequired: Boolean) {
    private var isRoutePlanned = false
    private val plannedRouteCandidateList: MutableList<PlannedRoute> = mutableListOf()
    // 只有一条通路的景点
    private val spotsWithOneRoute = mutableListOf<String>()
    // 其他的景点
    private val spots = mutableListOf<String>()

    init {
        if (numSpot <= 1)
            throw IllegalArgumentException()

        if (departure !in graph.keys)
            throw IllegalArgumentException()

        graph.forEach { t, u ->
            // 先取出图中只有一条路的节点
            if (u.length == 1) {
                spotsWithOneRoute.add(t)
            }
            // 其他节点纳入 spots
            else if (u.length > 1) {
                spots.add(t)
            }
        }
    }

    fun planRoute(): PlannedRoute? {
        if (!isRoutePlanned) {
            walk(departure, mutableListOf<String>())
            isRoutePlanned = true
        }

        // 无候选则返回 null
        if (plannedRouteCandidateList.isEmpty())
            return null

        // 从候选中找到路径最小的
        var curMinDistance = Double.MAX_VALUE
        var result: PlannedRoute? = null
        plannedRouteCandidateList.forEach {
            if (it.totalDistance < curMinDistance) {
                curMinDistance = it.totalDistance
                result = it
            }
        }

        // 将单条线路的加进去
        if (spotsWithOneRoute.isNotEmpty()) {
            val routeList = result!!.routeList.toMutableList()
            var totalDistance = result!!.totalDistance
            spotsWithOneRoute.forEach {
                val connectedVector = graph[it]!!.iterator().next()
                val connectedSpot = connectedVector.destination
                val additionalDistance = connectedVector.distance
                val ordinalOfSpotInList = routeList.indexOfFirst { it.name2 == connectedSpot }
                routeList.add(ordinalOfSpotInList + 1, Route(it, connectedSpot, additionalDistance))
                routeList.add(ordinalOfSpotInList + 2, Route(connectedSpot, it, additionalDistance))
                totalDistance += 2 * additionalDistance
            }
            result = PlannedRoute(routeList.toTypedArray(), totalDistance)
        }
        return result
    }

    private fun walk(spot: String, visited: MutableList<String>) {
        visited += spot

        // 现状：已经遍历所有的点
        if (visited.count() == numSpot - spotsWithOneRoute.count()) {
            var valid = false
            // 如果要求回路，则看能不能连通到起点
            if (isCircuitRequired) {
                val connectedSpots = graph[spot]!!.toList()
                // 如果可以回到起点，则将起点再加入 visited，并则标记此路有效
                if (departure in connectedSpots.map { it.destination }) {
                    visited += departure
                    valid = true
                }
            }
            // 如果不要求回路，则标记此路有效
            else {
                valid = true
            }

            // 若此路标记为有效，则封装为 PlannedRoute，加入候选列表，结束查找
            if (valid)
                plannedRouteCandidateList += toPlannedRoute(visited)
            // 否则放弃此路，不添加至候选列表
            return
        }

        // 现状：未遍历完所有点，则获取 spot 可连通的新点（排除已访问的点和只有一条边的点）
        val spotsInScope = graph[spot]!!.toList().map { it.destination }.filter {
            it !in visited && it !in spotsWithOneRoute
        }
        // 若没有新点可加，则此路不通
        if (spotsInScope.isEmpty())
            return

        // 对 spotsInScope 中每一个点进行递归
        spotsInScope.forEach {
            walk(it, visited.toMutableList())  // 注意必须传新的 visited，因为可能该路无效
        }
    }

    private fun toPlannedRoute(visited: MutableList<String>): PlannedRoute {
        val routeList = mutableListOf<Route>()
        var totalDistance = 0.0
        for (i in 0 until visited.count() - 1) {
            val route = RouteManager.query(visited[i], visited[i + 1])!!
            routeList.add(route)
            totalDistance += route.distance
        }
        return PlannedRoute(routeList.toTypedArray(), totalDistance)
    }
}