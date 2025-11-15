package com.weather.alarmclock.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock
import com.weather.alarmclock.model.WeatherSpeakInfo
import com.weather.alarmclock.receiver.AlarmReceiver
import com.weather.alarmclock.ui.AlarmRingActivity
import com.weather.alarmclock.utils.WeatherUtils
import kotlinx.coroutines.*

/**
 * 闹钟服务类
 * 负责管理闹钟逻辑和触发天气播报
 */
class AlarmService : Service() {
    
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var weatherService: WeatherService
    private lateinit var ttsService: TextToSpeechService
    
    override fun onCreate() {
        super.onCreate()
        weatherService = WeatherService.getInstance(this)
        ttsService = TextToSpeechService.getInstance(this)
        ttsService.initialize()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_TRIGGER_ALARM -> {
                triggerWeatherAlarm()
            }
            ACTION_STOP_ALARM -> {
                stopWeatherAlarm()
            }
        }
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    /**
     * 触发天气闹钟
     */
    private fun triggerWeatherAlarm() {
        serviceScope.launch {
            try {
                // 启动闹钟响铃界面
                val ringIntent = Intent(this@AlarmService, AlarmRingActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                startActivity(ringIntent)
                
                // 获取天气信息并播报
                val weatherInfo = getCurrentWeatherInfo()
                weatherInfo?.let { info ->
                    speakWeatherInfo(info)
                }
                
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    /**
     * 停止天气闹钟
     */
    private fun stopWeatherAlarm() {
        ttsService.stopSpeaking()
        
        // 停止前台服务
        stopForeground(true)
    }
    
    /**
     * 获取当前天气信息
     */
    private suspend fun getCurrentWeatherInfo(): WeatherSpeakInfo? {
        return try {
            // 使用定位或默认城市
            val location = "101010100" // 北京城市ID作为示例
            val weatherData = weatherService.getCurrentWeather(location)
            weatherData?.let { data ->
                WeatherUtils.formatWeatherForSpeech(data)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * 播报天气信息
     */
    private fun speakWeatherInfo(weatherInfo: WeatherSpeakInfo) {
        val speakText = weatherInfo.getSpeakText()
        
        ttsService.speakWeather(
            speakText,
            onStart = {
                // 开始播报，可以添加通知或振动提示
                startForeground(1, WeatherUtils.createNotification(this, "正在播报天气信息"))
            },
            onDone = {
                // 播报完成，可以添加延迟停止服务
                serviceScope.launch {
                    delay(2000) // 播报完成后等待2秒
                    stopSelf()
                }
            }
        )
    }
    
    /**
     * 设置每日闹钟
     */
    fun setDailyAlarm(context: Context, hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_TRIGGER_ALARM
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_DAILY_ALARM,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // 设置重复闹钟
        val triggerTime = getNextAlarmTime(hour, minute)
        
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            AlarmManager.INTERVAL_DAY, // 每天重复
            pendingIntent
        )
    }
    
    /**
     * 取消闹钟
     */
    fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_TRIGGER_ALARM
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_DAILY_ALARM,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager.cancel(pendingIntent)
    }
    
    /**
     * 获取下次闹钟触发时间
     */
    private fun getNextAlarmTime(hour: Int, minute: Int): Long {
        val calendar = java.util.Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            java.util.Calendar.HOUR_OF_DAY = hour
            java.util.Calendar.MINUTE = minute
            java.util.Calendar.SECOND = 0
            java.util.Calendar.MILLISECOND = 0
            
            // 如果时间已过，设置为明天
            if (timeInMillis <= System.currentTimeMillis()) {
                add(java.util.Calendar.DAY_OF_MONTH, 1)
            }
        }
        return calendar.timeInMillis
    }
    
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        ttsService.shutdown()
    }
    
    companion object {
        const val ACTION_TRIGGER_ALARM = "com.weather.alarmclock.TRIGGER_ALARM"
        const val ACTION_STOP_ALARM = "com.weather.alarmclock.STOP_ALARM"
        const val REQUEST_CODE_DAILY_ALARM = 1001
        
        /**
         * 启动闹钟服务
         */
        fun startAlarmService(context: Context, action: String) {
            val intent = Intent(context, AlarmService::class.java).apply {
                this.action = action
            }
            context.startService(intent)
        }
        
        /**
         * 设置每日天气闹钟
         */
        fun setDailyWeatherAlarm(context: Context, hour: Int, minute: Int) {
            val service = AlarmService()
            service.setDailyAlarm(context, hour, minute)
        }
    }
}