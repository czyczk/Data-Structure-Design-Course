package ui

import util.UiUtil

class LoginPage {
    companion object {
        // 存储操作序号与相应身份枚举值
        private val optionList: Map<Int, LoginOption>
        init {
            optionList = mutableMapOf()
            LoginOption.values().forEach { it -> optionList.put( it.ordinal + 1, it ) } // 操作序号从 1 开始
        }

        /**
         * 显示身份选项。择一以登录。
         */
        fun showMenu() {
            UiUtil.showTitle()
            println(UiUtil.getString("loginPageTip")) // 显示操作提示
            // 每个身份选项显示一行，格式为“操作序号. 枚举值对应的字符串”
            optionList.forEach { t, u ->
                val optionStr = UiUtil.getString(u.name, true)
                println("\t$t. $optionStr")
            }
        }
    }
}