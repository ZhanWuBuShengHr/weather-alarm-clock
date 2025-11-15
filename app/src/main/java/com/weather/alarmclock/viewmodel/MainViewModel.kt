package com.weather.alarmclock.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.weather.alarmclock.model.WeatherData
import com.weather.alarmclock.model.WeatherSpeakInfo
import com.weather.alarmclock.network.WeatherApiManager
import com.weather.alarmclock.service.TextToSpeechService
import com.weather.alarmclock.service.WeatherService
import com.weather.alarmclock.ui.AlarmStatus
import com.weather.alarmclock.utils.WeatherUtils
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

/**
 * 主界面ViewModel
 * 管理主界面的数据和业务逻辑
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val context = application.applicationContext
    private val weatherService = WeatherService.getInstance(context)
    private val ttsService = TextToSpeechService.getInstance(context)
    
    private val _currentWeather = MutableLiveData<WeatherData?>()
    val currentWeather: LiveData<WeatherData?> = _currentWeather
    
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _alarmStatus = MutableLiveData<AlarmStatus>()
    val alarmStatus: LiveData<AlarmStatus> = _alarmStatus
    
    private val _isSpeaking = MutableLiveData<Boolean>()
    val isSpeaking: LiveData<Boolean> = _isSpeaking
    
    init {
        loadCurrentWeather()
        loadAlarmStatus()
        ttsService.initialize()
    }
    
    /**
     * 加载当前天气
     */
    fun loadCurrentWeather() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val weatherData = weatherService.getCurrentWeather("101010100") // 默认北京
                _currentWeather.value = weatherData
            } catch (e: Exception) {
                _error.value = "获取天气信息失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 刷新天气数据
     */
    fun refreshWeather() {
        loadCurrentWeather()
    }
    
    /**
     * 测试语音播报
     */
    fun testSpeech() {
        viewModelScope.launch {
            val weatherData = _currentWeather.value
            if (weatherData != null) {
                val speakInfo = WeatherUtils.formatWeatherForSpeech(weatherData)
                val speakText = speakInfo.getSpeakText()
                
                _isSpeaking.value = true
                
                try {
                    ttsService.speakWeather(
                        speakText,
                        onStart = {
                            _isSpeaking.postValue(true)
                        },
                        onDone = {
                            _isSpeaking.postValue(false)
                        }
                    )
                } catch (e: Exception) {
                    _error.value = "语音播报失败: ${e.message}"
                    _isSpeaking.postValue(false)
                }
            } else {
                _error.value = "请先获取天气信息"
            }
        }
    }
    
    /**
     * 加载闹钟状态
     */
    fun loadAlarmStatus() {
        val sharedPrefs = context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
        val isAlarmEnabled = sharedPrefs.getBoolean("daily_alarm_enabled", false)
        val alarmHour = sharedPrefs.getInt("alarm_hour", 7)
        val alarmMinute = sharedPrefs.getInt("alarm_minute", 0)
        
        _alarmStatus.value = if (isAlarmEnabled) {
            AlarmStatus.Enabled(alarmHour, alarmMinute)
        } else {
            AlarmStatus.Disabled
        }
    }
    
    /**
     * 获取天气播报信息
     */
    fun getWeatherSpeakInfo(): WeatherSpeakInfo? {
        val weatherData = _currentWeather.value
        return weatherData?.let { WeatherUtils.formatWeatherForSpeech(it) }
    }
    
    /**
     * 检查网络连接
     */
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? android.net.ConnectivityManager
        val activeNetwork = connectivityManager?.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }
    
    override fun onCleared() {
        super.onCleared()
        // 清理资源
    }
}