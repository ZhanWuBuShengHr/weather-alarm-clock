package com.weather.alarmclock.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

/**
 * 通知工具类
 * 负责创建和管理应用通知
 */
object NotificationUtils {
    
    private const val CHANNEL_WEATHER = "weather_alarm"
    private const val CHANNEL_ID = "weather_alarm_channel"
    private const val NOTIFICATION_ID = 1001
    
    /**
     * 创建通知渠道（Android 8.0+）
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            val channel = NotificationChannel(
                CHANNEL_ID,
                "天气闹钟",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "天气闹钟通知频道"
                enableVibration(true)
                setShowBadge(true)
            }
            
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    /**
     * 创建天气播报通知
     */
    fun createWeatherNotification(context: Context, title: String, content: String): Notification {
        createNotificationChannel(context)
        
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setAutoCancel(false)
            .build()
    }
    
    /**
     * 创建闹钟提醒通知
     */
    fun createAlarmReminderNotification(context: Context, alarmTime: String): Notification {
        createNotificationChannel(context)
        
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("天气闹钟")
            .setContentText("设置闹钟提醒 - $alarmTime")
            .setStyle(NotificationCompat.BigTextStyle().bigText("提醒您设置天气闹钟"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
    }
    
    /**
     * 创建后台服务通知
     */
    fun createServiceNotification(context: Context): Notification {
        createNotificationChannel(context)
        
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setContentTitle("天气闹钟")
            .setContentText("正在监听闹钟设置")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }
    
    /**
     * 显示通知
     */
    fun showNotification(context: Context, notificationId: Int, notification: Notification) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }
    
    /**
     * 取消通知
     */
    fun cancelNotification(context: Context, notificationId: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }
    
    /**
     * 取消所有通知
     */
    fun cancelAllNotifications(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }
}