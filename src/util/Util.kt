package util

import com.google.gson.reflect.TypeToken

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
    }
}