package ui.function

import manager.FileManager
import manager.SpotManager
import model.Spot
import util.UiUtil

class ScenicSpotManagementPage {
    companion object {
        private val optionList = mutableMapOf<Int, ScenicSpotManagementOption>()
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
                ScenicSpotManagementOption.PLACEHOLDER ->
                    throw IllegalStateException(UiUtil.getString("optionIsPlaceholder"))
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
                val popularity = enterDouble(false)

                // 有无休息区
                print(UiUtil.getString("isRestAreaProvided"))
                println(UiUtil.getString("yesOrNo"))
                val isRestAreaProvided = enterChoice()

                // 有无厕所
                print(UiUtil.getString("isToiletProvided"))
                println(UiUtil.getString("yesOrNo"))
                val isToiletProvided = enterChoice()

                // 根据以上信息生成 Spot 对象并加入 pendingList
                val spot = Spot(name, introduction, popularity, isRestAreaProvided, isToiletProvided)
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
            println(UiUtil.getString("modifyScenicSpots")) // 显示功能标题

            // 检查是否有景点可以修改，不可以则返回上级
            val spotIsAvailable = checkIfAnySpotIsAvailable()
            if (!spotIsAvailable)
                return


            val pendingList = mutableMapOf<String, Spot>() // 待用户确认更改的景点列表 <旧名, Spot>
            //val nameChangesList = mutableMapOf<String, String>() // 待确认的改名 <旧名, 新名>
            val availableSpots = mutableMapOf<Int, Spot>() // 将 spotMap 中信息拷贝至此，该列表旧名不变 <序号, Spot>
            // 填充 availableSpots
            var ordinal = 1
            SpotManager.spotMap.values.forEach { availableSpots.put(ordinal++, it) }

            /*
             * 思路：
             * 总体：
             * 依次询问是否更改名称、简介、欢迎度、休息区、厕所等信息。
             *
             * 关于用户的选择：
             * 用户通过序号选择一个要修改的景点后，将从 availableSpots 中找到 Spot 实体。
             *
             * 关于名称修改：
             * 1. 显示在屏幕上的可选景点需要同时参考 availableSpots 和 pendingList。
             * 2. availableSpots 提供序号和对应原景点名，实际显示的景点名通过 pendingList 映射。
             *    即达到用户看到的是新景点名，但 availableSpots 中名称不变。由此满足用户可能多次更改景点名称的情景。
             *
             * 关于属性修改：
             * 依次询问属性时会先显示原来的/最新的属性，先从 pendingList 获取最新的信息，
             * 若尚未被修改则从 availableSpots 中获取原信息。
             */

            while (true) {
                // 显示景点总数
                print(
                        String.format(
                                UiUtil.getString("totalNumberOfSpots"),
                                SpotManager.spotMap.count()
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

                // 显示所有景点名称（按改名后名称显示）
                availableSpots.forEach { t, u ->
                    val name: String
                    if (pendingList.containsKey(u.name))
                        name = pendingList[u.name]!!.name + " *" // 显示新名（带 * 号）
                    else
                        name = u.name // 没有被更改则显示原名
                    println("\t$t. $name")
                }

                print(UiUtil.getString("selectAnOption"))
                println(UiUtil.getString("enterZeroToExit"))

                // 等待并检验用户回应
                var curSpot: Spot? = null
                var curName: String? = null // curSpot 在 availableSpots 或 pendingList 中对应的 key 名
                var pass: Boolean
                var isBreak = false
                do {
                    try {
                        pass = true
                        val resp = readLine()!!
                        // 0 以退出
                        if (resp == "0") {
                            isBreak = true
                            break
                        }
                        val respInt = resp.toInt()
                        if (!availableSpots.containsKey(respInt))
                            error("")
                        curSpot = availableSpots[respInt]!! // availableSpots 中序号对应的实体为待更改的景点
                        curName = curSpot.name
                        if (pendingList.containsKey(curSpot.name))
                            curSpot = pendingList[curSpot.name]!! // 若该景点已被修改过则在 pendingList 中找
                    } catch (e: Exception) {
                        System.err.println(UiUtil.getString("invalidResponse"))
                        pass = false
                    }
                } while (!pass)

                if (isBreak)
                    break

                // 显示景点详情
                curSpot as Spot
                curName as String
                println(curSpot)

                // 询问是否换名
                var name = curSpot.name
                print(UiUtil.getString("isNameToBeChanged"))
                println(UiUtil.getString("yesOrNo"))
                if (enterChoice()) {
                    println(UiUtil.getString("enterNameOfSpot"))

                    // 检查是否重名
                    do {
                        pass = true
                        name = readLine()!!
                        if (name != curName) {
                            if (SpotManager.spotMap.containsKey(name))
                                pass = false
                            else {
                                for (s in pendingList.values) {
                                    if (name == s.name)
                                        pass = false
                                }
                            }
                        }
                        if (!pass) {
                            System.err.println(UiUtil.getString("nameIsDuplicated"))
                        }
                    } while (!pass)
                }

                // 询问是否换简介
                var introduction = curSpot.introduction
                print(UiUtil.getString("isIntroductionToBeChanged"))
                println(UiUtil.getString("yesOrNo"))
                if (enterChoice()) {
                    println(UiUtil.getString("enterIntroduction"))
                    introduction = readLine()!!
                }

                // 询问是否换欢迎度
                var popularity = curSpot.popularity
                print(UiUtil.getString("isPopularityToBeChanged"))
                println(UiUtil.getString("yesOrNo"))
                if (enterChoice()) {
                    println(UiUtil.getString("enterPopularity"))
                    popularity = enterDouble(false)
                }

                // 是否更改休息区设置
                var isRestAreaProvided = curSpot.isRestAreaProvided
                print(UiUtil.getString("isRestAreaToBeToggled"))
                println(UiUtil.getString("yesOrNo"))
                if (enterChoice())
                    isRestAreaProvided = !isRestAreaProvided

                // 是否更改厕所设置
                var isToiletProvided = curSpot.isToiletProvided
                print(UiUtil.getString("isToiletToBeToggled"))
                println(UiUtil.getString("yesOrNo"))
                if (enterChoice())
                    isToiletProvided = !isToiletProvided

                // 检查是否被更改，被更改则加入 pendingList，或覆盖 pendingList 中上一次修改
                val newSpot = Spot(name, introduction, popularity, isRestAreaProvided, isToiletProvided)
                if (curSpot != newSpot)
                    pendingList.put(curName, newSpot)

                // 询问是否继续修改
                print(UiUtil.getString("isContinueModifying"))
                println(UiUtil.getString("yesOrNo"))
                val isContinueModifying = enterChoice()
                if (!isContinueModifying)
                    break
            }

            // 显示所有已修改待确认的景点
            if (pendingList.isNotEmpty()) {
                println(UiUtil.getString("scenicSpotsToBeModified"))
                ordinal = 1
                pendingList.forEach { t, u ->
                    println("\t${ordinal++}. $u")
                }

                // 询问是否保存
                print(UiUtil.getString("areChangesToBeSaved"))
                println(UiUtil.getString("yesOrNo"))
                val isSave = enterChoice()
                if (isSave) {
                    pendingList.forEach { t, u ->
                        SpotManager.update(t, u)
                    }
                    FileManager.saveAllSpots()
                }
            }
        }

        private fun removeScenicSpots() {
            println(UiUtil.getString("removeScenicSpots")) // 显示功能标题

            // 检查是否有景点可以删除，不可以则返回上级
            val spotIsAvailable = checkIfAnySpotIsAvailable()
            if (!spotIsAvailable)
                return


            val pendingList = mutableListOf<String>() // 待用户确认删除的景点列表
            val availableSpots = mutableMapOf<Int, String>() // 将 spotMap 中信息拷贝至此，该列表旧名不变 <序号, 名称>
            // 填充 availableSpots
            var ordinal = 1
            SpotManager.spotMap.values.forEach { availableSpots.put(ordinal++, it.name) }

            while (true) {
                // 显示景点总数
                print(
                        String.format(
                                UiUtil.getString("totalNumberOfSpots"),
                                SpotManager.spotMap.count()
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

                // 显示所有景点名称
                availableSpots.forEach { t, u ->
                    var name = u
                    if (pendingList.contains(u))
                        name += " -" // 若为待删除状态则带 - 号
                    println("\t$t. $name")
                }

                print(UiUtil.getString("selectAnOption"))
                println(UiUtil.getString("enterZeroToExit"))

                // 等待并检验用户回应
                var pass: Boolean
                var isBreak = false
                do {
                    try {
                        pass = true
                        val resp = readLine()!!
                        // 0 以退出
                        if (resp == "0") {
                            isBreak = true
                            break
                        }
                        val respInt = resp.toInt()
                        if (!availableSpots.containsKey(respInt))
                            throw IllegalStateException()
                        val nameOfSpotToBeRemoved = availableSpots[respInt]!! // availableSpots 中序号对应的景点名称
                        if (pendingList.contains(nameOfSpotToBeRemoved))
                            throw UnsupportedOperationException()
                        pendingList.add(nameOfSpotToBeRemoved)
                    } catch (e: Exception) {
                        if (e is IllegalStateException || e is NumberFormatException)
                            System.err.println(UiUtil.getString("invalidResponse"))
                        else if (e is UnsupportedOperationException)
                            System.err.println(UiUtil.getString("spotHasBeenMarkedToBeRemoved"))
                        pass = false
                    }
                } while (!pass)

                if (isBreak)
                    break

                // 询问是否继续修改
                print(UiUtil.getString("isContinueRemoving"))
                println(UiUtil.getString("yesOrNo"))
                val isContinueModifying = enterChoice()
                if (!isContinueModifying)
                    break
            }

            // 显示所有待确认删除的景点
            if (pendingList.isNotEmpty()) {
                println(UiUtil.getString("scenicSpotsToBeRemoved"))
                ordinal = 1
                pendingList.forEach {
                    println("\t${ordinal++}. $it")
                }

                // 询问是否保存
                print(UiUtil.getString("areChangesToBeSaved"))
                println(UiUtil.getString("yesOrNo"))
                val isSave = enterChoice()
                if (isSave) {
                    pendingList.forEach {
                        SpotManager.remove(it)
                    }
                    FileManager.saveAllSpots()
                }
            }
        }

        private fun enterDouble(isNegativeAllowed: Boolean = true): Double {
            var pass: Boolean
            var value = 0.0
            do {
                pass = true
                try {
                    value = readLine()!!.toDouble()
                    if (!isNegativeAllowed && value < 0)
                        throw IllegalArgumentException(UiUtil.getString("cannotBeNegative"))
                } catch (e: NumberFormatException) {
                    System.err.println(UiUtil.getString("invalidResponse"))
                    pass = false
                } catch (e: IllegalArgumentException) {
                    System.err.println(e.message)
                    pass = false
                }
            } while (!pass)
            return value
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

        private fun checkIfAnySpotIsAvailable(): Boolean {
            if (SpotManager.spotMap.count() == 0) {
                System.err.print(UiUtil.getString("noSpotAvailable"))
                println(UiUtil.getString("pressEnterToContinue"))
                readLine()
                return false
            }
            return true
        }
    }
}