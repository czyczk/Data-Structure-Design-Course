package ui.function

import manager.FileManager
import manager.RouteManager
import manager.SpotManager
import model.Route
import util.UiUtil

class RouteManagementPage {
    companion object {
        private val optionList = mutableMapOf<Int, RouteManagementOption>()
        init {
            val availableOptions = arrayOf(
                    RouteManagementOption.EXIT,
                    RouteManagementOption.ADD_ROUTES,
                    RouteManagementOption.MODIFY_ROUTES,
                    RouteManagementOption.REMOVE_ROUTES
            )
            var ordinal = 0
            availableOptions.forEach {
                optionList.put(ordinal++, it)
            }
        }

        fun run() {
            while (true) {
                // 显示标题和选项
                println(UiUtil.getString("routeManagement"))
                optionList.forEach { t, u ->
                    println("\t$t. ${UiUtil.getString(u.name, true)}")
                }

                // 等待并检验用户的响应
                println(UiUtil.getString("selectAnOption"))
                var option = RouteManagementOption.PLACEHOLDER
                var pass = true
                do {
                    try {
                        val resp = readLine()?.toInt()
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

        private fun invoke(option: RouteManagementOption): Boolean {
            when (option) {
                RouteManagementOption.PLACEHOLDER ->
                    throw IllegalStateException(UiUtil.getString("optionIsPlaceholder"))
                RouteManagementOption.EXIT -> return true
                RouteManagementOption.ADD_ROUTES -> addRoutes()
                RouteManagementOption.MODIFY_ROUTES -> modifyRoutes()
                RouteManagementOption.REMOVE_ROUTES -> removeRoutes()
            }
            return false
        }

        private fun addRoutes() {
            println(UiUtil.getString("addRoutes")) // 显示功能标题
            val pendingList = mutableListOf<Route>() // 待用户确认更改后加入的线路列表

            while (true) {
                // 显示线路总数（含待确认线路）
                print(
                        String.format(
                                UiUtil.getString("totalNumberOfRoutes"),
                                RouteManager.routeMap.count() + pendingList.count()
                        )
                )
                // 显示待确认线路数
                if (pendingList.isEmpty())
                    println()
                else {
                    println(
                            String.format(
                                    UiUtil.getString("totalNumberOfPendingRoutes"),
                                    pendingList.count()
                            )
                    )
                }

                // 输入并检验线路两端景点的名称
                // 景点 1
                print(UiUtil.getString("enterNameOfSpot1"))
                println(UiUtil.getString("enterZeroToExit"))
                var name1 = ""
                var pass: Boolean
                var isBreak = false
                do {
                    pass = true
                    name1 = readLine()!!
                    // 检查输入是否为 0
                    if (name1 == "0") {
                        isBreak = true
                        break
                    }
                    // 检查该景点是否存在
                    if (!SpotManager.spotMap.containsKey(name1)) {
                        // 若该景点不存在则重新输入名称
                        UiUtil.printErrorMessage(UiUtil.getString("theSpotDoesNotExist"))
                        pass = false
                    }
                } while (!pass)

                if (isBreak)
                    break

                // 景点 2
                print(UiUtil.getString("enterNameOfSpot2"))
                println(UiUtil.getString("enterZeroToExit"))
                var name2 = ""
                do {
                    pass = true
                    name2 = readLine()!!
                    // 检查输入是否为 0
                    if (name2 == "0") {
                        isBreak = true
                        break
                    }
                    // 检查是否与前一名称相同
                    if (name1 == name2) {
                        UiUtil.printErrorMessage(UiUtil.getString("departureAndDestinationAreTheSame"))
                        pass = false
                    }
                    // 检查该景点是否存在
                    else if (!SpotManager.spotMap.containsKey(name2)) {
                        // 若该景点不存在则重新输入名称
                        UiUtil.printErrorMessage(UiUtil.getString("theSpotDoesNotExist"))
                        pass = false
                    }
                } while (!pass)

                if (isBreak)
                    break

                // 检查此条距离是否存在
                if (Route(name1, name2) in pendingList.map { Route(it.name1, it.name2) }) {
                    UiUtil.printErrorMessage(UiUtil.getString("theRouteIsPending"))
                    continue
                } else if (RouteManager.query(name1, name2) != null) {
                    UiUtil.printErrorMessage(UiUtil.getString("theRouteAlreadyExists"))
                    continue
                }

                // 输入距离
                println(UiUtil.getString("enterDistance"))
                val distance = UiUtil.enterDouble(false)

                // 根据以上信息生成 Route 对象并加入 pendingList
                val route = Route(name1, name2, distance)
                pendingList.add(route)

                // 询问是否继续添加
                print(UiUtil.getString("isContinueAdding"))
                println(UiUtil.getString("yesOrNo"))
                val isContinueAdding = UiUtil.enterChoice()
                if (!isContinueAdding)
                    break
            }

            if (pendingList.isNotEmpty()) {
                // 列出要添加的线路
                println(UiUtil.getString("routesToBeAdded"))
                var ordinal = 1
                pendingList.forEach {
                    println("\t${ordinal++}. $it")
                }

                // 询问是否保存
                print(UiUtil.getString("areChangesToBeSaved"))
                println(UiUtil.getString("yesOrNo"))
                val isSave = UiUtil.enterChoice()
                if (isSave) {
                    pendingList.forEach { RouteManager.add(it) }
                    FileManager.saveAllRoutes()
                }
            }
        }

        private fun modifyRoutes() {
            println(UiUtil.getString("modifyRoutes"))

            // 检查是否有线路可以修改，不可以则返回上级
            val routeIsAvailable = UiUtil.checkIfAnyRouteIsAvailable()
            if (!routeIsAvailable)
                return

            val pendingList = mutableListOf<Route>() // 待用户确认更改的线路列表

            while (true) {
                // 显示当前线路数量
                print(
                        String.format(
                                UiUtil.getString("totalNumberOfRoutes"),
                                RouteManager.routeMap.count()
                        )
                )
                // 显示待确认线路数
                if (pendingList.isEmpty())
                    println()
                else {
                    println(
                            String.format(
                                    UiUtil.getString("totalNumberOfPendingRoutes"),
                                    pendingList.count()
                            )
                    )
                }

                // 等待输入并检验名称 1
                print(UiUtil.getString("enterNameOfSpot1"))
                println(UiUtil.getString("enterZeroToExit"))
                var pass: Boolean
                var isBreak = false
                var name1 = ""

                do {
                    pass = true
                    name1 = readLine()!!
                    if (name1 == "0") {
                        isBreak = true
                        break
                    }

                    if (name1 !in SpotManager.spotMap.keys) {
                        UiUtil.printErrorMessage(UiUtil.getString("theSpotDoesNotExist"))
                        pass = false
                    }
                } while (!pass)

                if (isBreak)
                    break

                // 等待输入并检验名称 2
                print(UiUtil.getString("enterNameOfSpot2"))
                println(UiUtil.getString("enterZeroToExit"))
                var name2 = ""

                do {
                    pass = true
                    name2 = readLine()!!
                    if (name2 == "0") {
                        isBreak = true
                        break
                    }

                    if (name1 == name2) {
                        UiUtil.printErrorMessage(UiUtil.getString("departureAndDestinationAreTheSame"))
                        pass = false
                    } else if (name2 !in SpotManager.spotMap.keys) {
                        UiUtil.printErrorMessage(UiUtil.getString("theSpotDoesNotExist"))
                        pass = false
                    }
                } while (!pass)

                if (isBreak)
                    break

                // 检验该线路是否不存在（取出旧线路，可能是二次修改先从待确认列表中取，再从 routeMap 中取）
                var oldRoute: Route? = pendingList.firstOrNull {
                    (it.name1 == name1 && it.name2 == name2) || (it.name1 == name2 && it.name2 == name1)
                }
                if (oldRoute == null)
                    oldRoute = RouteManager.query(name1, name2)

                if (oldRoute == null) {
                    UiUtil.printErrorMessage(UiUtil.getString("theRouteDoesNotExist"))
                    continue
                }

                // 输入距离
                println(UiUtil.getString("enterDistance"))
                val distance = UiUtil.enterDouble(false)

                // 若新距离不一样则加入待确认列表（注意可能是二次修改）
                val newRoute = Route(name1, name2, distance)
                if (oldRoute != newRoute) {
                    pendingList.remove(oldRoute)
                    pendingList.add(Route(name1, name2, distance))
                }

                // 询问是否继续添加
                print(UiUtil.getString("isContinueModifying"))
                println(UiUtil.getString("yesOrNo"))
                val isContinueAdding = UiUtil.enterChoice()
                if (!isContinueAdding)
                    break
            }

            if (pendingList.isNotEmpty()) {
                // 列出所有已修改待确认的线路
                println(UiUtil.getString("routesToBeModified"))
                var ordinal = 1
                pendingList.forEach {
                    println("\t${ordinal++}. $it")
                }

                // 询问是否保存
                print(UiUtil.getString("areChangesToBeSaved"))
                println(UiUtil.getString("yesOrNo"))
                val isSave = UiUtil.enterChoice()
                if (isSave) {
                    pendingList.forEach { RouteManager.updateDistance(it) }
                    FileManager.saveAllRoutes()
                }
            }
        }

        private fun removeRoutes() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}

private enum class RouteManagementOption {
    PLACEHOLDER, ADD_ROUTES, MODIFY_ROUTES, REMOVE_ROUTES, EXIT
}