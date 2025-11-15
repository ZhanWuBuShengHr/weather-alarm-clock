package com.weather.alarmclock.network

import com.weather.alarmclock.model.WeatherData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 天气API服务接口
 * 使用和风天气API或心知天气API
 */
interface WeatherApiService {
    
    @GET("v7/weather/now")
    suspend fun getCurrentWeather(
        @Query("location") location: String, // 城市ID或经纬度
        @Query("key") apiKey: String // API密钥
    ): Response<WeatherData>
    
    companion object {
        // 使用心知天气免费API (建议使用和风天气，需要注册获取API Key)
        const val BASE_URL = "https://devapi.qweather.com/"
        const val DEFAULT_API_KEY = "your-api-key-here" // 请替换为实际的API Key
        
        // 和风天气API地址
        const val QWEATHER_BASE_URL = "https://devapi.qweather.com/"
        const val QWEATHER_API_KEY = "your-qweather-api-key-here" // 请替换为实际的和风天气API Key
    }
}

/**
 * 天气API管理器
 * 统一管理天气数据获取
 */
object WeatherApiManager {
    private var apiKey = WeatherApiService.DEFAULT_API_KEY
    private var currentProvider = "default" // 默认使用心知天气
    
    fun setApiKey(key: String, provider: String = "default") {
        apiKey = key
        currentProvider = provider
    }
    
    fun getApiKey(): String = apiKey
    
    fun getCurrentProvider(): String = currentProvider
}