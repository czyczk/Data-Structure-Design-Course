package ui.function

import algorithm.RoutePlanner
import manager.SpotManager
import util.UiUtil

class QueryDistance {
    companion object {
        fun run() {
            println(UiUtil.getString("queryDistance"))
            val isSpotAvailable = UiUtil.checkIfAnySpotIsAvailable()
            if (!isSpotAvailable)
                return

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
                    println(UiUtil.getString("departureAndDestinationAreTheSame"))
                } else {
                    val plannedRoute = RoutePlanner.planBestRoute(src, dest)
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