package dataStructure

/**
 * 栈
 * 使用 LinkedList 存储数据。
 * @author Zenas Chen (czyczk)
 */
class Stack<E> {
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

    override fun toString(): String {
        val sb = StringBuilder()
        sb.appendln("TOP (Depth = $depth)")
        if (!isEmpty)
            sb.appendln(list.toString().replaceBefore('\n', "").drop(1))
        return sb.dropLast(1).toString()
    }
}