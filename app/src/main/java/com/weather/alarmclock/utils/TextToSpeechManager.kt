package com.weather.alarmclock.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale
import java.util.UUID

/**
 * 文本转语音工具类
 * 负责管理语音播报功能
 */
class TextToSpeechManager private constructor(
    private val context: Context,
    private var tts: TextToSpeech? = null
) : TextToSpeech.OnInitListener {
    
    companion object {
        @Volatile
        private var INSTANCE: TextToSpeechManager? = null
        
        fun getInstance(context: Context): TextToSpeechManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TextToSpeechManager(context.applicationContext, null).also { INSTANCE = it }
            }
        }
    }
    
    private var isInitialized = false
    private val utteranceId = UUID.randomUUID().toString()
    private var onSpeakCompleteListener: (() -> Unit)? = null
    private var onSpeakErrorListener: ((String) -> Unit)? = null
    
    /**
     * 初始化TTS
     */
    init {
        initTTS()
    }
    
    /**
     * 初始化文本转语音引擎
     */
    private fun initTTS() {
        tts = TextToSpeech(context, this)
    }
    
    /**
     * TTS初始化回调
     */
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // 设置中文语音
            val result = tts?.setLanguage(Locale.CHINESE)
            
            when {
                result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED -> {
                    // 中文不支持，使用默认语言
                    tts?.setLanguage(Locale.getDefault())
                }
            }
            
            // 设置语音参数
            tts?.let { ttsInstance ->
                ttsInstance.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        // 语音开始播报
                    }
                    
                    override fun onDone(utteranceId: String?) {
                        onSpeakCompleteListener?.invoke()
                    }
                    
                    override fun onError(utteranceId: String?) {
                        onSpeakErrorListener?.invoke("语音播报出错")
                    }
                })
            }
            
            isInitialized = true
        } else {
            isInitialized = false
        }
    }
    
    /**
     * 播报文本
     */
    fun speak(text: String): Boolean {
        if (!isInitialized || tts == null) {
            onSpeakErrorListener?.invoke("语音服务未准备就绪")
            return false
        }
        
        if (text.isEmpty()) {
            return true
        }
        
        // 清空队列
        tts?.stop()
        
        // 准备语音参数
        val params = hashMapOf(
            TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID to utteranceId
        )
        
        // 播报文本
        val result = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params as HashMap<String, String>?, utteranceId)
        
        return result != TextToSpeech.ERROR
    }
    
    /**
     * 停止播报
     */
    fun stop() {
        tts?.stop()
    }
    
    /**
     * 检查TTS是否支持中文
     */
    fun isChineseSupported(): Boolean {
        return if (tts != null && isInitialized) {
            val result = tts?.isLanguageAvailable(Locale.CHINESE)
            result == TextToSpeech.LANG_AVAILABLE || result == TextToSpeech.LANG_COUNTRY_AVAILABLE
        } else {
            false
        }
    }
    
    /**
     * 获取当前语言
     */
    fun getCurrentLanguage(): Locale? {
        return if (isInitialized) {
            tts?.language
        } else {
            null
        }
    }
    
    /**
     * 设置播报完成监听器
     */
    fun setOnSpeakCompleteListener(listener: () -> Unit) {
        onSpeakCompleteListener = listener
    }
    
    /**
     * 设置播报错误监听器
     */
    fun setOnSpeakErrorListener(listener: (String) -> Unit) {
        onSpeakErrorListener = listener
    }
    
    /**
     * 设置语速
     */
    fun setSpeechRate(speechRate: Float) {
        tts?.setSpeechRate(speechRate)
    }
    
    /**
     * 设置音调
     */
    fun setPitch(pitch: Float) {
        tts?.setPitch(pitch)
    }
    
    /**
     * 设置语音包
     */
    fun setVoice(voice: android.speech.Voice) {
        tts?.setVoice(voice)
    }
    
    /**
     * 释放资源
     */
    fun destroy() {
        stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
        onSpeakCompleteListener = null
        onSpeakErrorListener = null
        INSTANCE = null
    }
}