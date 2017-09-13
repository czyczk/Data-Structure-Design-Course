package ui.function

import manager.NoticeManager
import util.UiUtil

class ViewNoticePage {
    companion object {
        fun run() {
            println(UiUtil.getString("viewNotice"))
            val noticeList = NoticeManager.fetchAll()
            if (noticeList.isEmpty()) {
                UiUtil.printErrorMessage(UiUtil.getString("noNoticeAvailable"))
            } else {
                noticeList.forEachIndexed { i, s ->
                    println("\t${i + 1}. $s")
                }
            }

            println(UiUtil.getString("pressEnterToContinue"))
            readLine()
        }
    }
}