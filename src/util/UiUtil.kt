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
         */
        fun getString(key: String) = convertToUTF8(bundle.getString(key))

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