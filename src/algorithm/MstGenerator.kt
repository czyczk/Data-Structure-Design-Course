package algorithm

import manager.RouteManager
import manager.SpotManager
import model.scenicArea.Route

/**
 * 最小生成树生成器
 * 使用 Prim 算法根据指定起点生成最小生成树。
 */
class MstGenerator {
    companion object {
        /**
         * 根据指定出发点生成最小生成树。
         * @param departure 出发点
         * @return 最小生成树
         */
        fun generate(departure: String): List<Route> {
            val visited = mutableListOf<String>(departure)
            val pending = SpotManager.spotMap.keys.filter { it != departure }.toMutableList()
            val mst = mutableListOf<Route>()

            while (pending.isNotEmpty()) {
                // 将所有与 visited 中的节点连通、但不在 visited 中的节点列表记为candidates
                val candidates = mutableSetOf<String>()
                visited.forEach {
                    val spotsInScope =
                            RouteManager.routeMap[it]!!.map { it.destination }.filter { it in pending }
                    candidates += spotsInScope
                }
                // 从 visited 和 candidates 中各选一节点，找到距离最短的边
                var curMinRoute = Route("", "", Double.MAX_VALUE) // 记录当前最短边，用于后续比较
                visited.forEach { v ->
                    candidates.forEach { c ->
                        val distanceVC = RouteManager.query(v, c)?.distance ?: Double.MAX_VALUE
                        if (distanceVC < curMinRoute.distance) {
                            curMinRoute = Route(v, c, distanceVC)
                        }
                    }
                }

                mst += curMinRoute // 将边加入 mst
                visited += curMinRoute.name2 // 将新的节点加入 visited
                pending -= curMinRoute.name2
            }

            return mst.toList()
        }
    }
}