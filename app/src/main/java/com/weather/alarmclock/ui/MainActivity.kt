package com.weather.alarmclock.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.weather.alarmclock.R
import com.weather.alarmclock.viewmodel.MainViewModel

/**
 * 主Activity
 * 显示当前天气和闹钟状态
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var viewModel: MainViewModel
    
    private lateinit var tvWeatherTitle: TextView
    private lateinit var tvWeatherInfo: TextView
    private lateinit var btnRefreshWeather: Button
    private lateinit var tvAlarmTitle: TextView
    private lateinit var tvAlarmStatus: TextView
    private lateinit var btnSetAlarm: Button
    private lateinit var btnTestSpeech: Button
    private lateinit var tvSpeechStatus: TextView
    private lateinit var tvError: TextView
    private lateinit var progressLoading: ProgressBar
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        setupViewModel()
        checkPermissions()
        loadData()
        setupClickListeners()
    }
    
    /**
     * 初始化UI组件
     */
    private fun initViews() {
        tvWeatherTitle = findViewById(R.id.tv_weather_title)
        tvWeatherInfo = findViewById(R.id.tv_weather_info)
        btnRefreshWeather = findViewById(R.id.btn_refresh_weather)
        tvAlarmTitle = findViewById(R.id.tv_alarm_title)
        tvAlarmStatus = findViewById(R.id.tv_alarm_status)
        btnSetAlarm = findViewById(R.id.btn_set_alarm)
        btnTestSpeech = findViewById(R.id.btn_test_speech)
        tvSpeechStatus = findViewById(R.id.tv_speech_status)
        tvError = findViewById(R.id.tv_error)
        progressLoading = findViewById(R.id.progress_loading)
        
        // 设置标题
        supportActionBar?.title = "天气闹钟"
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }
    
    /**
     * 设置ViewModel
     */
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        
        // 观察天气数据变化
        viewModel.currentWeather.observe(this) { weatherData ->
            updateWeatherDisplay(weatherData)
        }
        
        // 观察错误信息
        viewModel.error.observe(this) { error ->
            error?.let {
                showError(it)
            }
        }
        
        // 观察加载状态
        viewModel.isLoading.observe(this) { isLoading ->
            updateLoadingState(isLoading)
        }
        
        // 观察闹钟状态
        viewModel.alarmStatus.observe(this) { status ->
            updateAlarmStatusDisplay(status)
        }
        
        // 观察语音播报状态
        viewModel.isSpeaking.observe(this) { isSpeaking ->
            updateSpeechButtonState(isSpeaking)
        }
    }
    
    /**
     * 检查权限
     */
    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) 
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.INTERNET)
            }
        }
    }
    
    /**
     * 加载数据
     */
    private fun loadData() {
        viewModel.loadCurrentWeather()
        viewModel.loadAlarmStatus()
    }
    
    /**
     * 设置点击监听器
     */
    private fun setupClickListeners() {
        // 刷新天气
        btnRefreshWeather.setOnClickListener {
            viewModel.refreshWeather()
        }
        
        // 设置闹钟
        btnSetAlarm.setOnClickListener {
            val intent = Intent(this, AlarmSettingActivity::class.java)
            startActivity(intent)
        }
        
        // 测试语音播报
        btnTestSpeech.setOnClickListener {
            viewModel.testSpeech()
        }
    }
    
    /**
     * 更新天气显示
     */
    private fun updateWeatherDisplay(weatherData: com.weather.alarmclock.model.WeatherData?) {
        if (weatherData != null && weatherData.currentWeather != null) {
            val cityName = weatherData.cityInfo?.name ?: "未知地区"
            val weatherText = weatherData.currentWeather.text ?: "未知天气"
            val temperature = weatherData.currentWeather.temperature?.toString() ?: "--"
            
            tvWeatherInfo.text = "城市: $cityName\n天气: $weatherText\n温度: ${temperature}°C"
            tvWeatherInfo.setTextColor(ContextCompat.getColor(this, android.R.color.black))
        } else {
            tvWeatherInfo.text = "正在获取天气信息...\n请稍候"
            tvWeatherInfo.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))
        }
    }
    
    /**
     * 更新闹钟状态显示
     */
    private fun updateAlarmStatusDisplay(status: AlarmStatus) {
        when (status) {
            is AlarmStatus.Disabled -> {
                tvAlarmStatus.text = "已禁用"
                btnSetAlarm.text = "设置闹钟"
            }
            is AlarmStatus.Enabled -> {
                val timeString = "${status.hour.toString().padStart(2, '0')}:${status.minute.toString().padStart(2, '0')}"
                tvAlarmStatus.text = "已启用 - $timeString"
                btnSetAlarm.text = "修改闹钟"
            }
            is AlarmStatus.Ringing -> {
                tvAlarmStatus.text = "正在响铃"
                btnSetAlarm.text = "关闭闹钟"
            }
        }
    }
    
    /**
     * 更新加载状态
     */
    private fun updateLoadingState(isLoading: Boolean) {
        progressLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        
        if (isLoading) {
            tvWeatherInfo.text = "正在获取天气信息..."
        }
    }
    
    /**
     * 更新语音播报按钮状态
     */
    private fun updateSpeechButtonState(isSpeaking: Boolean) {
        if (isSpeaking) {
            btnTestSpeech.text = "正在播报..."
            btnTestSpeech.isEnabled = false
            tvSpeechStatus.text = "语音播报中..."
            tvSpeechStatus.visibility = View.VISIBLE
        } else {
            btnTestSpeech.text = "测试语音播报"
            btnTestSpeech.isEnabled = true
            tvSpeechStatus.visibility = View.GONE
        }
    }
    
    /**
     * 显示错误信息
     */
    private fun showError(error: String) {
        tvError.text = error
        tvError.visibility = View.VISIBLE
        
        // 3秒后自动隐藏错误信息
        tvError.postDelayed({
            tvError.visibility = View.GONE
        }, 3000)
    }
    
    // 权限请求
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            loadData()
        } else {
            Toast.makeText(this, "需要网络权限才能获取天气信息", Toast.LENGTH_LONG).show()
            showError("需要网络权限才能获取天气信息")
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_select_icon -> {
                val intent = Intent(this, IconSelectActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_help -> {
                val intent = Intent(this, HelpActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    override fun onResume() {
        super.onResume()
        // 页面重新可见时刷新数据
        loadData()
    }
}

/**
 * 闹钟状态密封类
 */
sealed class AlarmStatus {
    data class Enabled(val hour: Int, val minute: Int) : AlarmStatus()
    object Disabled : AlarmStatus()
}