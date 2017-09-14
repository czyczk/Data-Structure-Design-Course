package ui.function

import algorithm.RoutePlanner
import manager.RouteManager
import manager.SpotManager
import util.UiUtil

class QueryDistancePage {
    companion object {
        fun run() {
            println(UiUtil.getString("queryDistance"))

            // 如果不存在景点则返回上级
            val isSpotAvailable = UiUtil.checkIfAnySpotIsAvailable()
            if (!isSpotAvailable)
                return

            // 如果有景点未连通则返回上级
            val isAllSpotsConnected = RouteManager.routeMap.values.none { it.isEmpty }
            if (!isAllSpotsConnected) {
                UiUtil.printErrorMessage(UiUtil.getString("notAllSpotsAreConnected"))
                println(UiUtil.getString("pressEnterToContinue"))
                readLine()
                return
            }

            while (true) {
                var pass: Boolean
                var isBreak = false

                // 输入出发地
                print(UiUtil.getString("enterDeparture"))
                println(UiUtil.getString("enterZeroToExit"))
                var src: String
                do {
                    pass = true
                    src = readLine()!!
                    // 输入 0 以退出
                    if (src == "0") {
                        isBreak = true
                        break
                    }
                    // 若景点不存在则重输
                    if (!SpotManager.spotMap.containsKey(src)) {
                        pass = false
                        UiUtil.printErrorMessage(UiUtil.getString("theSpotDoesNotExist"))
                    }
                } while (!pass)

                if (isBreak)
                    break

                // 输入目的地
                print(UiUtil.getString("enterDestination"))
                println(UiUtil.getString("enterZeroToExit"))
                var dest: String
                do {
                    pass = true
                    dest = readLine()!!
                    // 输入 0 以退出
                    if (dest == "0") {
                        isBreak = true
                        break
                    }
                    // 若景点不存在则重输
                    if (!SpotManager.spotMap.containsKey(dest)) {
                        pass = false
                        UiUtil.printErrorMessage(UiUtil.getString("theSpotDoesNotExist"))
                    }
                } while (!pass)

                if (isBreak)
                    break

                // 检查出发地与目的地是否一致
                if (src == dest) {
                    UiUtil.printErrorMessage(UiUtil.getString("departureAndDestinationAreTheSame"))
                }
                // 否则，为正查查询
                else {
                    val plannedRoute = RoutePlanner(src, dest).planBestRoute()
                    plannedRoute.routeList.forEachIndexed { index, route ->
                        println("${index + 1}. $route")
                    }
                    print(UiUtil.getString("totalDistance"))
                    println(UiUtil.beautifyDouble(plannedRoute.totalDistance))
                }

                // 询问是否继续查询
                print(UiUtil.getString("isContinueQuerying"))
                println(UiUtil.getString("yesOrNo"))
                val isContinueQuerying = UiUtil.enterChoice()
                if (!isContinueQuerying)
                    break
            }
        }
    }
}