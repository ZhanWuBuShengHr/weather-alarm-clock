package com.weather.alarmclock.model

import com.google.gson.annotations.SerializedName

/**
 * 天气数据模型类
 * 用于解析天气API返回的JSON数据
 */
data class WeatherData(
    @SerializedName("code")
    val code: String, // 状态码
    
    @SerializedName("updateTime")
    val updateTime: String, // 更新时间
    
    @SerializedName("fxLink")
    val fxLink: String, // 天气链接
    
    @SerializedName("now")
    val now: CurrentWeather, // 当前天气
    
    @SerializedName("city")
    val city: CityInfo // 城市信息
)

data class CurrentWeather(
    @SerializedName("obsTime")
    val obsTime: String, // 观测时间
    
    @SerializedName("temp")
    val temp: String, // 温度
    
    @SerializedName("feelsLike")
    val feelsLike: String, // 体感温度
    
    @SerializedName("icon")
    val icon: String, // 天气图标
    
    @SerializedName("text")
    val text: String, // 天气文字描述
    
    @SerializedName("wind360")
    val wind360: String, // 风向360度角
    
    @SerializedName("windDir")
    val windDir: String, // 风向
    
    @SerializedName("windScale")
    val windScale: String, // 风力等级
    
    @SerializedName("windSpeed")
    val windSpeed: String, // 风速
    
    @SerializedName("humidity")
    val humidity: String, // 湿度
    
    @SerializedName("precip")
    val precip: String, // 降水量
    
    @SerializedName("pressure")
    val pressure: String, // 气压
    
    @SerializedName("vis")
    val vis: String, // 能见度
    
    @SerializedName("cloud")
    val cloud: String, // 云量
    
    @SerializedName("dew")
    val dew: String // 露点温度
)

data class CityInfo(
    @SerializedName("id")
    val id: String, // 城市ID
    
    @SerializedName("name")
    val name: String, // 城市名称
    
    @SerializedName("lat")
    val lat: String, // 纬度
    
    @SerializedName("lon")
    val lon: String, // 经度
    
    @SerializedName("adm2")
    val adm2: String, // 所属地级市
    
    @SerializedName("adm1")
    val adm1: String, // 所属省份
    
    @SerializedName("country")
    val country: String, // 所属国家
    
    @SerializedName("tz")
    val tz: String, // 时区
    
    @SerializedName("utcOffset")
    val utcOffset: String // UTC偏移
)

/**
 * 天气播报信息类
 * 用于格式化语音播报的天气内容
 */
data class WeatherSpeakInfo(
    val cityName: String,
    val temperature: String,
    val weatherDescription: String,
    val humidity: String,
    val windInfo: String,
    val feelsLike: String
) {
    fun getSpeakText(): String {
        return "今天是${cityName}，${weatherDescription}，气温${temperature}度，体感温度${feelsLike}度，湿度${humidity}%，${windInfo}。祝你一天愉快！"
    }
}