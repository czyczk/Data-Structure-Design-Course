package algorithm

import model.PlannedRoute
import model.Route
import util.UiUtil

/**
 * 最小生成树遍历器
 * 使用自定义算法、深度优先和广度优先遍历最小生成树，作为 Hamilton 图的近似解。
 */
class MstIterator {
    enum class IterationMode {
        CUSTOM_1, CUSTOM_2, DEPTH_FIRST, BREADTH_FIST
    }

    companion object {
        /**
         * 生成最小生成树的遍历方案
         * @param mst 最小生成树
         * @param lastSpot 指定最后一个景点（可选）
         * @param iterationMode 指定遍历算法（可选）
         */
        fun planRoute(
                mst: List<Route>,
                lastSpot: String? = null,
                iterationMode: IterationMode = IterationMode.CUSTOM_1
        ): PlannedRoute {
            if (mst.isEmpty())
                throw IllegalArgumentException(UiUtil.getString("mstIsEmpty"))

            return when (iterationMode) {
                IterationMode.CUSTOM_1 -> custom1(mst, lastSpot)
                IterationMode.CUSTOM_2 -> custom2(mst, lastSpot)
                IterationMode.DEPTH_FIRST -> depthFirst(mst, lastSpot)
                IterationMode.BREADTH_FIST -> breadthFirst(mst, lastSpot)
            }
        }

        private fun custom1(mst: List<Route>, lastSpot: String?): PlannedRoute {
            var visited = mutableListOf(mst[0].name1)
            if (lastSpot != null)
                visited.add(lastSpot)
            var curBestAssumption = mutableListOf<String>() // curBestAssumption 将记录每个新点以来每次假设的最佳记录
            var curMinDis = Double.MAX_VALUE

            // 每次从 mst 中取出一个新点（每一条Route的终点），探讨它插入在 curBestRoute 中的位置
            for (newSpot in mst.map { it.name2 }) {
                // 若新节点恰巧已因更佳路线被遍历过，则跳过
                if (newSpot in visited)
                    continue

                // 对新节点最佳插入位置的探讨
                curMinDis = Double.MAX_VALUE
                val range = if (lastSpot != null)
                    1 until visited.count()
                else
                    1..visited.count()
                for (i in range) {
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

        private fun custom2(mst: List<Route>, lastSpot: String?): PlannedRoute {
            TODO()
        }

        private fun depthFirst(mst: List<Route>, lastSpot: String?): PlannedRoute {
            TODO()
        }

        private fun breadthFirst(mst: List<Route>, lastSpot: String?): PlannedRoute {
            TODO()
        }
    }
}