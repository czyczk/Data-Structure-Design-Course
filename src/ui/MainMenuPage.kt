package ui

import util.UiUtil

/**
 * MainMenuPage
 * 这个类包含功能列表界面所需的方法和属性。
 * @param identity 用户的身份。依此决定是否允许访问管理员功能。
 * Created by Zenas Chen (czyczk) on 2017-8-30.
 */
class MainMenuPage(identity: LoginOption) {
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
        UiUtil.showTitle()
        println(UiUtil.getString("mainMenuPageTip")) // 显示操作提示
        // 每个身份选项显示一行，格式为“操作序号. 枚举值对应的字符串”
        optionList.forEach { t, u ->
            val optionStr = UiUtil.getString(u.name, true)
            println("\t$t. $optionStr")
        }
    }
}