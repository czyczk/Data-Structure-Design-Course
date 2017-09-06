package ui.function

import manager.FileManager
import manager.SpotManager
import model.Spot
import util.UiUtil

class ScenicSpotManagementPage {
    companion object {
        val optionList = mutableMapOf<Int, ScenicSpotManagementOption>()
        init {
            val availableOptions = arrayOf(
                    ScenicSpotManagementOption.EXIT,
                    ScenicSpotManagementOption.ADD_SCENIC_SPOTS,
                    ScenicSpotManagementOption.MODIFY_SCENIC_SPOTS,
                    ScenicSpotManagementOption.REMOVE_SCENIC_SPOTS
            )
            var ordinal = 0
            availableOptions.forEach {
                optionList.put(ordinal++, it)
            }
        }

        fun run() {
            while (true) {
                // 显示标题和选项
                println(UiUtil.getString("scenicSpotManagement"))
                optionList.forEach { t, u ->
                    println("\t$t. ${UiUtil.getString(u.name, true)}")
                }

                // 等待并检验用户的响应
                println(UiUtil.getString("selectAnOption"))
                var option = ScenicSpotManagementOption.PLACEHOLDER
                var pass = true
                do {
                    try {
                        val resp = readLine()?.toInt()
                        if (!optionList.containsKey(resp))
                            error("")
                        option = optionList[resp]!!
                    } catch (e: Exception) {
                        pass = false
                        System.err.println(UiUtil.getString("invalidResponse"))
                    }
                } while (!pass)

                // 调用相关方法
                val isBreak = invoke(option)
                if (isBreak)
                    break
            }
        }

        private fun invoke(option: ScenicSpotManagementOption): Boolean {
            when (option) {
                ScenicSpotManagementOption.EXIT -> return true
                ScenicSpotManagementOption.ADD_SCENIC_SPOTS -> addScenicSpots()
                ScenicSpotManagementOption.MODIFY_SCENIC_SPOTS -> modifyScenicSpots()
                ScenicSpotManagementOption.REMOVE_SCENIC_SPOTS -> removeScenicSpots()
            }
            return false
        }

        private fun addScenicSpots() {
            println(UiUtil.getString("addScenicSpots")) // 显示功能标题
            val pendingList = mutableListOf<Spot>() // 待用户确认更改后加入的景点列表

            while (true) {
                // 显示景点总数（含待确认景点）
                print(
                        String.format(
                                UiUtil.getString("totalNumberOfSpots"),
                                SpotManager.spotMap.count() + pendingList.count()
                        )
                )
                // 显示待确认景点数
                if (pendingList.isEmpty())
                    println()
                else {
                    println(
                            String.format(
                                    UiUtil.getString("totalNumberOfPendingSpots"),
                                    pendingList.count()
                            )
                    )
                }

                // 要添加的名称
                print(UiUtil.getString("enterNameOfSpot"))
                println(UiUtil.getString("enterZeroToExit"))
                val name = readLine()!!
                // 检查输入是否为 0
                if (name == "0")
                    break
                // 检查是否已有该景点
                if (SpotManager.spotMap.containsKey(name)) {
                    // 若已有该景点则重新输入名称
                    System.err.println(UiUtil.getString("theSpotAlreadyExists"))
                    continue
                }

                // 景点的简介
                println(UiUtil.getString("enterIntroduction"))
                val introduction = readLine()!!

                // 欢迎度
                println(UiUtil.getString("enterPopularity"))
                val popularity = enterPopularity()

                // 有无休息区
                print(UiUtil.getString("isRestAreaAvailable"))
                println(UiUtil.getString("yesOrNo"))
                val isRestAreaAvailable = enterChoice()

                // 有无厕所
                print(UiUtil.getString("isToiletAvailable"))
                println(UiUtil.getString("yesOrNo"))
                val isToiletAreaAvailable = enterChoice()

                // 根据以上信息生成 Spot 对象并加入 pendingList
                val spot = Spot(name, introduction, popularity, isRestAreaAvailable, isToiletAreaAvailable)
                pendingList.add(spot)

                // 询问是否继续添加
                print(UiUtil.getString("isContinueAdding"))
                println(UiUtil.getString("yesOrNo"))
                val isContinueAdding = enterChoice()
                if (!isContinueAdding)
                    break
            }

            if (pendingList.isNotEmpty()) {
                // 列出要添加的景点
                println(UiUtil.getString("scenicSpotsToBeAdded"))
                var ordinal = 1
                pendingList.forEach {
                    println("\t${ordinal++}. ${it.name}")
                }

                // 询问是否保存
                print(UiUtil.getString("areChangesToBeSaved"))
                println(UiUtil.getString("yesOrNo"))
                val isSave = enterChoice()
                if (isSave) {
                    pendingList.forEach { SpotManager.add(it) }
                    FileManager.saveAllSpots()
                }
            }
        }

        private fun modifyScenicSpots() {

        }

        private fun removeScenicSpots() {

        }

        private fun enterPopularity(): Double {
            var pass: Boolean
            var popularity = 0.0
            do {
                pass = true
                try {
                    popularity = readLine()!!.toDouble()
                } catch (e: NumberFormatException) {
                    System.err.println(UiUtil.getString("invalidResponse"))
                    pass = false
                }
            } while (!pass)
            return popularity
        }

        private fun enterChoice(): Boolean {
            var pass: Boolean
            var choice = false
            do {
                pass = true
                try {
                    choice = UiUtil.parseInputToChoice(readLine()!!)
                } catch (e: IllegalArgumentException) {
                    System.err.println(UiUtil.getString("invalidResponse"))
                    pass = false
                }
            } while (!pass)
            return choice
        }
    }
}