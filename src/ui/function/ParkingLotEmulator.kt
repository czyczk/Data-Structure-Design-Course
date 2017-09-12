package ui.function

import algorithm.RealTimeParkingLotEmulator
import model.parkingLot.ParkingLotEvent
import model.parkingLot.ParkingLotEventEnum
import util.UiUtil

class ParkingLotEmulatorPage {
    companion object {
//        private val optionList = mutableMapOf<Int, ParkingLotEmulatorOption>()
//        init {
//            val availableOptions = arrayOf(
//                    ParkingLotEmulatorOption.EXIT,
//                    ParkingLotEmulatorOption.READ_DATA_FROM_FILE,
//                    ParkingLotEmulatorOption.MANUALLY_INPUT_DATA
//            )
//            var ordinal = 0
//            availableOptions.forEach {
//                optionList[ordinal++] = it
//            }
//        }

        fun run() {
//            while (true) {
//                println(UiUtil.getString("parkingLotEmulator"))
//
//                // 显示可选模式
//                optionList.forEach { t, u ->
//                    println("\t$t. ${UiUtil.getString(u.name, true)}")
//                }
//
//                // 等待并检验用户的响应
//                println(UiUtil.getString("selectAnOption"))
//                var option = ParkingLotEmulatorOption.PLACEHOLDER
//                var pass: Boolean
//                do {
//                    try {
//                        pass = true
//                        val resp = readLine()!!.toInt()
//                        if (!optionList.containsKey(resp))
//                            error("")
//                        option = optionList[resp]!!
//                    } catch (e: Exception) {
//                        pass = false
//                        UiUtil.printErrorMessage(UiUtil.getString("invalidResponse"))
//                    }
//                } while (!pass)
//
//                // 调用相关方法
//                val isBreak = invoke(option)
//                if (isBreak)
//                    break
//            }
            println(UiUtil.getString("parkingLotEmulator"))
            manuallyInputData()

        }

//        private fun invoke(option: ParkingLotEmulatorOption): Boolean {
//            when (option) {
//                ParkingLotEmulatorOption.PLACEHOLDER ->
//                    throw IllegalStateException(UiUtil.getString("optionIsPlaceholder"))
//                ParkingLotEmulatorOption.EXIT -> return true
//                ParkingLotEmulatorOption.READ_DATA_FROM_FILE ->
//                    readDataFromFile()
//                ParkingLotEmulatorOption.MANUALLY_INPUT_DATA ->
//                    manuallyInputData()
//            }
//            return false
//        }

//        private fun readDataFromFile() {
//            TODO()
//        }

        private fun manuallyInputData() {
            println(UiUtil.getString("manuallyInputData"))

            // 输入停车场大小
            print(UiUtil.getString("enterSizeOfParkingLot"))
            println(UiUtil.getString("enterZeroToExit"))
            val size = UiUtil.enterInt()
            if (size == 0)
                return
            val emulator = RealTimeParkingLotEmulator(size)

            // 显示输入提示和样例记录格式
            println(UiUtil.getString("parkingLotEmulatorHint"))
            println(UiUtil.getString("enterRecord"))
            println(UiUtil.getString("parkingLotExampleInput"))

            while (true) {
                try {
                    // 提示当前模拟时刻
                    print(String.format(UiUtil.getString("emuTime"), emulator.time))

                    // 输入记录或命令并检验
                    val record = readLine()!!
                    if (record.trim().isEmpty()) {
                        break
                    } else if (record.equals("pl", true)) {
                        // pl 查看停车场
                        val parkingLot = emulator.parkingLotStatus
                        if (parkingLot.isEmpty())
                            UiUtil.printErrorMessage('\t' + UiUtil.getString("noVehicleInParkingLot"))
                        else {
                            println(UiUtil.getString("vehiclesInParkingLot"))
                            parkingLot.forEachIndexed { i, s ->
                                println("\t${i + 1}. $s")
                            }
                        }
                        continue
                    } else if (record.equals("events", true)) {
                        // events 查看当前所有信息
                        val events = emulator.eventTimetable
                        if (events.isEmpty())
                            UiUtil.printErrorMessage('\t' + UiUtil.getString("noEvent"))
                        else {
                            // 时间 车牌号 事件
                            UiUtil.printStringInFixedWidth(UiUtil.getString("ParkingLotEvent.time"), 8, false)
                            UiUtil.printStringInFixedWidth(UiUtil.getString("ParkingLotEvent.licenseNumber"), 16, false)
                            UiUtil.printStringInFixedWidth(UiUtil.getString("ParkingLotEvent.event"), 12)

                            events.forEach {
                                UiUtil.printStringInFixedWidth(it.time.toString(), 8, false)
                                UiUtil.printStringInFixedWidth(it.licenseNumber, 16, false)
                                UiUtil.printStringInFixedWidth(UiUtil.getString("${it.event.javaClass.simpleName}.${it.event.name}"), 12)
                            }
                        }
                        continue
                    }

                    // 解析该条记录
                    val strArr = record.split(" ")
                    if (strArr.count() != 2)
                        error(UiUtil.getString("incorrectRecordFormat"))

                    val license = strArr[0]
                    val event = strArr[1]
                    val eventEnum =  when (event) {
                        "A", "a" -> ParkingLotEventEnum.ARRIVE
                        "L", "l" -> ParkingLotEventEnum.LEAVE
                        else -> error(UiUtil.getString("incorrectRecordFormat"))
                    }

                    // 模拟并打印结果
                    val resultedEvents = emulator.emulate(ParkingLotEvent(license, eventEnum))
                    resultedEvents.forEach {
                        print("\t" + it.licenseNumber + " " + UiUtil.getString(it.event.javaClass.simpleName + "." + it.event.name))
                        if (it.event == ParkingLotEventEnum.LEAVE) {
                            print(UiUtil.getString("comma"))
                            val lastEnter = emulator.eventTimetable.last {
                                it.licenseNumber == license && it.event == ParkingLotEventEnum.ENTER
                            }
                            val duration = emulator.time - lastEnter.time
                            println(String.format(UiUtil.getString("durationOfStay"), duration)) // 输出停车时长
                        }
                        else
                            println(UiUtil.getString("period"))
                    }
                } catch (e: Exception) {
                    UiUtil.printErrorMessage("\t" + e.message)
                }
            }
        }
    }
}

//private enum class ParkingLotEmulatorOption {
//    PLACEHOLDER, READ_DATA_FROM_FILE, MANUALLY_INPUT_DATA, EXIT
//}