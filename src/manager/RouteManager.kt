package manager

import dataStructure.LinkedList
import model.Route
import model.Vector

class RouteManager {
    companion object {
        // 存储线路信息，键值对为景点名称->邻接链表
        var routeMap: MutableMap<String, LinkedList<Vector>> = mutableMapOf()

        /**
         * 添加线路，需要检查线路是否存在。
         * @param route 要添加的线路
         */
        fun add(route: Route, bypassValidityCheck: Boolean = false) {
            if (!bypassValidityCheck)
            // 若线路存在，则返回
                if (query(route.name1, route.name2) != null)
                    return

            // 若 routeMap 中该任何景点为 null 则创建一个邻接链表
            if (!routeMap.containsKey(route.name1))
                routeMap[route.name1] = LinkedList<Vector>()
            if (!routeMap.containsKey(route.name2))
                routeMap[route.name2] = LinkedList<Vector>()

            // 向邻接链表中添加线路 (Vector)
            routeMap[route.name1]!!.addLast(Vector(route.name2, route.distance))
            routeMap[route.name2]!!.addLast(Vector(route.name1, route.distance))
        }

        /**
         * 删除一条线路，由于在于用户交互时做了检查，此处直接删除，不作检查。
         * @param route 要删除的线路
         */
        fun remove(route: Route) {
            routeMap[route.name1]!!.removeFirstOccurrence(Vector(route.name2, route.distance))
            routeMap[route.name2]!!.removeFirstOccurrence(Vector(route.name1, route.distance))
        }

        /**
         * 删除所有涉及景点名称为 name 的路线。由 SpotManager 调用。
         * @param name 所涉及的景点名称
         */
        fun removeAllRoutesAbout(name: String) {
            routeMap.remove(name)
            routeMap.values.forEach {
                var vector: Vector? = null
                val i = it.iterator()
                while (i.hasNext()) {
                    vector = i.next()
                    if (vector.destination == name)
                        break
                    else
                        vector = null
                }
                if (vector != null)
                    it.removeFirstOccurrence(vector)
            }
        }

        /**
         * 更新一条线路的距离。由用户交互用户选择一条现有的线路进行修改，故只能修改距离不能修改两端。不作检查，直接修改。
         * @param route 新的线路
         */
        fun updateDistance(route: Route) {
            routeMap[route.name1]?.forEach {
                if (it.destination == route.name2)
                    it.distance = route.distance
            }

            routeMap[route.name2]?.forEach {
                if (it.destination == route.name1)
                    it.distance = route.distance
            }
        }

        /**
         * 更新所有线路中的景点名字。由 SpotManager 调用。更新所有线路中的旧名称为新名称。
         * @param oldName 旧名称
         * @param newName 新名称
         */
        fun updateName(oldName: String, newName: String) {
            if (oldName == newName) return

            // 将 routeMap 中 key 从 oldName 改为 newName
            routeMap[newName] = routeMap[oldName]!!
            routeMap.remove(oldName)

            // 遍历 routeMap 中所有键值对。键无需修改，跳过。值从 oldName 改为 newName。
            for (pair in routeMap) {
                if (pair.key == newName) continue
                pair.value.forEach {
                    if (it.destination == oldName)
                        it.destination = newName
                }
            }
        }

        /**
         * 根据两端名称返回一条线路 (Route)。
         * @param name1 其中一端的名称
         * @param name2 另一端的名称
         * @return 存在该路径则返回 Route 对象，否则为 null
         */
        fun query(name1: String, name2: String): Route? {
            // routeMap 对称
            routeMap[name1]?.forEach {
                if (it.destination == name2) return Route(name1, name2, it.distance)
            }
            return null
        }
    }
}