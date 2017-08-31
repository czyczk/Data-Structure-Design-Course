package ui

import util.UiUtil

class LoginPage {
    companion object {
        // 存储 操作序号 -> 身份枚举值 的键值对，作为选项列表
        private val optionList: Map<Int, LoginOption>

        // 初始化 optionList
        init {
            optionList = mutableMapOf()
            // 关联操作序号与身份枚举值，存入 optionList
            LoginOption.values().forEach { it -> optionList.put( it.ordinal + 1, it ) } // 操作序号从 1 开始
        }

        /**
         * 显示身份选项。择一以登录。
         */
        fun showMenu() {
            while (true) {
                UiUtil.showTitle()
                println(UiUtil.getString("loginPageTip")) // 显示选项介绍
                // 每个身份选项显示一行，格式为“操作序号. 枚举值对应的字符串”
                optionList.forEach { t, u ->
                    val optionStr = UiUtil.getString(u.name, true)
                    println("\t$t. $optionStr")
                }
                println(UiUtil.getString("selectAnOption")) // 提示用户做出选择

                // 等待用户选择身份
                var pass = false
                var resp: Int? = null
                var identity: LoginOption? = null
                do {
                    try {
                        // 若用户的回应为数字且包含在 optionList 的操作序号中，则为有效回应
                        resp = readLine()?.toIntOrNull()
                        if (optionList.containsKey(resp)) pass = true
                    } catch (e: NumberFormatException) {
                    } finally {
                        // 无效回应则提示，有效回应则获取相应身份
                        if (!pass) println(UiUtil.getString("invalidResponse"))
                        else identity = optionList[resp]
                    }
                } while (!pass)

                // 如果选择管理员登录，则需使用密码验证身份
                if (identity == LoginOption.ADMINISTRATOR) {
                    println("输入密码：")
                    val isIdentityVerified = UiUtil.verifyAdministratorIdentity()
                    if (!isIdentityVerified) continue   // 密码错误则返回登录界面
                }

                // 如果选择游客或通过管理员身份验证，则进入相应功能列表界面
                MainMenuPage(identity!!).showMenu()
            }
        }
    }
}