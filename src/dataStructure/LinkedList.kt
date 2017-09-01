package dataStructure

/**
 * 链表（单向）
 * @author Zenas Chen (czyczk)
 */
class LinkedList<E> {
    data class Node<E>(var data: E) {
        var next: Node<E>? = null
    }

    private var first: Node<E>? = null
    private var last: Node<E>? = null
    var length: Int = 0
        private set
    val isEmpty: Boolean
        get() = (first == null)

    /**
     * 添加新元素至头部。
     * @param item 要添加的新元素
     */
    fun addFirst(item: E) {
        // 如果链表为空，则指定首、尾节点为新节点
        if (isEmpty) {
            first = Node(item)
            last = first
        }

        // 如果链表不为空，则保存原首节点，将新节点设为首节点，并后接原首节点。
        else {
            val temp = first
            first = Node(item)
            first!!.next = temp
        }

        // 长度 +1
        length++
    }

    /**
     * 清空链表。
     */
    fun clear() {
        length = 0
        first = null
        last = null
    }

    /**
     * 添加新元素至尾部。
     * @param item 要添加的新元素
     */
    fun addLast(item: E) {
        // 如果链表为空，则与添加至头部相同。
        if (isEmpty) addFirst(item)

        // 如果链表不为空，则将“现尾”连接新节点，并设新节点为尾节点，长度 +1
        else {
            val temp = Node(item)
            last!!.next = temp
            last = temp
            length++
        }
    }

    fun getFirst(): E? = first?.data
    fun getLast(): E? = last?.data

    /**
     * 移除头部的元素。
     * @return 头部的元素
     * @throws EmptyLinkedListException 链表为空，不能移除元素。
     */
    fun removeFirst(): E {
        // 如果链表为空，则抛出异常
        if (isEmpty) throw EmptyLinkedListException("链表为空。")

        // 如果链表不为空
        val data = first!!.data // 将头部数据取出（用于返回）
        first = first!!.next // 将下一节点设为首节点

        // 若移除后没有节点了，则将尾节点也设为 null
        if (first == null) last = null

        // 长度 -1
        length--
        return data
    }

    /**
     * 移除尾部的元素。
     * 注：将倒数第二个尾节点设为新尾节点时需要遍历链表，可能导致效率问题。
     * @return 尾部的元素
     * @throws EmptyLinkedListException
     */
    fun removeLast(): E {
        // 如果链表为空，则抛出异常
        if (isEmpty) throw EmptyLinkedListException("链表为空。")

        // 如果链表不为空，则取出数据
        val data = last!!.data

        // 若链表只有一个元素，将头尾节点设为 null
        if (length == 1) {
            first = null
            last = null
        }
        // 如果链表有至少 2 个元素
        else {
            // 找出倒数第二个节点
            var cursor = first
            while (cursor!!.next!!.next != null)
                cursor = cursor.next
            // 将其设为尾节点
            cursor.next = null
            last = cursor
        }

        // 长度 -1
        length--
        return data
    }

    /**
     * 寻找链表中第一个出现的该元素，并移除它。
     * @param item 要删除的元素
     * @return 是否成功找到并移除该元素
     */
    fun removeFirstOccurrence(item: E): Boolean {
        if (isEmpty) return false
        else if (length == 1) {
            if (first!!.data == item) {
                clear() // 找到该项，移除即清空链表
                return true
            } else
                return false
        }
        else {
            // 当链表多于两项时，要记录两个指针位置：当前与上一个
            var curCursor = first
            var lastCursor: Node<E>? = null
            while (curCursor != null) {
                if (curCursor.data == item) {
                    // 该元素被找到。
                    // 若上一个指针是 null，则它在第一个，调用 removeFirst()。否则将上一指针 next 跳过一个节点。
                    if (lastCursor == null) removeFirst()
                    else {
                        lastCursor.next = curCursor.next
                        length--
                    }
                    return true
                }

                // 当前元素不是目标元素，则两个指针分别移往下一步
                lastCursor = curCursor
                curCursor = curCursor.next
            }
            return false
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.appendln("Length = $length")

        var cursor = first
        while (cursor != null) {
            sb.appendln(cursor.data)
            cursor = cursor.next
        }

        return sb.dropLast(1).toString()
    }


}