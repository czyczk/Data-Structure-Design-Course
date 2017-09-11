package util

import manager.RouteManager
import manager.SpotManager
import java.math.BigDecimal
import java.nio.charset.Charset
import java.util.*
import java.util.regex.Pattern

/**
 * UiUtil
 *
 * 此类提供系统中 UI 部分通用的方法。
 *
 * @author: Zenas Chen (czyczk)
 */
class UiUtil {
    companion object {

        /*
         * 字符串资源相关
         */

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





        /*
         * 屏幕显示相关
         */

        /**
         * 在登录界面和菜单界面显示标题。
         */
        fun showTitle() {
            println(UiUtil.getString("applicationTitleWithDecoration"))
        }

        /**
         * 打印错误消息并 sleep 10 毫秒。
         * @param message 被打印的消息
         * @param newLine 打印后是否换行
         */
        fun printErrorMessage(message: String?, newLine: Boolean = true) {
            if (message != null) {
                System.err.println(message)
                Thread.sleep(10)
            } else {
                println()
            }
        }

        /**
         * 以固定的宽度打印字符串。
         * @param str 被打印的字符串
         * @param width 固定的宽度
         * @param newLine 打印后是否换行
         */
        fun printStringInFixedWidth(str: String, width: Int, newLine: Boolean = true) {
            // 最小宽度为 str 字符数量 + 全角字符的个数
            var desiredWidth = str.length
            val pattern = Pattern.compile("[^\\x00-\\xff]*")
            val matcher = pattern.matcher(str)
            matcher.find()
            val numFullWidthChars = matcher.group().count()
            desiredWidth += numFullWidthChars

            if (width < desiredWidth)
                throw IllegalArgumentException("widthIsSmallerThanDesired")

            val result = String.format("%-${width - numFullWidthChars}s", str)
            if (newLine)
                println(result)
            else
                print(result)
        }

        /**
         * Get a string of a Double value in a user-friendly way.
         * @param number A number of Double
         * @return A string containing the value of the number in a user-friendly fashion
         */
        fun beautifyDouble(number: Double): String {
            return if (Util.checkIfRoughlyEqualsToInt(number))
                number.toInt().toString()
            else
                BigDecimal(number.toString()).toString()
        }

        /**
         * 检查是否有可用景点。无可用景点则打印错误消息。
         * @return 是否有可用景点
         */
        fun checkIfAnySpotIsAvailable(): Boolean {
            if (SpotManager.spotMap.count() == 0) {
                printErrorMessage(UiUtil.getString("noSpotAvailable"))
                println(UiUtil.getString("pressEnterToContinue"))
                readLine()
                return false
            }
            return true
        }

        /**
         * 检查是否有可用线路。无可用线路则打印错误消息。
         * @return 是否有可用线路
         */
        fun checkIfAnyRouteIsAvailable(): Boolean {
            if (RouteManager.routeMap.count() == 0) {
                printErrorMessage(UiUtil.getString("noRouteAvailable"))
                println(UiUtil.getString("pressEnterToContinue"))
                readLine()
                return false
            }
            return true
        }






        /*
         * 接收用户回应相关。
         */

        fun verifyAdministratorIdentity(): Boolean {
            val input = readLine()
            // TODO Verify the password.
            return input == "password"
        }

        /**
         * 将用户输入转换为 boolean 值。
         * @param resp 可转换为 true 的值有“Y”、“yes”、“true”、“是”；可转换为 false 的值有“N”、“no”、“false”、“否”。不区分大小写。
         * @return true 或 false
         */
        private fun parseInputToChoice(resp: String): Boolean {
            return if (resp.equals("Y", true) ||
                    resp.equals("yes", true) ||
                    resp.equals("true", true) ||
                    resp == "是")
                true
            else if (resp.equals("N", true) ||
                    resp.equals("no", true)||
                    resp.equals("false", true) ||
                    resp == "否")
                false
            else
                throw IllegalArgumentException(UiUtil.getString("notInvalidInputForBoolean"))
        }

        /**
         * 要求一个“是”或“否”的选择。不合法的输入将导致重试。
         * @return true 或 false
         */
        fun enterChoice(): Boolean {
            var pass: Boolean
            var choice = false
            do {
                pass = true
                try {
                    choice = UiUtil.parseInputToChoice(readLine()!!)
                } catch (e: IllegalArgumentException) {
                    printErrorMessage(UiUtil.getString("invalidResponse"))
                    pass = false
                }
            } while (!pass)
            return choice
        }

        /**
         * 要求一个 double 型。不合法的输入将导致重试。
         * @param isNegativeAllowed 允许负数
         * @return Double 型数值
         */
        fun enterDouble(isNegativeAllowed: Boolean = true): Double {
            var pass: Boolean
            var value = 0.0
            do {
                pass = true
                try {
                    value = readLine()!!.toDouble()
                    if (!isNegativeAllowed && value < 0)
                        throw IllegalArgumentException(UiUtil.getString("cannotBeNegative"))
                } catch (e: NumberFormatException) {
                    printErrorMessage(UiUtil.getString("invalidResponse"))
                    pass = false
                } catch (e: IllegalArgumentException) {
                    printErrorMessage(e.message)
                    pass = false
                }
            } while (!pass)
            return value
        }


    }
}