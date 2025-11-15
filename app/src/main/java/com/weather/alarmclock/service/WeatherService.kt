package com.weather.alarmclock.service

import android.content.Context
import com.weather.alarmclock.model.WeatherData
import com.weather.alarmclock.network.WeatherApiManager
import com.weather.alarmclock.network.WeatherApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 天气服务类
 * 负责获取和处理天气数据
 */
class WeatherService private constructor(
    private val context: Context
) {
    
    private val weatherApiService: WeatherApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(WeatherApiService.QWEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        retrofit.create(WeatherApiService::class.java)
    }
    
    companion object {
        @Volatile
        private var INSTANCE: WeatherService? = null
        
        fun getInstance(context: Context): WeatherService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: WeatherService(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    /**
     * 获取当前天气信息
     */
    suspend fun getCurrentWeather(location: String): WeatherData? {
        return withContext(Dispatchers.IO) {
            try {
                val apiKey = WeatherApiManager.getApiKey()
                val response = weatherApiService.getCurrentWeather(location, apiKey)
                
                if (response.isSuccessful) {
                    response.body()
                } else {
                    // 处理API错误
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
    
    /**
     * 根据城市名称获取城市ID
     */
    suspend fun getCityId(cityName: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                // 这里可以调用城市查询API
                // 暂时返回一些常见城市ID
                val cityIds = mapOf(
                    "北京" to "101010100",
                    "上海" to "101020100", 
                    "广州" to "101280101",
                    "深圳" to "101280601",
                    "杭州" to "101210101",
                    "南京" to "101190101",
                    "成都" to "101270101",
                    "重庆" to "101040100",
                    "武汉" to "101200101",
                    "西安" to "101110101"
                )
                cityIds[cityName]
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
    
    /**
     * 获取默认城市天气
     */
    suspend fun getDefaultWeather(): WeatherData? {
        return getCurrentWeather("101010100") // 北京作为默认城市
    }
    
    /**
     * 检查网络连接
     */
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? android.net.ConnectivityManager
        val activeNetwork = connectivityManager?.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }
}