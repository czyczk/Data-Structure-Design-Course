package manager

class NoticeManager {
    companion object {
        private val noticeList = mutableListOf<String>()

        fun add(notice: String) {
            noticeList.add(notice)
        }

        fun fetchAll(): Array<String> {
            return noticeList.toTypedArray()
        }
    }
}