package com.weather.alarmclock.service

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

/**
 * 语音播报服务类
 * 使用Android内置的TextToSpeech API实现天气信息播报
 */
class TextToSpeechService private constructor(
    private val context: Context
) : TextToSpeech.OnInitListener {
    
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var isSpeaking = false
    
    companion object {
        @Volatile
        private var INSTANCE: TextToSpeechService? = null
        
        fun getInstance(context: Context): TextToSpeechService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TextToSpeechService(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    /**
     * 初始化TextToSpeech
     */
    fun initialize() {
        if (!isInitialized) {
            tts = TextToSpeech(context, this)
        }
    }
    
    /**
     * TextToSpeech初始化回调
     */
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // 设置语言为中文
            val result = tts?.setLanguage(Locale.CHINESE)
            tts?.setPitch(1.0f) // 音调
            tts?.setSpeechRate(0.8f) // 语速
            
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // 如果不支持中文，使用英文
                tts?.setLanguage(Locale.ENGLISH)
            }
            
            isInitialized = true
        } else {
            // 初始化失败
            isInitialized = false
        }
    }
    
    /**
     * 播报天气信息
     */
    fun speakWeather(weatherText: String, onStart: (() -> Unit)? = null, onDone: (() -> Unit)? = null) {
        if (!isInitialized) {
            initialize()
        }
        
        // 等待初始化完成
        waitForInitialization {
            if (isSpeaking) {
                stopSpeaking()
            }
            
            onStart?.invoke()
            isSpeaking = true
            
            tts?.speak(
                weatherText,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "weather_speech_${System.currentTimeMillis()}"
            )
        }
        
        // 监听播报完成
        tts?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                // 播报开始
            }
            
            override fun onDone(utteranceId: String?) {
                isSpeaking = false
                onDone?.invoke()
            }
            
            override fun onError(utteranceId: String?) {
                isSpeaking = false
                onDone?.invoke()
            }
        })
    }
    
    /**
     * 停止播报
     */
    fun stopSpeaking() {
        tts?.stop()
        isSpeaking = false
    }
    
    /**
     * 播报自定义文本
     */
    fun speak(text: String, onStart: (() -> Unit)? = null, onDone: (() -> Unit)? = null) {
        speakWeather(text, onStart, onDone)
    }
    
    /**
     * 检查是否正在播报
     */
    fun isSpeaking(): Boolean = isSpeaking
    
    /**
     * 等待初始化完成
     */
    private fun waitForInitialization(action: () -> Unit) {
        if (isInitialized) {
            action()
        } else {
            // 如果还没有初始化完成，延迟执行
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                waitForInitialization(action)
            }, 100)
        }
    }
    
    /**
     * 获取可用的语音引擎列表
     */
    fun getAvailableVoices(): List<TextToSpeech.Engine> {
        val intent = TextToSpeech.Engine.ACTION_CHECK_TTS_DATA
        return TextToSpeech.Engine.getEngines()
    }
    
    /**
     * 检查语音数据是否可用
     */
    fun isLanguageAvailable(): Boolean {
        return if (isInitialized) {
            tts?.isLanguageAvailable(Locale.CHINESE) != TextToSpeech.LANG_NOT_SUPPORTED
        } else false
    }
    
    /**
     * 设置语音参数
     */
    fun setSpeechParameters(pitch: Float, speechRate: Float) {
        tts?.setPitch(pitch)
        tts?.setSpeechRate(speechRate)
    }
    
    /**
     * 释放资源
     */
    fun shutdown() {
        stopSpeaking()
        tts?.shutdown()
        tts = null
        isInitialized = false
        INSTANCE = null
    }
}