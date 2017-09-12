package manager

import algorithm.RoutePlanner
import model.scenicArea.Spot

class SpotManager {
    companion object {
        var spotMap: MutableMap<String, Spot> = mutableMapOf()

        /**
         * 添加景点。
         * @param spot 新增的景点
         */
        fun add(spot: Spot) {
            // 由于在与用户交互时会检查景点是否存在，此处直接添加，不作检查。
            spotMap.put(spot.name, spot)
            clearAllRelatedCaches()
        }

        /**
         * 删除景点。
         * @param name 要删除的景点的名称
         */
        fun remove(name: String) {
            // 由于在与用户交互时不会发送删除不存在景点的请求，此处直接删除，不作检查。
            spotMap.remove(name)
            // 同时删除所有与之相关的路径
            RouteManager.removeAllRoutesAbout(name)
            clearAllRelatedCaches()
        }

        /**
         * 修改景点。
         * @param name 要更新的景点的名称
         * @param spot 更新后的景点
         */
        fun update(name: String, spot: Spot) {
            // 由于在与用户交互时不会修改不存在的景点，此处直接将原景点替换为新景点，不作检查。
            if (name == spot.name)
                spotMap[name] = spot
            else {
                spotMap.remove(name)
                spotMap[spot.name] = spot
            }
            // 若名称有变更，则更新所有与之相关的路径
            if (name != spot.name) {
                RouteManager.updateName(name, spot.name)
            }
            clearAllRelatedCaches()
        }

        /**
         * 根据 keyword 检索景点。
         * @param isNameOnly 为 true 时，只检索名字包含关键字的景点；为 false 时检索条件包括简介。
         * @return 符合条件的景点的名称列表。
         */
        fun queryContains(keyword: String, isNameOnly: Boolean): Array<String> {
            /*
             * 返回结果中景点顺序依次为：
             * 名称为 keyword 的景点
             * 名称包含 keyword 的景点
             * 简介包含 keyword 的景点
             */
            val result = mutableListOf<String>()

            spotMap.keys.forEach {
                if (it == keyword)
                    result.add(0, keyword) // 名称为 keyword 的景点放在最前面
                else if (it.contains(keyword))
                    result.add(it) // 名称包含 keyword 的景点在其后，之间顺序不定
            }

            if (!isNameOnly) {
                val rest = spotMap.keys
                rest.removeAll(result)
                rest.forEach {
                    if (spotMap[it]!!.introduction.contains(keyword))
                        result.add(it)
                }
            }

            return result.toTypedArray()
        }

        // 当景点信息发生变动时清空所有相关的缓存
        private fun clearAllRelatedCaches() {
            // 清除 RoutePlanner 中最佳线路的缓存
            RoutePlanner.clearCache()
        }
    }
}