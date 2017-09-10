package algorithm

import model.PlannedRoute
import model.Route
import util.UiUtil

class MstIterator {
    enum class IterationMode {
        CUSTOM_1, CUSTOM_2, DEPTH_FIRST, BREADTH_FIST
    }

    companion object {
        fun planRoute(mst: List<Route>, iterationMode: IterationMode = IterationMode.CUSTOM_1): PlannedRoute {
            if (mst.isEmpty())
                throw IllegalArgumentException(UiUtil.getString("mstIsEmpty"))

            return when (iterationMode) {
                IterationMode.CUSTOM_1 -> custom1(mst)
                IterationMode.CUSTOM_2 -> custom2(mst)
                IterationMode.DEPTH_FIRST -> depthFirst(mst)
                IterationMode.BREADTH_FIST -> breadthFirst(mst)
            }
        }

        private fun custom1(mst: List<Route>): PlannedRoute {
            var visited = listOf(mst[0].name1)
            var curBestAssumption = listOf<String>() // curBestAssumption 将记录最佳线路
            var curMinDis = Double.MAX_VALUE

            // 每次从 mst 中取出一个新点（每一条Route的终点），探讨它插入在 curBestRoute 中的位置
            for (newSpot in mst.map { it.name2 }) {
                // 若新节点恰巧已因更佳路线被遍历过，则跳过
                if (newSpot in visited)
                    continue

                // 对新节点最佳插入位置的探讨
                curMinDis = Double.MAX_VALUE
                for (i in 1..visited.count()) {
                    // 探讨一种假设
                    val assumption = visited.toMutableList()
                    assumption.add(i, newSpot)
                    // 计算最短的距离
                    val distance = (0 until assumption.count() - 1)
                            .map { RoutePlanner(assumption[it], assumption[it + 1]).planBestRoute() }
                            .sumByDouble { it.totalDistance }
                    if (distance < curMinDis) {
                        curMinDis = distance
                        curBestAssumption = assumption // 只记录起点与终点，忽略途经地点
                    }
                }
                // 将此次结果汇总到 visited 中
                visited = curBestAssumption
            }

            // 封装 bestRoute 并传出
            val routeList = mutableListOf<Route>()
            (0 until curBestAssumption.count() - 1).forEach {
                val plannedRoute =
                        RoutePlanner(curBestAssumption[it], curBestAssumption[it + 1]).planBestRoute()
                routeList.addAll(plannedRoute.routeList)
            }
            return PlannedRoute(routeList.toTypedArray(), curMinDis)
        }

        private fun custom2(mst: List<Route>): PlannedRoute {
            TODO()
        }

        private fun depthFirst(mst: List<Route>): PlannedRoute {
            TODO()
        }

        private fun breadthFirst(mst: List<Route>): PlannedRoute {
            TODO()
        }
    }
}