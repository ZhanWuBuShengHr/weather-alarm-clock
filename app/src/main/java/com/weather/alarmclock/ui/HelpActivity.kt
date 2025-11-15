package com.weather.alarmclock.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.weather.alarmclock.R

/**
 * 帮助/引导界面
 * 提供应用使用说明和功能介绍
 */
class HelpActivity : AppCompatActivity() {
    
    private lateinit var tvTitle: TextView
    private lateinit var tvContent: TextView
    private lateinit var btnBack: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        
        initViews()
        setupToolbar()
        loadContent()
        setupClickListeners()
    }
    
    /**
     * 初始化视图组件
     */
    private fun initViews() {
        tvTitle = findViewById(R.id.tv_help_title)
        tvContent = findViewById(R.id.tv_help_content)
        btnBack = findViewById(R.id.btn_back)
    }
    
    /**
     * 设置工具栏
     */
    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "帮助"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    /**
     * 加载帮助内容
     */
    private fun loadContent() {
        val helpText = """
            天气闹钟 - 使用说明
            
            📱 应用功能
            • 显示实时天气信息
            • 设置个性化闹钟
            • 语音播报天气内容
            • 智能贪睡功能
            
            ⚙️ 设置方法
            1. 点击"设置闹钟"进入设置界面
            2. 开启闹钟开关
            3. 设置闹钟时间（小时/分钟）
            4. 输入所在城市名称
            5. 点击"保存"完成设置
            
            🔊 语音功能
            • 闹钟响铃时会自动播报天气
            • 可以手动测试语音播报功能
            • 支持中文语音朗读
            
            📞 贪睡功能
            • 点击"贪睡5分钟"延迟闹钟
            • 贪睡期间会继续播报天气
            • 可重复设置贪睡
            
            🔄 数据更新
            • 天气信息会定期自动更新
            • 点击天气卡片可手动刷新
            • 首次使用需要网络权限
            
            📋 注意事项
            • 确保手机连接互联网
            • 允许应用访问网络权限
            • 建议将应用添加到自启动列表
            • 关闭电池优化以确保闹钟正常工作
            
            ❓ 常见问题
            Q: 闹钟不响？
            A: 检查是否开启了闹钟，检查电池优化设置
            
            Q: 天气信息不准确？
            A: 确保网络连接正常，检查城市名称设置
            
            Q: 语音播报异常？
            A: 检查手机音量设置，确保TTS功能正常
            
            💬 联系支持
            如有问题或建议，欢迎反馈！
            
            感谢使用天气闹钟！😊
        """.trimIndent()
        
        tvTitle.text = "使用说明"
        tvContent.text = helpText
    }
    
    /**
     * 设置点击监听器
     */
    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }
        
        // 添加更多帮助内容的交互
        tvContent.setOnClickListener {
            // 点击内容可以滚动或复制
        }
    }
}