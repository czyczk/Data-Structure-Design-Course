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

    override fun toString(): String = list.toString()
}