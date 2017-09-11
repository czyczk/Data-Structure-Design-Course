package algorithm

import dataStructure.FullStackException
import dataStructure.Queue
import dataStructure.Stack
import model.parkingLot.ParkingLotEvent
import model.parkingLot.ParkingLotEventEnum
import util.UiUtil

class RealTimeParkingLotEmulator(@Suppress("CanBeParameter") val sizeOfParkingLot: Int) {
    // 当前时刻
    var time = 1
        get
        private set
    // 栈式停车场
    private val parkingLot = Stack<String>(sizeOfParkingLot)
    // 停车场外排队队列
    private val queue = Queue<String>()
    // 临时让路区
    private val bufferArea = Stack<String>()
    // 内部使用的停车场事件时刻表（可变列表）
    private val eventList = mutableListOf<ParkingLotEvent>()

    // 对外的停车场事件时刻表
    val eventTimetable
        get() = eventList.toTypedArray()
    //对外的停车场内容
    val parkingLotStatus
        get() = parkingLot.toList().reversed()

    /**
     * 初始化停车场，清空停车场和事件记录。
     */
    fun initialize() {
        time = 0
        parkingLot.clear()
        queue.clear()
        bufferArea.clear()
        eventList.clear()
    }

    fun emulate(record: ParkingLotEvent): Array<ParkingLotEvent> {
        val license = record.licenseNumber
        val resultedEventList = mutableListOf<ParkingLotEvent>()

        when (record.event) {
            // 事件为到达
            ParkingLotEventEnum.ARRIVE -> {
                // 检查此车是否已在停车场或队列内
                if (parkingLot.contains(license) || queue.contains(license))
                    throw IllegalArgumentException(UiUtil.getString("theCarIsAlreadyInTheParkingLotOrTheQueue"))
                // 尝试停到停车场中
                try {
                    parkingLot.push(license)
                    resultedEventList.add(ParkingLotEvent(license, ParkingLotEventEnum.ENTER, time++))
                } catch (e: FullStackException) {
                    // 若停车场已满则加入队列
                    queue.insert(license)
                    resultedEventList.add(ParkingLotEvent(license, ParkingLotEventEnum.WAIT, time++))
                }
            }

            // 事件为离开
            ParkingLotEventEnum.LEAVE -> {
                // 检查此车是否在停车场内
                if (!parkingLot.contains(license))
                    throw IllegalArgumentException(UiUtil.getString("theCarIsNotInTheParkingLot"))
                // 从外侧出车，直到为该车为止
                while (true) {
                    val curLicense = parkingLot.pop()
                    if (curLicense != license) {
                        bufferArea.push(curLicense) // 将出车放入缓冲过道，待稍后按原序重进停车场
                        resultedEventList.add(ParkingLotEvent(curLicense, ParkingLotEventEnum.GIVE_WAY, time))
                    } else {
                        resultedEventList.add(ParkingLotEvent(license, ParkingLotEventEnum.LEAVE, time))
                        break
                    }
                }
                // 让路的车停回去
                while (!bufferArea.isEmpty) {
                    parkingLot.push(bufferArea.pop())
                }
                // 是否有新车可以进场
                if (!queue.isEmpty) {
                    val newLicense = queue.pop()
                    parkingLot.push(newLicense)
                    resultedEventList.add(ParkingLotEvent(newLicense, ParkingLotEventEnum.ENTER, time))
                }

                time++
            }
            // 事件只能是到达或离开
            else -> throw IllegalArgumentException(UiUtil.getString("illegalParkingLotEvent"))
        }

        eventList.addAll(resultedEventList)
        return resultedEventList.toTypedArray()
    }
}