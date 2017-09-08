package ui.function

import manager.SpotManager
import util.UiUtil

class QueryScenicSpot {
    companion object {
        private val optionList = mutableMapOf<Int, QueryScenicSpotOption>()
        init {
            val availableOptions = arrayOf(
                    QueryScenicSpotOption.EXIT,
                    QueryScenicSpotOption.IN_NAME_ONLY,
                    QueryScenicSpotOption.IN_NAME_AND_INTRODUCTION
            )
            var ordinal = 0
            availableOptions.forEach {
                optionList[ordinal++] = it
            }
        }

        fun run() {
            while (true) {
                println(UiUtil.getString("queryScenicSpot"))
                // 显示可选模式
                optionList.forEach { t, u ->
                    println("\t$t. ${UiUtil.getString(u.name, true)}")
                }

                // 等待并检验用户的响应
                println(UiUtil.getString("selectAnOption"))
                var option = QueryScenicSpotOption.PLACEHOLDER
                var pass = true
                do {
                    try {
                        val resp = readLine()!!.toInt()
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

        private fun invoke(option: QueryScenicSpotOption): Boolean {
            when (option) {
                QueryScenicSpotOption.PLACEHOLDER ->
                    throw IllegalStateException(UiUtil.getString("optionIsPlaceholder"))
                QueryScenicSpotOption.EXIT -> return true
                QueryScenicSpotOption.IN_NAME_ONLY, QueryScenicSpotOption.IN_NAME_AND_INTRODUCTION ->
                    queryScenicSpot(option)
                QueryScenicSpotOption.ORDERED_BY_NAME, QueryScenicSpotOption.ORDERED_BY_POPULARITY ->
                    showAllSpotsOrderedBy(option)
            }
            return false
        }

        private fun queryScenicSpot(searchMode: QueryScenicSpotOption) {
            // 显示选项
            println(UiUtil.getString(searchMode.name, true))

            val isNameOnly = searchMode == QueryScenicSpotOption.IN_NAME_ONLY

            while (true) {
                // 等待用户输入关键词
                print(UiUtil.getString("enterKeywordOfSpot"))
                println(UiUtil.getString("enterZeroToExit"))
                val keyword = readLine()!!
                if (keyword == "0")
                    break
                val candidateList = SpotManager.queryContains(keyword, isNameOnly)
                if (candidateList.isNotEmpty()) {
                    var ordinal = 1
                    candidateList.forEach {
                        println("${ordinal++}. ${SpotManager.spotMap[it]!!}")
                    }
                } else
                    println(UiUtil.getString("noSpotFound"))

                // 询问是否继续查询
                print(UiUtil.getString("isContinueQuerying"))
                println(UiUtil.getString("yesOrNo"))
                val isContinue = UiUtil.enterChoice()
                if (!isContinue)
                    break
            }
        }

        private fun showAllSpotsOrderedBy(orderBy: QueryScenicSpotOption) {
            TODO()
        }
    }
}

private enum class QueryScenicSpotOption {
    PLACEHOLDER, IN_NAME_ONLY, IN_NAME_AND_INTRODUCTION, ORDERED_BY_NAME, ORDERED_BY_POPULARITY, EXIT
}