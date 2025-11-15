package com.weather.alarmclock.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.weather.alarmclock.R
import com.weather.alarmclock.service.TextToSpeechService
import com.weather.alarmclock.service.WeatherService
import com.weather.alarmclock.viewmodel.AlarmRingViewModel

/**
 * 闹钟响铃Activity
 * 显示闹钟界面并播报天气信息
 */
class AlarmRingActivity : AppCompatActivity() {
    
    private lateinit var viewModel: AlarmRingViewModel
    private lateinit var ttsService: TextToSpeechService
    private lateinit var weatherService: WeatherService
    
    // UI组件
    private lateinit var tvAlarmTime: MaterialTextView
    private lateinit var tvWeatherTitle: MaterialTextView
    private lateinit var tvWeatherInfo: MaterialTextView
    private lateinit var tvCityName: MaterialTextView
    private lateinit var tvTemperature: MaterialTextView
    private lateinit var tvWeatherDescription: MaterialTextView
    private lateinit var tvWindInfo: MaterialTextView
    private lateinit var tvHumidity: MaterialTextView
    private lateinit var btnStopAlarm: MaterialButton
    private lateinit var btnSnooze: MaterialButton
    private lateinit var cardWeatherInfo: MaterialCardView
    private lateinit var progressLoading: View
    
    private var isSpeaking = false
    private var snoozeHandler: Handler? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 设置全屏显示
        setupFullScreen()
        
        setContentView(R.layout.activity_alarm_ring)
        
        initViews()
        setupViewModel()
        setupClickListeners()
        startAlarmSequence()
    }
    
    /**
     * 设置全屏显示
     */
    private fun setupFullScreen() {
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
            
            // 隐藏状态栏和导航栏
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                ViewCompat.getWindowInsetsController(window.decorView)?.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            } else {
                decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            }
        }
    }
    
    /**
     * 初始化视图组件
     */
    private fun initViews() {
        tvAlarmTime = findViewById(R.id.tv_alarm_time)
        tvWeatherTitle = findViewById(R.id.tv_weather_title)
        tvWeatherInfo = findViewById(R.id.tv_weather_info)
        tvCityName = findViewById(R.id.tv_city_name)
        tvTemperature = findViewById(R.id.tv_temperature)
        tvWeatherDescription = findViewById(R.id.tv_weather_description)
        tvWindInfo = findViewById(R.id.tv_wind_info)
        tvHumidity = findViewById(R.id.tv_humidity)
        btnStopAlarm = findViewById(R.id.btn_stop_alarm)
        btnSnooze = findViewById(R.id.btn_snooze)
        cardWeatherInfo = findViewById(R.id.card_weather_info)
        progressLoading = findViewById(R.id.progress_loading)
        
        // 显示当前时间
        updateCurrentTime()
    }
    
    /**
     * 设置ViewModel
     */
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[AlarmRingViewModel::class.java]
        ttsService = TextToSpeechService.getInstance(this)
        weatherService = WeatherService.getInstance(this)
        
        // 观察天气数据
        viewModel.weatherData.observe(this) { weatherInfo ->
            updateWeatherDisplay(weatherInfo)
        }
        
        // 观察错误信息
        viewModel.error.observe(this) { error ->
            handleWeatherError(error)
        }
        
        // 观察加载状态
        viewModel.isLoading.observe(this) { isLoading ->
            updateLoadingState(isLoading)
        }
    }
    
    /**
     * 设置点击监听器
     */
    private fun setupClickListeners() {
        btnStopAlarm.setOnClickListener {
            stopAlarm()
        }
        
        btnSnooze.setOnClickListener {
            snoozeAlarm()
        }
        
        // 点击天气卡片刷新天气信息
        cardWeatherInfo.setOnClickListener {
            refreshWeatherInfo()
        }
    }
    
    /**
     * 开始闹钟流程
     */
    private fun startAlarmSequence() {
        // 初始化语音服务
        ttsService.initialize()
        
        // 获取天气信息
        loadWeatherInfo()
        
        // 延迟开始播报，给天气数据加载一些时间
        Handler(Looper.getMainLooper()).postDelayed({
            startWeatherSpeech()
        }, 2000)
    }
    
    /**
     * 加载天气信息
     */
    private fun loadWeatherInfo() {
        viewModel.loadWeatherInfo()
    }
    
    /**
     * 刷新天气信息
     */
    private fun refreshWeatherInfo() {
        viewModel.refreshWeatherInfo()
    }
    
    /**
     * 开始天气语音播报
     */
    private fun startWeatherSpeech() {
        val weatherInfo = viewModel.getCurrentWeatherInfo()
        if (weatherInfo != null) {
            val speakText = generateSpeechText(weatherInfo)
            
            ttsService.speakWeather(
                speakText,
                onStart = {
                    isSpeaking = true
                    // 可以添加振动或声音效果
                },
                onDone = {
                    isSpeaking = false
                    // 播报完成后，可以自动停止闹钟或等待用户手动停止
                }
            )
        } else {
            // 如果没有天气数据，播报默认文本
            val defaultText = "早上好！该起床了。今天天气不错，祝你有美好的一天！"
            ttsService.speakWeather(defaultText)
        }
    }
    
    /**
     * 生成语音播报文本
     */
    private fun generateSpeechText(weatherInfo: com.weather.alarmclock.model.WeatherSpeakInfo): String {
        return weatherInfo.getSpeakText()
    }
    
    /**
     * 停止闹钟
     */
    private fun stopAlarm() {
        // 停止语音播报
        ttsService.stopSpeaking()
        
        // 取消贪睡
        cancelSnooze()
        
        // 发送广播停止闹钟服务
        val intent = Intent("com.weather.alarmclock.STOP_ALARM")
        sendBroadcast(intent)
        
        // 结束Activity
        finish()
    }
    
    /**
     * 贪睡功能
     */
    private fun snoozeAlarm() {
        // 停止当前播报
        ttsService.stopSpeaking()
        
        // 设置5分钟后的贪睡
        snoozeHandler = Handler(Looper.getMainLooper())
        snoozeHandler?.postDelayed({
            startWeatherSpeech()
        }, 5 * 60 * 1000) // 5分钟
        
        // 显示贪睡提示
        btnSnooze.text = "5分钟后再次提醒"
        btnSnooze.isEnabled = false
        
        // 1分钟后重新启用贪睡按钮
        Handler(Looper.getMainLooper()).postDelayed({
            btnSnooze.text = "贪睡5分钟"
            btnSnooze.isEnabled = true
        }, 60000)
    }
    
    /**
     * 取消贪睡
     */
    private fun cancelSnooze() {
        snoozeHandler?.removeCallbacksAndMessages(null)
        snoozeHandler = null
    }
    
    /**
     * 更新天气显示
     */
    private fun updateWeatherDisplay(weatherInfo: com.weather.alarmclock.model.WeatherSpeakInfo?) {
        weatherInfo?.let { info ->
            tvWeatherInfo.text = "今日天气"
            tvCityName.text = info.cityName
            tvTemperature.text = "${info.temperature}°"
            tvWeatherDescription.text = info.weatherDescription
            tvWindInfo.text = info.windInfo
            tvHumidity.text = "湿度 ${info.humidity}%"
            
            // 隐藏加载指示器
            progressLoading.visibility = View.GONE
            cardWeatherInfo.visibility = View.VISIBLE
        }
    }
    
    /**
     * 处理天气错误
     */
    private fun handleWeatherError(error: String) {
        // 显示默认天气信息
        tvWeatherInfo.text = "天气信息"
        tvCityName.text = "北京"
        tvTemperature.text = "25°"
        tvWeatherDescription.text = "多云"
        tvWindInfo.text = "东南风 2级"
        tvHumidity.text = "湿度 60%"
        
        progressLoading.visibility = View.GONE
        cardWeatherInfo.visibility = View.VISIBLE
    }
    
    /**
     * 更新加载状态
     */
    private fun updateLoadingState(isLoading: Boolean) {
        if (isLoading) {
            progressLoading.visibility = View.VISIBLE
            cardWeatherInfo.visibility = View.GONE
        }
    }
    
    /**
     * 更新当前时间显示
     */
    private fun updateCurrentTime() {
        val currentTime = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            .format(java.util.Date())
        tvAlarmTime.text = currentTime
        
        // 每秒更新时间
        Handler(Looper.getMainLooper()).postDelayed({
            updateCurrentTime()
        }, 1000)
    }
    
    override fun onBackPressed() {
        // 禁用返回键，确保闹钟必须手动停止
    }
    
    override fun onDestroy() {
        super.onDestroy()
        cancelSnooze()
        // 不在这里关闭TTS服务，让它在应用运行时保持可用
    }
}