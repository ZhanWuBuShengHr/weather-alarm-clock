package com.weather.alarmclock.ui

/**
 * 闹钟状态枚举类
 */
sealed class AlarmStatus {
    /**
     * 闹钟已禁用
     */
    object Disabled : AlarmStatus()
    
    /**
     * 闹钟已启用
     * @param hour 小时
     * @param minute 分钟
     */
    data class Enabled(val hour: Int, val minute: Int) : AlarmStatus()
    
    /**
     * 闹钟正在响
     */
    object Ringing : AlarmStatus()
}