package ui.function

import algorithm.BacktrackingGraphIterator
import algorithm.MstGenerator
import algorithm.MstIterator
import manager.RouteManager
import manager.SpotManager
import model.scenicArea.PlannedRoute
import util.UiUtil

class QueryTouristRoutePage {
    companion object {
        private val optionList = mutableMapOf<Int, QueryTouristRouteOption>()
        init {
            val availableOptions = arrayOf(
                    QueryTouristRouteOption.EXIT,
                    QueryTouristRouteOption.ANY_DEPARTURE_AND_DESTINATION,
                    QueryTouristRouteOption.CIRCUIT
            )
            var ordinal = 0
            availableOptions.forEach {
                optionList[ordinal++] = it
            }
        }

        fun run() {
            // 没有景点则返回上级
            val isSpotAvailable = UiUtil.checkIfAnySpotIsAvailable()
            if (!isSpotAvailable)
                return

            // 如果有景点没有连通，则不能遍历所有景点，提示该错误
            val isAllSpotsConnected = RouteManager.routeMap.values.none { it.isEmpty }
            if (!isAllSpotsConnected) {
                UiUtil.printErrorMessage(UiUtil.getString("notAllSpotsAreConnected"))
                println(UiUtil.getString("pressEnterToContinue"))
                readLine()
                return
            }

            while (true) {
                println(UiUtil.getString("queryTouristRoute"))
                // 显示可选模式
                optionList.forEach { t, u ->
                    println("\t$t. ${UiUtil.getString(u.name, true)}")
                }

                // 等待并检验用户的响应
                println(UiUtil.getString("selectAnOption"))
                var option = QueryTouristRouteOption.PLACEHOLDER
                var pass: Boolean
                do {
                    try {
                        pass = true
                        val resp = readLine()!!.toInt()
                        if (!optionList.containsKey(resp))
                            error("")
                        option = optionList[resp]!!
                    } catch (e: Exception) {
                        pass = false
                        UiUtil.printErrorMessage(UiUtil.getString("invalidResponse"))
                    }
                } while (!pass)

                // 调用相关方法
                val isBreak = invoke(option)
                if (isBreak)
                    break
            }
        }

        private fun invoke(option: QueryTouristRouteOption): Boolean {
            when (option) {
                QueryTouristRouteOption.PLACEHOLDER ->
                    throw IllegalStateException(UiUtil.getString("optionIsPlaceholder"))
                QueryTouristRouteOption.EXIT -> return true
                QueryTouristRouteOption.ANY_DEPARTURE_AND_DESTINATION, QueryTouristRouteOption.CIRCUIT ->
                    queryTouristRoute(option)
            }
            return false
        }

        private fun queryTouristRoute(option: QueryTouristRouteOption) {
            var departure: String
            while (true) {
                // 等待用户输入起点并检验
                print(UiUtil.getString("enterDeparture"))
                println(UiUtil.getString("enterZeroToExit"))

                var pass: Boolean
                var isBreak = false
                do {
                    pass = true
                    departure = readLine()!!
                    if (departure == "0") {
                        isBreak = true
                        break
                    }
                    if (departure !in SpotManager.spotMap.keys) {
                        pass = false
                        UiUtil.printErrorMessage(UiUtil.getString("theSpotDoesNotExist"))
                    }
                } while (!pass)

                if (isBreak)
                    break

                // 等待用户输入终点并检验。若是回路模式则不输终点
                var destination: String? = departure
                if (option == QueryTouristRouteOption.ANY_DEPARTURE_AND_DESTINATION) {
                    print(UiUtil.getString("enterDestination"))
                    print(UiUtil.getString("pressEnterToKeepTheDefault"))
                    println(UiUtil.getString("enterZeroToExit"))
                    do {
                        pass = true
                        destination = readLine()!!
                        if (destination == "0") {
                            isBreak = true
                            break
                        } else if (destination.trim().isEmpty()) {
                            destination = null
                        } else if (destination !in SpotManager.spotMap.keys) {
                            pass = false
                            UiUtil.printErrorMessage(UiUtil.getString("theSpotDoesNotExist"))
                        }
                    } while (!pass)

                    if (isBreak)
                        break
                }

                // 只有不指定目的地或者要求回路的时候，通过回溯算法获得结果（该算法不支持指定目的地）
                var plannedRouteBacktracking: PlannedRoute? = null
                if (destination == null || departure == destination) {
                    plannedRouteBacktracking = BacktrackingGraphIterator(
                            RouteManager.routeMap.toMap(),
                            SpotManager.spotMap.keys.count(),
                            departure,
                            departure == destination
                    ).planRoute()
                }

                // 获取以 departure 为起点的最小生成树
                val mst = MstGenerator.generate(departure)
                // 通过自定义算法遍历该最小生成树获得的结果
                val plannedRouteCustom = MstIterator.planRoute(mst, destination)

                // 比较结果
                var bestResult = plannedRouteBacktracking
                var achievedBy = UiUtil.getString("backtrackingAlgorithm")
                if (plannedRouteBacktracking == null || plannedRouteCustom.totalDistance < plannedRouteBacktracking.totalDistance) {
                    bestResult = plannedRouteCustom
                    achievedBy = UiUtil.getString("customAlgorithm")
                }

                // 展示结果
                println(String.format(UiUtil.getString("bestResultAchievedBy"), achievedBy))
                bestResult as PlannedRoute
                bestResult.routeList.forEachIndexed { index, route ->
                    println("${index + 1}. $route")
                }
                println("${UiUtil.getString("totalDistance")}${UiUtil.beautifyDouble(bestResult.totalDistance)}")

                // 询问是否继续查询
                print(UiUtil.getString("isContinueQuerying"))
                println(UiUtil.getString("yesOrNo"))
                val isContinue = UiUtil.enterChoice()
                if (!isContinue)
                    break
            }
        }
    }
}

private enum class QueryTouristRouteOption {
    PLACEHOLDER,
    ANY_DEPARTURE_AND_DESTINATION, CIRCUIT,
    EXIT
}