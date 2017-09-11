package dataStructure

/**
 * 栈
 * 使用 LinkedList 存储数据。
 * @param size 栈的最大容量（0 为不限容量，默认为 0）。
 * @author Zenas Chen (czyczk)
 */
class Stack<E>(val size: Int = 0) {
    private val list: LinkedList<E> = LinkedList()
    val depth: Int
        get() = list.length
    val isEmpty: Boolean
        get() = list.isEmpty

    /**
     * 向栈添加新元素。
     * @param item 要添加的元素
     */
    fun push(item: E) {
        if (size != 0 && depth == size)
            throw FullStackException()
        list.addFirst(item)
    }

    /**
     * 返回栈顶的元素。
     * @return 栈顶的元素（空栈时为 null）
     */
    fun peek(): E? = list.getFirst()

    /**
     * 返回并移除栈顶的元素。
     * @return 栈顶的元素
     * @throws EmptyStackException
     */
    fun pop(): E {
        try {
            return list.removeFirst()
        } catch (e: EmptyLinkedListException) {
            throw EmptyStackException()
        }
    }

    /**
     * 清空栈（清空所有元素）。
     */
    fun clear() {
        list.clear()
    }

    /**
     * 检查是否包含元素。
     * @param item 检查的元素
     * @return 是否包含元素
     */
    fun contains(item: E): Boolean {
        return list.contains(item)
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.appendln("TOP (Depth = $depth)")
        if (!isEmpty)
            sb.appendln(list.toString().replaceBefore('\n', "").drop(1))
        return sb.dropLast(1).toString()
    }

    fun toList() = list.toList()
}