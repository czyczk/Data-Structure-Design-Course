package algorithm

import model.Spot
import java.text.Collator
import java.util.*

/**
 * 景点排序器
 * 使用快速排序算法按一定依据排序景点。
 */
class SpotSorter {
    companion object {
        private val supportedOrderBy = arrayOf(Spot.OrderBy.NAME, Spot.OrderBy.POPULARITY)
        private lateinit var list: MutableList<Spot>
        private val comparator = Collator.getInstance(Locale.CHINA)

        /**
         * 通过快速排序获得排序后的景点列表。
         * @param unsorted 排序前的景点列表
         * @param orderBy 排序依据
         * @param reverseOrder 是否倒序排列
         * @return 排序后的景点列表
         */
        fun getSortedList(unsorted: List<Spot>, orderBy: Spot.OrderBy, reverseOrder: Boolean = false): List<Spot> {
            if (unsorted.count() < 2)
                return unsorted

            if (!supportedOrderBy.contains(orderBy))
                throw NotImplementedError()

            list = unsorted.toMutableList()
            quickSort(orderBy)
            return if (reverseOrder)
                list.reversed()
            else
                list.toList()
        }

        private fun quickSort(orderBy: Spot.OrderBy, left: Int = 0, right: Int = list.count() - 1) {
            if (left > right)
                return

            val ref = list[left] // 基准
            var cursorI = left
            var cursorJ = right

            while (cursorI != cursorJ) {
                // 从右往左找比基准小者
                while (cursorI < cursorJ) {
                    if (orderBy == Spot.OrderBy.NAME && comparator.compare(list[cursorJ].name, ref.name) < 0)
                        break
                    else if (orderBy == Spot.OrderBy.POPULARITY && list[cursorJ].popularity < ref.popularity)
                        break
                    cursorJ--
                }

                // 从左往右找比基准大者
                while (cursorI < cursorJ) {
                    if (orderBy == Spot.OrderBy.NAME && comparator.compare(list[cursorJ].name, ref.name) > 0)
                        break
                    else if (orderBy == Spot.OrderBy.POPULARITY && list[cursorI].popularity > ref.popularity)
                        break
                    cursorI++
                }

                // 指针未重叠则交换两指针所指的对象
                if (cursorI != cursorJ) {
                    val temp = list[cursorI]
                    list[cursorI] = list[cursorJ]
                    list[cursorJ] = temp
                }
                // 指针重叠则交换指针与基准所指的对象
                else {
                    val temp = list[cursorI]
                    list[cursorI] = list[left]
                    list[left] = temp
                }
            }

            // 递归
            quickSort(orderBy, left, right - 1)
            quickSort(orderBy, left + 1, right)
        }
    }

}