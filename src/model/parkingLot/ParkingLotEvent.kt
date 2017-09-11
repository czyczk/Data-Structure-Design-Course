package model.parkingLot

/**
 * 停车场事件
 * 停车场模拟器的输入数据和生成的模拟结果由此类组成。
 * @param time 事件发生的时刻（作为输入时被忽略）
 * @param licenseNumber 车牌号
 * @param event 事件
 */
data class ParkingLotEvent(val licenseNumber: String, val event: ParkingLotEventEnum, val time: Int = 0)

/**
 * 停车场事件枚举类
 * 可选事件包括：
 * ARRIVE（到达停车场，仅作为输入）、
 * WAIT（进入队列开始等待，仅作为结果）、
 * ENTER（进入停车场，仅作为结果）、
 * LEAVE（缴费并离开停车场）、
 * GIVE_WAY（为停车场中其他车辆让路，仅作为结果）
  */
enum class ParkingLotEventEnum {
    ARRIVE, WAIT, ENTER, LEAVE, GIVE_WAY
}