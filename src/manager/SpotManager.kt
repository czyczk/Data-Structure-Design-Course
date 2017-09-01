package manager

import model.Spot

class SpotManager {
    companion object {
        private val spotMap: MutableMap<String, Spot> = mutableMapOf()

        /**
         * 添加景点。
         * @param spot 新增的景点
         */
        fun add(spot: Spot) {
            // 由于在与用户交互时会检查景点是否存在，此处直接添加，不作检查。
            spotMap.put(spot.name, spot)
        }

        /**
         * 删除景点。
         * @param name 要删除的景点的名称
         */
        fun remove(name: String) {
            // 由于在与用户交互时不会发送删除不存在景点的请求，此处直接删除，不作检查。
            spotMap.remove(name)
            // TODO 需要检查并删除所有与之相关的路径。
        }

        /**
         * 修改景点。
         * @param name 要更新的景点的名称
         * @param spot 更新后的景点
         */
        fun update(name: String, spot: Spot) {
            // 由于在与用户交互时不会修改不存在的景点，此处直接将原景点替换为新景点，不作检查。
            spotMap[name] = spot
            // TODO 需要检查并更新所有与之相关的路径。
        }

        fun query(keyword: String, isNameOnly: Boolean) {

        }
    }
}