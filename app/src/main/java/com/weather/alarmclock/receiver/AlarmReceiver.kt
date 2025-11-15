package com.weather.alarmclock.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.weather.alarmclock.service.AlarmService

/**
 * 闹钟接收器
 * 负责接收闹钟触发事件并启动相应的服务
 */
class AlarmReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                // 开机自启动，恢复所有闹钟设置
                restoreAlarmsAfterBoot(context)
            }
            "com.weather.alarmclock.TRIGGER_ALARM" -> {
                // 触发闹钟
                triggerAlarm(context)
            }
            "com.weather.alarmclock.STOP_ALARM" -> {
                // 停止闹钟
                stopAlarm(context)
            }
        }
    }
    
    /**
     * 触发闹钟
     */
    private fun triggerAlarm(context: Context) {
        // 启动闹钟服务
        AlarmService.startAlarmService(context, AlarmService.ACTION_TRIGGER_ALARM)
    }
    
    /**
     * 停止闹钟
     */
    private fun stopAlarm(context: Context) {
        // 停止闹钟服务
        AlarmService.startAlarmService(context, AlarmService.ACTION_STOP_ALARM)
    }
    
    /**
     * 开机后恢复闹钟设置
     */
    private fun restoreAlarmsAfterBoot(context: Context) {
        // 从SharedPreferences中恢复闹钟设置
        val sharedPrefs = context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
        val isAlarmEnabled = sharedPrefs.getBoolean("daily_alarm_enabled", false)
        val alarmHour = sharedPrefs.getInt("alarm_hour", 7)
        val alarmMinute = sharedPrefs.getInt("alarm_minute", 0)
        
        if (isAlarmEnabled) {
            // 重新设置闹钟
            AlarmService.setDailyWeatherAlarm(context, alarmHour, alarmMinute)
        }
    }
}