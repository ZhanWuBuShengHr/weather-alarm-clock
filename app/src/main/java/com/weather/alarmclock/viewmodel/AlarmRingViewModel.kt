package com.weather.alarmclock.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.weather.alarmclock.model.WeatherSpeakInfo
import com.weather.alarmclock.service.WeatherService

/**
 * 闹钟响铃界面的ViewModel
 * 负责管理闹钟响铃时的天气数据和状态
 */
class AlarmRingViewModel(application: Application) : AndroidViewModel(application) {
    
    private val weatherService = WeatherService.getInstance(application)
    
    private val _weatherData = MutableLiveData<WeatherSpeakInfo?>()
    val weatherData: LiveData<WeatherSpeakInfo?> = _weatherData
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    private var _currentWeatherInfo: WeatherSpeakInfo? = null
    
    /**
     * 加载天气信息
     */
    fun loadWeatherInfo() {
        _isLoading.value = true
        _error.value = null
        
        weatherService.getCurrentWeather { weatherData ->
            _isLoading.postValue(false)
            
            if (weatherData != null) {
                val weatherSpeakInfo = convertToSpeakInfo(weatherData)
                _currentWeatherInfo = weatherSpeakInfo
                _weatherData.postValue(weatherSpeakInfo)
            } else {
                _error.postValue("无法获取天气信息")
            }
        }
    }
    
    /**
     * 刷新天气信息
     */
    fun refreshWeatherInfo() {
        loadWeatherInfo()
    }
    
    /**
     * 获取当前天气信息
     */
    fun getCurrentWeatherInfo(): WeatherSpeakInfo? {
        return _currentWeatherInfo
    }
    
    /**
     * 将WeatherData转换为WeatherSpeakInfo
     */
    private fun convertToSpeakInfo(weatherData: com.weather.alarmclock.model.WeatherData): WeatherSpeakInfo {
        return WeatherSpeakInfo(
            cityName = weatherData.cityInfo?.name ?: "未知城市",
            temperature = weatherData.currentWeather?.temperature ?: 0,
            weatherDescription = weatherData.currentWeather?.text ?: "未知天气",
            windInfo = weatherData.currentWeather?.let {
                val windDir = it.windDir ?: "未知风向"
                val windScale = it.windScale ?: 0
                "风力$windDir ${windScale}级"
            } ?: "风力信息不可用",
            humidity = weatherData.currentWeather?.humidity ?: 0,
            date = formatDate()
        )
    }
    
    /**
     * 格式化日期
     */
    private fun formatDate(): String {
        val dateFormat = java.text.SimpleDateFormat("MM月dd日 EEEE", java.util.Locale.getDefault())
        return dateFormat.format(java.util.Date())
    }
    
    /**
     * 设置错误信息
     */
    fun setError(errorMessage: String) {
        _error.value = errorMessage
        _isLoading.value = false
    }
    
    /**
     * 清除错误信息
     */
    fun clearError() {
        _error.value = null
    }
    
    /**
     * 重置状态
     */
    fun resetState() {
        _isLoading.value = false
        _error.value = null
        _weatherData.value = null
        _currentWeatherInfo = null
    }
}