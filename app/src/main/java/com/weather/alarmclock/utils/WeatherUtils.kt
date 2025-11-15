package com.weather.alarmclock.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.weather.alarmclock.R
import com.weather.alarmclock.model.WeatherData
import com.weather.alarmclock.model.WeatherSpeakInfo

/**
 * 天气工具类
 * 提供天气相关的工具方法
 */
object WeatherUtils {
    
    /**
     * 格式化天气数据为语音播报信息
     */
    fun formatWeatherForSpeech(weatherData: WeatherData): WeatherSpeakInfo {
        val currentWeather = weatherData.now
        val cityInfo = weatherData.city
        
        return WeatherSpeakInfo(
            cityName = cityInfo.name,
            temperature = currentWeather.temp,
            weatherDescription = currentWeather.text,
            humidity = currentWeather.humidity,
            windInfo = "${currentWeather.windDir}，${currentWeather.windScale}级",
            feelsLike = currentWeather.feelsLike
        )
    }
    
    /**
     * 获取天气图标资源ID
     */
    fun getWeatherIconResId(iconCode: String): Int {
        return try {
            val iconName = when (iconCode) {
                "100" -> "sunny"          // 晴
                "101" -> "cloudy"         // 多云
                "102" -> "partly_cloudy"  // 少云
                "103" -> "partly_cloudy"  // 晴间多云
                "104" -> "cloudy"         // 阴
                "150" -> "sunny"          // 晴（夜间）
                "151" -> "cloudy"         // 多云（夜间）
                "152" -> "partly_cloudy"  // 少云（夜间）
                "153" -> "partly_cloudy"  // 晴间多云（夜间）
                "300" -> "rainy"          // 阵雨
                "301" -> "rainy"          // 强阵雨
                "302" -> "thunderstorm"   // 雷阵雨
                "303" -> "thunderstorm"   // 强雷阵雨
                "304" -> "thunderstorm"   // 雷阵雨伴有冰雹
                "305" -> "rainy"          // 小雨
                "306" -> "rainy"          // 中雨
                "307" -> "rainy"          // 大雨
                "308" -> "rainy"          // 极大雨
                "309" -> "rainy"          // 毛毛雨
                "310" -> "rainy"          // 暴雨
                "311" -> "rainy"          // 大暴雨
                "312" -> "rainy"          // 特大暴雨
                "313" -> "rainy"          // 冻雨
                "400" -> "snowy"          // 小雪
                "401" -> "snowy"          // 中雪
                "402" -> "snowy"          // 大雪
                "403" -> "snowy"          // 暴雪
                "404" -> "snowy"          // 雨夹雪
                "405" -> "snowy"          // 雨雪天气
                "406" -> "snowy"          // 阵雨夹雪
                "407" -> "snowy"          // 阵雪
                "500" -> "foggy"          // 薄雾
                "501" -> "foggy"          // 雾
                "502" -> "foggy"          // 霾
                "503" -> "foggy"          // 扬沙
                "504" -> "foggy"          // 浮尘
                "505" -> "foggy"          // 沙尘暴
                "506" -> "foggy"          // 强沙尘暴
                "507" -> "foggy"          // 飑
                "508" -> "foggy"          // 飑
                "509" -> "foggy"          // 雾
                "510" -> "foggy"          // 霾
                "511" -> "foggy"          // 扬沙
                "512" -> "foggy"          // 浮尘
                "513" -> "foggy"          // 沙尘暴
                "514" -> "foggy"          // 强沙尘暴
                "515" -> "foggy"          // 飑
                else -> "unknown"
            }
            
            // 这里应该返回实际的资源ID，暂时返回默认图标
            R.drawable.ic_weather_unknown
        } catch (e: Exception) {
            R.drawable.ic_weather_unknown
        }
    }
    
    /**
     * 创建天气通知
     */
    fun createNotification(context: Context, content: String): Notification {
        val channelId = "weather_alarm_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // 创建通知渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "天气闹钟",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "天气闹钟播报通知"
                setSound(null, null) // 不播放声音
            }
            notificationManager.createNotificationChannel(channel)
        }
        
        return NotificationCompat.Builder(context, channelId)
            .setContentTitle("天气闹钟")
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_weather_alarm)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            .setAutoCancel(false)
            .build()
    }
    
    /**
     * 获取城市列表（示例数据）
     */
    fun getCityList(): List<Pair<String, String>> {
        return listOf(
            "北京" to "101010100",
            "上海" to "101020100",
            "广州" to "101280101",
            "深圳" to "101280601",
            "杭州" to "101210101",
            "南京" to "101190101",
            "成都" to "101270101",
            "重庆" to "101040100",
            "武汉" to "101200101",
            "西安" to "101110101",
            "天津" to "101030100",
            "苏州" to "101190401",
            "郑州" to "101180101",
            "长沙" to "101250101",
            "沈阳" to "101070101",
            "大连" to "101070201",
            "青岛" to "101120201",
            "济南" to "101120101",
            "合肥" to "101220101",
            "福州" to "101230101"
        )
    }
    
    /**
     * 根据城市名称获取城市ID
     */
    fun getCityIdByName(cityName: String): String? {
        return getCityList().firstOrNull { it.first == cityName }?.second
    }
    
    /**
     * 根据城市ID获取城市名称
     */
    fun getCityNameById(cityId: String): String? {
        return getCityList().firstOrNull { it.second == cityId }?.first
    }
}