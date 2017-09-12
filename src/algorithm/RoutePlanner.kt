package algorithm

import manager.RouteManager
import manager.SpotManager
import model.scenicArea.PlannedRoute
import model.scenicArea.Route

/**
 * 路线规划器。
 * 使用 Dijkstra 算法规划出发地和目的地之间的最佳路径和最短距离。
 */
class RoutePlanner(
        @Suppress("MemberVisibilityCanPrivate") val src: String,
        @Suppress("MemberVisibilityCanPrivate") val dest: String) {
    companion object {
        // 记录任意两个景点的最佳路径。<Route(起点, 终点), PlannedRoute>。景点信息发生变动时将被清空。
        private val plannedRouteMap: MutableMap<Route, PlannedRoute> = mutableMapOf()

        fun clearCache() {
            plannedRouteMap.clear()
        }
    }

    // 记录 src 到任意景点的最佳路径。<任意景点名, 路径沿途景点名>
    private var bestRouteMap: MutableMap<String, MutableList<String>> = mutableMapOf()
    // 记录 src 至其他的最短距离
    private var minDisMap: MutableMap<String, Double> = mutableMapOf()

    fun planBestRoute(): PlannedRoute {
        // 若该线路未被缓存则计算 src 到任意景点的最佳线路
        if (Route(src, dest) !in plannedRouteMap.keys) {
            // 初始化
            SpotManager.spotMap.keys.forEach {
                if (it == src) {
                    bestRouteMap[it] = mutableListOf(src)   // src 到 src 的最佳路径为 [src]
                    minDisMap[it] = 0.0 // src 到 src 的距离为 0
                }
                else {
                    bestRouteMap[it] = mutableListOf()  // src 到 it 的最佳路径未知
                    minDisMap[it] = Double.MAX_VALUE    // src 到 it 的最短距离初始化为 +∞
                }
            }

            val visited = mutableListOf<String>()   // 记录已访问过的节点
            val pending = SpotManager.spotMap.keys.toMutableList()  // 记录未访问过的节点

            // 遍历所有节点
            while (pending.isNotEmpty()) {
                // 在 pending 中找 minDisMap 中数值最小的节点
                var curMinDis = Double.MAX_VALUE
                var curSpot = ""
                minDisMap.forEach { t, u ->
                    if (u < curMinDis && t in pending) {
                        curMinDis = u
                        curSpot = t
                    }
                }

                // 获取与 curSpot 邻接，但未确认最短路径的景点
                val queue = RouteManager.routeMap[curSpot]!!.map { it.destination }.filter { it in pending }
                // 对它们逐一更新距离和路径
                queue.forEach {
                    // 重新计算距离
                    val newDistance = curMinDis + RouteManager.query(curSpot, it)!!.distance
                    // 若新距离更短
                    if (newDistance < minDisMap[it]!!) {
                        // 更新到 it 的最短距离为新距离
                        minDisMap[it] = newDistance
                        // 更新 bestRouteMap 中的路径，新值为 bestRouteMap[curSpot] + it
                        bestRouteMap[it] = bestRouteMap[curSpot]!!.toMutableList()
                        bestRouteMap[it]!! += it
                    }
                }

                visited += curSpot
                pending -= curSpot
            }

            visited.filter { it != src }.forEach {
                val bestRouteNameList = bestRouteMap[it]!!
                val minDis = minDisMap[it]!!
                val bestRoute = mutableListOf<Route>()
                for (j in 0 until bestRouteNameList.count() - 1) {
                    val name1 = bestRouteNameList[j]
                    val name2 = bestRouteNameList[j + 1]
                    bestRoute += RouteManager.query(name1, name2)!!
                }
                plannedRouteMap[Route(src, it)] = PlannedRoute(bestRoute.toTypedArray(), minDis)
            }
        }

        return plannedRouteMap[Route(src, dest)]!!
    }
}