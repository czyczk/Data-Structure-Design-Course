package ui.function

import algorithm.SpotSorter
import manager.RouteManager
import manager.SpotManager
import model.Spot
import util.UiUtil
import util.Util
import java.math.BigDecimal

class ViewScenicSpotMap {
    companion object {
        fun run() {
            println(UiUtil.getString("viewScenicSpotMap"))

            val orderedNameList = SpotSorter.getSortedList(
                    SpotManager.spotMap.values.toList(), Spot.OrderBy.NAME
            )
            if (orderedNameList.isEmpty()) {
                UiUtil.printErrorMessage(UiUtil.getString("noSpotAvailable"))
            } else {
                // 打印景点分布图
                /*
                 * 格式：
                 *              景点 1          景点 2...
                 * 景点 1       距离            距离...
                 * 景点 2       距离            距离...
                 * ...
                 */

                val width = 16
                // 打印表头第一行
                for (i in 1..width)
                    print(' ')
                orderedNameList.forEach {
                    UiUtil.printStringInFixedWidth(it.name, width, false)
                }
                println()
                // 为每个景点打印到其他景点的距离
                for (i in orderedNameList) {
                    UiUtil.printStringInFixedWidth(i.name, width, false)
                    for (j in orderedNameList) {
                        val distance: Double
                        // 为对角线元素设置特例
                        distance = if (i == j) {
                            0.0
                        } else {
                            val route = RouteManager.query(i.name, j.name)
                            route?.distance ?: 65535.0
                        }
                        val distanceStr = if (Util.checkIfRoughlyEqualsToInt(distance))
                            distance.toInt().toString()
                        else
                            BigDecimal(distance.toString()).toString()
                        UiUtil.printStringInFixedWidth(distanceStr, width, false)
                    }
                    println()
                }
            }


            // 按 Enter 键继续
            println(UiUtil.getString("pressEnterToContinue"))
            readLine()
        }
    }
}