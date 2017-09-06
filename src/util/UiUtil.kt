package util

import java.nio.charset.Charset
import java.util.*

/**
 * UiUtil
 *
 * 此类提供系统中 UI 部分通用的方法。
 *
 * @author: Zenas Chen (czyczk)
 */
class UiUtil {
    companion object {
        // UI 字符串资源文件
        private val bundle = ResourceBundle.getBundle("UiString")

        /**
         * 根据 key 查找相应字符串。
         * 注：直接读取的字符串以 ISO-8859-1 编码，在返回前先转换为 UTF-8。
         * @param key 查找字符串的依据。
         * @param isCaseConversionRequired 是否将 key 转换为驼峰大小写。用于为 enum 获取字符串。
         */
        fun getString(key: String, isCaseConversionRequired: Boolean): String {
            return if (isCaseConversionRequired) getString(convertCamelCase(key), false)
            else convertToUTF8(bundle.getString(key))
        }
        fun getString(key: String) = getString(key, false)

        /**
         * 在登录界面和菜单界面显示标题。
         */
        fun showTitle() {
            println(UiUtil.getString("applicationTitleWithDecoration"))
        }

        fun verifyAdministratorIdentity(): Boolean {
            val input = readLine()
            // TODO Verify the password.
            return input == "password"
        }

        /**
         * 将用户输入转换为 boolean 值。
         * @param resp 可转换为 true 的值有“Y”、“true”；可转换为 false 的值有“N”、“false”。不区分大小写。
         * @return true 或 false
         */
        fun parseInputToChoice(resp: String): Boolean {
            if (resp.equals("Y", true) || resp.equals("true", true))
                return true
            else if (resp.equals("N", true) || resp.equals("false", true))
                return false
            else
                throw IllegalArgumentException("Not invalid input for a boolean value.")
        }

        /**
         * 转换全大写为驼峰大小写。
         * @param src 待转换的全大写的字符串。
         * @return 转换后依照驼峰规则的字符串。
         */
        private fun convertCamelCase(src: String): String {
            val strArr = src.toLowerCase().split("_")
            val sb = StringBuilder()
            for (i in strArr.indices) {
                if (i == 0) {
                    sb.append(strArr[i])
                } else {
                    sb.append(strArr[i].capitalize())
                }
            }
            return sb.toString()
        }

        /**
         * 用于处理字符串的编码转换。
         * @param str 待处理的字符串。
         * @param from 源字符集。
         * @param to 目标字符集。
         * @return 转码后的字符串。
         */
        private fun convertCharset(str: String, from: Charset, to: Charset) = String(str.toByteArray(from), to)

        /**
         * 用于将字符串转换为 UTF-8 编码。
         * @param str 待处理的字符串。
         * @return 转码后的字符串。
         */
        private fun convertToUTF8(str: String): String {
            return convertCharset(str, Charset.forName("ISO-8859-1"), Charset.forName("UTF-8"))
        }
    }
}