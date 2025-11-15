package com.weather.alarmclock.model

import java.util.Locale

/**
 * 天气语音播报信息数据类
 */
data class WeatherSpeakInfo(
    val cityName: String = "",
    val temperature: Int = 0,
    val weatherDescription: String = "",
    val windInfo: String = "",
    val humidity: Int = 0,
    val date: String = ""
) {
    
    /**
     * 生成语音播报文本
     */
    fun getSpeakText(): String {
        val dateText = if (date.isNotEmpty()) date else "今天"
        
        return when {
            cityName.isNotEmpty() && temperature != 0 -> {
                String.format(
                    Locale.getDefault(),
                    "早上好！今天是%s，%s的天气是%s，温度%d度，%s。祝你有美好的一天！",
                    dateText,
                    cityName,
                    weatherDescription,
                    temperature,
                    windInfo
                )
            }
            temperature != 0 -> {
                String.format(
                    Locale.getDefault(),
                    "早上好！今天是%s，天气是%s，温度%d度，%s。祝你有美好的一天！",
                    dateText,
                    weatherDescription,
                    temperature,
                    windInfo
                )
            }
            else -> {
                "早上好！该起床了。今天天气不错，祝你有美好的一天！"
            }
        }
    }
    
    /**
     * 获取简化的播报文本（用于测试）
     */
    fun getShortSpeakText(): String {
        return if (temperature != 0) {
            "今天${weatherDescription}，温度${temperature}度"
        } else {
            "今天天气不错"
        }
    }
}