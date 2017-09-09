package util

import com.google.gson.reflect.TypeToken
import java.math.BigDecimal

/**
 * Util
 *
 * 此类提供系统中通用的方法，方便各类调用。
 */
class Util {
    companion object {
        /**
         * 根据泛型参数生成相应 TypeToken。用于解决 Gson 库中 fromJson 方法遇到的泛型擦除问题。
         * @param T 目标类
         */
        inline fun <reified T> genericType() = object: TypeToken<T>() {}.type!!

        /**
         * 检验一个 Double 值是否非常接近于最近的 Int 值。用于屏显。
         * 例 1：
         *     233.999999999 != 234
         *     233.99999999999999 == 234
         *     234.00000000000001 == 234
         *     234.0 == 234
         * @param double 待检验的 Double 型数值
         * @return 是否非常接近于最近的 Int 值
         */
        fun checkIfRoughlyEqualsToInt(double: Double): Boolean {
            val fromDouble = BigDecimal(double.toString())
            val fromInt = BigDecimal(double.toInt().toString() + ".0")
            return fromDouble == fromInt
        }
    }
}