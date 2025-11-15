package com.weather.alarmclock.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.widget.ProgressBar
import android.widget.Toast
import com.weather.alarmclock.R
import com.weather.alarmclock.viewmodel.AlarmSettingViewModel

/**
 * 闹钟设置Activity
 * 用于设置闹钟时间和相关参数
 */
class AlarmSettingActivity : AppCompatActivity() {
    
    private lateinit var viewModel: AlarmSettingViewModel
    
    private lateinit var switchAlarmEnabled: Switch
    private lateinit var npHour: NumberPicker
    private lateinit var npMinute: NumberPicker
    private lateinit var etCityName: TextInputEditText
    private lateinit var btnTestAlarm: Button
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var tvTestResult: TextView
    private lateinit var progressSaving: ProgressBar
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_setting)
        
        // 初始化ViewModel
        viewModel = ViewModelProvider(this)[AlarmSettingViewModel::class.java]
        
        // 初始化UI组件
        initViews()
        
        // 设置监听器
        setupListeners()
        
        // 观察ViewModel数据变化
        observeViewModel()
    }
    
    /**
     * 初始化UI组件
     */
    private fun initViews() {
        switchAlarmEnabled = findViewById(R.id.switch_alarm_enabled)
        npHour = findViewById(R.id.np_hour)
        npMinute = findViewById(R.id.np_minute)
        etCityName = findViewById(R.id.et_city_name)
        btnTestAlarm = findViewById(R.id.btn_test_alarm)
        btnSave = findViewById(R.id.btn_save)
        btnCancel = findViewById(R.id.btn_cancel)
        tvTestResult = findViewById(R.id.tv_test_result)
        progressSaving = findViewById(R.id.progress_saving)
        
        // 设置时间选择器范围
        npHour.minValue = 0
        npHour.maxValue = 23
        npMinute.minValue = 0
        npMinute.maxValue = 59
        
        // 设置时间选择器显示样式
        npHour.displayedValues = (0..23).map { String.format("%02d", it) }.toTypedArray()
        npMinute.displayedValues = (0..59).map { String.format("%02d", it) }.toTypedArray()
        
        // 设置闹钟开关状态
        val savedEnabled = getSharedPreferences("alarm_prefs", MODE_PRIVATE)
            .getBoolean("daily_alarm_enabled", false)
        switchAlarmEnabled.isChecked = savedEnabled
    }
    
    /**
     * 设置监听器
     */
    private fun setupListeners() {
        // 时间选择器监听
        val timeChangeListener = NumberPicker.OnValueChangeListener { _, _, _ ->
            updateSaveButtonState()
        }
        npHour.setOnValueChangedListener(timeChangeListener)
        npMinute.setOnValueChangedListener(timeChangeListener)
        
        // 城市输入监听
        etCityName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                updateSaveButtonState()
            }
        }
        
        // 城市名称输入监听
        etCityName.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                updateSaveButtonState()
            }
        })
        
        // 闹钟开关监听
        switchAlarmEnabled.setOnCheckedChangeListener { _, _ ->
            updateSaveButtonState()
        }
        
        // 测试按钮
        btnTestAlarm.setOnClickListener {
            val hour = npHour.value
            val minute = npMinute.value
            val city = etCityName.text.toString()
            if (city.isNotEmpty()) {
                viewModel.testAlarm(hour, minute, city)
            } else {
                Toast.makeText(this, "请先输入城市名称", Toast.LENGTH_SHORT).show()
            }
        }
        
        // 保存按钮
        btnSave.setOnClickListener {
            saveAlarmSettings()
        }
        
        // 取消按钮
        btnCancel.setOnClickListener {
            finish()
        }
    }
    
    /**
     * 观察ViewModel数据变化
     */
    private fun observeViewModel() {
        // 当前设置
        viewModel.currentHour.observe(this) { hour ->
            if (hour != null && hour != npHour.value) {
                npHour.value = hour
            }
        }
        
        viewModel.currentMinute.observe(this) { minute ->
            if (minute != null && minute != npMinute.value) {
                npMinute.value = minute
            }
        }
        
        viewModel.isAlarmEnabled.observe(this) { enabled ->
            switchAlarmEnabled.isChecked = enabled ?: false
        }
        
        viewModel.selectedCity.observe(this) { city ->
            if (city != null && etCityName.text.toString() != city) {
                etCityName.setText(city)
            }
        }
        
        // 测试结果
        viewModel.testResult.observe(this) { result ->
            tvTestResult.text = result ?: ""
        }
        
        // 保存结果
        viewModel.saveResult.observe(this) { success ->
            if (success != null) {
                progressSaving.visibility = View.GONE
                btnSave.isEnabled = true
                btnSave.text = "保存"
                
                if (success) {
                    Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "保存失败，请重试", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    /**
     * 更新保存按钮状态
     */
    private fun updateSaveButtonState() {
        val city = etCityName.text.toString().trim()
        
        // 基本的输入验证
        val isValid = city.isNotEmpty() && city.length <= 20 // 城市名称长度限制
        
        btnSave.isEnabled = isValid
        btnSave.alpha = if (isValid) 1.0f else 0.5f
    }
    
    /**
     * 保存闹钟设置
     */
    private fun saveAlarmSettings() {
        val hour = npHour.value
        val minute = npMinute.value
        val enabled = switchAlarmEnabled.isChecked
        val city = etCityName.text.toString().trim()
        
        if (city.isEmpty()) {
            val textInputLayout = findViewById<TextInputLayout>(R.id.et_city_name).parent.parent as? TextInputLayout
            textInputLayout?.error = "请输入城市名称"
            return
        }
        
        // 清除错误
        val textInputLayout = findViewById<TextInputLayout>(R.id.et_city_name).parent.parent as? TextInputLayout
        textInputLayout?.error = null
        
        // 显示加载状态
        progressSaving.visibility = View.VISIBLE
        btnSave.isEnabled = false
        btnSave.text = "保存中..."
        
        viewModel.saveAlarmSettings(hour, minute, enabled, city)
    }
}