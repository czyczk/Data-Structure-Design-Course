package dataStructure

/**
 * 队列
 * 使用 LinkedList 存储数据
 * @author Zenas Chen (czyczk)
 */
class Queue<E> {
    private val list: LinkedList<E> = LinkedList()
    val length: Int
        get() = list.length
    val isEmpty: Boolean
        get() = list.isEmpty

    /**
     * 向队列尾部添加新元素。
     * @param item 要添加的元素
     */
    fun insert(item: E) {
        list.addLast(item)
    }

    /**
     * 返回队列头部的元素。
     * @return 队列头部的元素（空队列时为 null）
     */
    fun getFirst(): E? = list.getFirst()

    /**
     * 返回并移除队列头部的元素。
     * @return 队列头部的元素
     * @throws EmptyQueueException
     */
    fun pop(): E {
        try {
            return list.removeFirst()
        } catch (e: EmptyLinkedListException) {
            throw EmptyQueueException()
        }
    }

    /**
     * 清空队列（清空所有元素）。
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

    override fun toString(): String = list.toString()

    fun toList() = list.toList()
}