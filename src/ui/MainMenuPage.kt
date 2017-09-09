package ui

import ui.function.QueryScenicSpot
import ui.function.ScenicSpotManagementPage
import ui.function.ViewScenicSpotMap
import util.UiUtil

/**
 * MainMenuPage
 * 这个类包含功能列表界面所需的方法和属性。
 * @param identity 用户的身份。依此决定是否允许访问管理员功能。
 * Created by Zenas Chen (czyczk) on 2017-8-30.
 */
class MainMenuPage(private val identity: LoginOption) {
    // 存储 操作序号 -> 功能枚举值 的键值对，作为选项列表
    private val optionList: Map<Int, MainMenuOption>

    // 根据身份初始化 optionList
    init {
        optionList = mutableMapOf()

        // 筛选该用户可访问的功能的枚举值
        val availableOptions: Array<MainMenuOption>
        when (identity) {
            LoginOption.ADMINISTRATOR -> availableOptions = arrayOf(
                    MainMenuOption.BACK_TO_LOGIN_PAGE,
                    MainMenuOption.SCENIC_SPOT_MANAGEMENT,
                    MainMenuOption.ROUTE_MANAGEMENT,
                    MainMenuOption.QUERY_SCENIC_SPOT,
                    MainMenuOption.VIEW_SCENIC_SPOT_MAP,
                    MainMenuOption.QUERY_DISTANCE,
                    MainMenuOption.VIEW_TOURIST_ROUTE,
                    MainMenuOption.PARKING_LOT_EMULATOR,
                    MainMenuOption.NOTICE_MANAGEMENT,
                    MainMenuOption.VIEW_NOTICE,
                    MainMenuOption.EXIT
            )
            LoginOption.TOURIST      -> availableOptions = arrayOf(
                    MainMenuOption.BACK_TO_LOGIN_PAGE,
                    MainMenuOption.QUERY_SCENIC_SPOT,
                    MainMenuOption.VIEW_SCENIC_SPOT_MAP,
                    MainMenuOption.QUERY_DISTANCE,
                    MainMenuOption.VIEW_TOURIST_ROUTE,
                    MainMenuOption.VIEW_NOTICE
            )
            else                     -> throw NotImplementedError()
        }

        // 关联操作序号与功能枚举值，存入 optionList
        var ordinal = 0 // 操作序号从 0 开始
        availableOptions.forEach { optionList.put( ordinal++, it ) }
    }

    /**
     * 显示选项，引导用户选择。
     */
    fun showMenu() {
        while (true) {
            // 显示标题和选项
            UiUtil.showTitle()
            showOptions()

            // 等待并检查用户的选择
            var pass: Boolean
            var option = MainMenuOption.PLACEHOLDER
            do {
                pass = true
                try {
                    val response = readLine()?.toInt()
                    if (!optionList.containsKey(response))
                        error("")
                    option = optionList[response]!!
                } catch (e: Exception) {
                    pass = false
                    System.err.println(UiUtil.getString("invalidResponse"))
                }
            } while (!pass)

            // 引发相应功能
            val isBreak = invoke(option)
            if (isBreak)
                break
        }
    }

    private fun showOptions() {
        // 显示欢迎信息
        var stringKey: String
        when (identity) {
            LoginOption.ADMINISTRATOR -> stringKey = "helloAdministrator"
            LoginOption.TOURIST       -> stringKey = "helloTourist"
        }
        println(UiUtil.getString(stringKey))
        // 每个身份选项显示一行，格式为“操作序号. 枚举值对应的字符串”
        optionList.forEach { t, u ->
            val optionStr = UiUtil.getString(u.name, true)
            println("\t$t. $optionStr")
        }
        println(UiUtil.getString("selectAnOption")) // 显示操作提示
    }

    private fun invoke(option: MainMenuOption): Boolean {
        when (option) {
            // PLACEHOLDER: nonsense
            MainMenuOption.PLACEHOLDER -> error(UiUtil.getString("optionIsPlaceholder"))
            // EXIT: exit the program
            MainMenuOption.EXIT -> {
                println(UiUtil.getString("bye"))
                System.exit(0)
            }
            // BACK_TO_LOGIN_PAGE: break the loop in showMenu() and return to the Login Page
            MainMenuOption.BACK_TO_LOGIN_PAGE -> return true
            // SCENIC_SPOT_MANAGEMENT
            MainMenuOption.SCENIC_SPOT_MANAGEMENT -> ScenicSpotManagementPage.run()
            // ROUTE_MANAGEMENT
            MainMenuOption.ROUTE_MANAGEMENT -> TODO("Pending implementation")
            // QUERY_SCENIC_SPOT
            MainMenuOption.QUERY_SCENIC_SPOT -> QueryScenicSpot.run()
            // VIEW_SCENIC_SPOT_MAP
            MainMenuOption.VIEW_SCENIC_SPOT_MAP -> ViewScenicSpotMap.run()
        }
        return false
    }
}