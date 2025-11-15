package com.weather.alarmclock.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.weather.alarmclock.service.AlarmService
import com.weather.alarmclock.service.TextToSpeechService
import com.weather.alarmclock.service.WeatherService
import com.weather.alarmclock.utils.WeatherUtils
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

/**
 * 闹钟设置ViewModel
 * 管理闹钟设置的逻辑
 */
class AlarmSettingViewModel(application: Application) : AndroidViewModel(application) {
    
    private val context = application.applicationContext
    private val sharedPrefs: SharedPreferences = context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
    private val weatherService = WeatherService.getInstance(context)
    private val ttsService = TextToSpeechService.getInstance(context)
    
    private val _currentHour = MutableLiveData<Int>()
    val currentHour: LiveData<Int> = _currentHour
    
    private val _currentMinute = MutableLiveData<Int>()
    val currentMinute: LiveData<Int> = _currentMinute
    
    private val _isAlarmEnabled = MutableLiveData<Boolean>()
    val isAlarmEnabled: LiveData<Boolean> = _isAlarmEnabled
    
    private val _selectedCity = MutableLiveData<String>()
    val selectedCity: LiveData<String> = _selectedCity
    
    private val _saveResult = MutableLiveData<Boolean>()
    val saveResult: LiveData<Boolean> = _saveResult
    
    private val _testResult = MutableLiveData<String>()
    val testResult: LiveData<String> = _testResult
    
    init {
        loadCurrentSettings()
    }
    
    /**
     * 加载当前设置
     */
    fun loadCurrentSettings() {
        _currentHour.value = sharedPrefs.getInt("alarm_hour", 7)
        _currentMinute.value = sharedPrefs.getInt("alarm_minute", 0)
        _isAlarmEnabled.value = sharedPrefs.getBoolean("daily_alarm_enabled", false)
        _selectedCity.value = sharedPrefs.getString("selected_city", "北京") ?: "北京"
    }
    
    /**
     * 设置时间
     */
    fun setTime(hour: Int, minute: Int) {
        _currentHour.value = hour
        _currentMinute.value = minute
        
        // 保存到SharedPreferences
        sharedPrefs.edit()
            .putInt("alarm_hour", hour)
            .putInt("alarm_minute", minute)
            .apply()
    }
    
    /**
     * 设置城市
     */
    fun setCity(cityName: String) {
        _selectedCity.value = cityName
        
        // 保存到SharedPreferences
        sharedPrefs.edit()
            .putString("selected_city", cityName)
            .apply()
    }
    
    /**
     * 保存闹钟设置
     */
    fun saveAlarmSettings(hour: Int, minute: Int, enabled: Boolean, city: String) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    // 保存到SharedPreferences
                    sharedPrefs.edit()
                        .putBoolean("daily_alarm_enabled", enabled)
                        .putInt("alarm_hour", hour)
                        .putInt("alarm_minute", minute)
                        .putString("selected_city", city)
                        .apply()
                    
                    if (enabled) {
                        // 设置闹钟
                        AlarmService.setDailyWeatherAlarm(context, hour, minute)
                    } else {
                        // 取消闹钟
                        AlarmService().cancelAlarm(context)
                    }
                }
                
                _saveResult.value = true
            } catch (e: Exception) {
                _saveResult.value = false
            }
        }
    }
    
    /**
     * 禁用闹钟
     */
    fun disableAlarm() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    // 保存设置
                    sharedPrefs.edit()
                        .putBoolean("daily_alarm_enabled", false)
                        .apply()
                    
                    // 取消闹钟
                    AlarmService().cancelAlarm(context)
                }
                
                _saveResult.value = true
            } catch (e: Exception) {
                _saveResult.value = false
            }
        }
    }
    
    /**
     * 测试闹钟
     */
    fun testAlarm(hour: Int, minute: Int, city: String) {
        viewModelScope.launch {
            _testResult.value = "正在准备测试..."
            
            try {
                // 获取天气数据
                val cityId = WeatherUtils.getCityIdByName(city) ?: "101010100"
                val weatherData = weatherService.getCurrentWeather(cityId)
                
                if (weatherData != null) {
                    val speakInfo = WeatherUtils.formatWeatherForSpeech(weatherData)
                    val speakText = speakInfo.getSpeakText()
                    
                    // 初始化语音服务
                    ttsService.initialize()
                    
                    // 开始播报测试
                    ttsService.speakWeather(
                        "测试闹钟：${speakText}",
                        onStart = {
                            _testResult.postValue("正在播报天气信息...")
                        },
                        onDone = {
                            _testResult.postValue("测试完成！闹钟功能正常")
                        }
                    )
                } else {
                    // 没有天气数据，播报默认内容
                    ttsService.speakWeather(
                        "测试闹钟：早上好！该起床了。今天天气不错，祝你有美好的一天！",
                        onStart = {
                            _testResult.postValue("正在播报测试内容...")
                        },
                        onDone = {
                            _testResult.postValue("测试完成！语音功能正常")
                        }
                    )
                }
            } catch (e: Exception) {
                _testResult.value = "测试失败: ${e.message}"
            }
        }
    }
    
    /**
     * 获取闹钟状态
     */
    fun getAlarmStatus(): String {
        return if (_isAlarmEnabled.value == true) {
            "已启用 ${_currentHour.value.toString().padStart(2, '0')}:${_currentMinute.value.toString().padStart(2, '0')}"
        } else {
            "已禁用"
        }
    }
}