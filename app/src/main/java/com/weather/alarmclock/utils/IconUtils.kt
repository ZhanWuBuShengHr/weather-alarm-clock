package com.weather.alarmclock.utils

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.TypedValue
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 图标工具类
 * 负责创建和管理应用的自定义图标
 */
object IconUtils {
    
    /**
     * 创建天气主题的自定义图标
     */
    fun createWeatherIcon(context: Context): Bitmap {
        val size = dpToPx(context, 64)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        // 设置背景颜色
        canvas.drawColor(Color.parseColor("#FF6B6B"))
        
        // 创建画笔
        val paint = Paint().apply {
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        
        // 绘制太阳图标
        drawSun(canvas, size / 2f, size / 3f, paint, context)
        
        // 绘制温度数字
        paint.apply {
            color = Color.WHITE
            typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
            textSize = spToPx(context, 16f)
        }
        canvas.drawText("23°C", size / 2f, size * 2f / 3f, paint)
        
        // 绘制闹钟图标
        drawAlarmClock(canvas, size * 3f / 4f, size / 4f, paint, context)
        
        return bitmap
    }
    
    /**
     * 创建闹钟主题的自定义图标
     */
    fun createAlarmIcon(context: Context): Bitmap {
        val size = dpToPx(context, 64)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        // 设置渐变背景
        val gradientPaint = Paint().apply {
            isAntiAlias = true
        }
        // 这里可以添加渐变逻辑，暂时使用纯色
        canvas.drawColor(Color.parseColor("#4A90E2"))
        
        // 绘制闹钟图标
        drawAlarmClock(canvas, size / 2f, size / 2f, paint, context)
        
        return bitmap
    }
    
    /**
     * 创建时间主题的自定义图标
     */
    fun createTimeIcon(context: Context): Bitmap {
        val size = dpToPx(context, 64)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        canvas.drawColor(Color.parseColor("#7ED321"))
        
        // 绘制时钟图标
        drawClock(canvas, size / 2f, size / 2f, context)
        
        return bitmap
    }
    
    /**
     * 绘制太阳图标
     */
    private fun drawSun(canvas: Canvas, centerX: Float, centerY: Float, paint: Paint, context: Context) {
        // 太阳主体
        paint.color = Color.parseColor("#FFE66D")
        canvas.drawCircle(centerX, centerY, dpToPx(context, 12f), paint)
        
        // 太阳光线
        paint.color = Color.parseColor("#FFA500")
        val rayLength = dpToPx(context, 8f)
        val rayCount = 8
        
        for (i in 0 until rayCount) {
            val angle = (i * 360f / rayCount) * Math.PI.toFloat() / 180f
            val startX = centerX + Math.cos(angle.toDouble()).toFloat() * dpToPx(context, 12f)
            val startY = centerY + Math.sin(angle.toDouble()).toFloat() * dpToPx(context, 12f)
            val endX = centerX + Math.cos(angle.toDouble()).toFloat() * (dpToPx(context, 12f) + rayLength)
            val endY = centerY + Math.sin(angle.toDouble()).toFloat() * (dpToPx(context, 12f) + rayLength)
            
            canvas.drawLine(startX, startY, endX, endY, paint)
        }
    }
    
    /**
     * 绘制闹钟图标
     */
    private fun drawAlarmClock(canvas: Canvas, centerX: Float, centerY: Float, paint: Paint, context: Context) {
        val radius = dpToPx(context, 16f)
        
        // 闹钟外圈
        paint.color = Color.WHITE
        canvas.drawCircle(centerX, centerY, radius, paint)
        
        // 闹钟内圈
        paint.color = Color.parseColor("#333333")
        canvas.drawCircle(centerX, centerY, radius * 0.8f, paint)
        
        // 闹钟指针
        paint.strokeWidth = dpToPx(context, 2f)
        paint.color = Color.WHITE
        paint.strokeCap = Paint.Cap.ROUND
        
        // 时针
        val hourAngle = (Calendar.getInstance().get(Calendar.HOUR) % 12) * 30f - 90f
        val hourLength = radius * 0.5f
        canvas.drawLine(
            centerX, centerY,
            centerX + Math.cos(Math.toRadians(hourAngle.toDouble())).toFloat() * hourLength,
            centerY + Math.sin(Math.toRadians(hourAngle.toDouble())).toFloat() * hourLength,
            paint
        )
        
        // 分针
        val minuteAngle = Calendar.getInstance().get(Calendar.MINUTE) * 6f - 90f
        val minuteLength = radius * 0.7f
        paint.strokeWidth = dpToPx(context, 1.5f)
        canvas.drawLine(
            centerX, centerY,
            centerX + Math.cos(Math.toRadians(minuteAngle.toDouble())).toFloat() * minuteLength,
            centerY + Math.sin(Math.toRadians(minuteAngle.toDouble())).toFloat() * minuteLength,
            paint
        )
        
        // 中心点
        paint.color = Color.WHITE
        canvas.drawCircle(centerX, centerY, dpToPx(context, 2f), paint)
        
        // 铃铛
        val bellY = centerY - radius - dpToPx(context, 4f)
        paint.color = Color.parseColor("#FFD700")
        canvas.drawCircle(centerX, bellY, dpToPx(context, 3f), paint)
    }
    
    /**
     * 绘制时钟图标
     */
    private fun drawClock(canvas: Canvas, centerX: Float, centerY: Float, context: Context) {
        val paint = Paint()
        val radius = dpToPx(context, 20f)
        
        // 时钟外圈
        paint.color = Color.WHITE
        canvas.drawCircle(centerX, centerY, radius, paint)
        
        // 时钟刻度
        paint.color = Color.parseColor("#333333")
        paint.strokeWidth = dpToPx(context, 2f)
        
        for (i in 0 until 12) {
            val angle = i * 30f
            val startX = centerX + Math.cos(Math.toRadians((angle - 90).toDouble())).toFloat() * radius * 0.8f
            val startY = centerY + Math.sin(Math.toRadians((angle - 90).toDouble())).toFloat() * radius * 0.8f
            val endX = centerX + Math.cos(Math.toRadians((angle - 90).toDouble())).toFloat() * radius * 0.9f
            val endY = centerY + Math.sin(Math.toRadians((angle - 90).toDouble())).toFloat() * radius * 0.9f
            
            canvas.drawLine(startX, startY, endX, endY, paint)
        }
        
        // 指针
        drawClockHands(canvas, centerX, centerY, radius, context)
    }
    
    /**
     * 绘制时钟指针
     */
    private fun drawClockHands(canvas: Canvas, centerX: Float, centerY: Float, radius: Float, context: Context) {
        val calendar = Calendar.getInstance()
        val paint = Paint()
        
        paint.color = Color.parseColor("#333333")
        
        // 时针
        paint.strokeWidth = dpToPx(context, 3f)
        val hourAngle = (calendar.get(Calendar.HOUR) % 12) * 30f + calendar.get(Calendar.MINUTE) * 0.5f - 90f
        canvas.drawLine(
            centerX, centerY,
            centerX + Math.cos(Math.toRadians(hourAngle.toDouble())).toFloat() * radius * 0.5f,
            centerY + Math.sin(Math.toRadians(hourAngle.toDouble())).toFloat() * radius * 0.5f,
            paint
        )
        
        // 分针
        paint.strokeWidth = dpToPx(context, 2f)
        val minuteAngle = calendar.get(Calendar.MINUTE) * 6f - 90f
        canvas.drawLine(
            centerX, centerY,
            centerX + Math.cos(Math.toRadians(minuteAngle.toDouble())).toFloat() * radius * 0.7f,
            centerY + Math.sin(Math.toRadians(minuteAngle.toDouble())).toFloat() * radius * 0.7f,
            paint
        )
        
        // 中心点
        paint.color = Color.parseColor("#333333")
        canvas.drawCircle(centerX, centerY, dpToPx(context, 2f), paint)
    }
    
    /**
     * 保存图标到文件
     */
    fun saveIconToFile(context: Context, bitmap: Bitmap, fileName: String): Boolean {
        return try {
            val filesDir = context.filesDir
            val iconDir = File(filesDir, "icons")
            if (!iconDir.exists()) {
                iconDir.mkdirs()
            }
            
            val file = File(iconDir, "$fileName.png")
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.close()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * 加载自定义图标
     */
    fun loadCustomIcon(context: Context, fileName: String): Bitmap? {
        return try {
            val filesDir = context.filesDir
            val iconDir = File(filesDir, "icons")
            val file = File(iconDir, "$fileName.png")
            
            if (file.exists()) {
                android.graphics.BitmapFactory.decodeFile(file.absolutePath)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * 获取系统安装的应用图标
     */
    fun getInstalledAppIcon(context: Context, packageName: String): Bitmap? {
        return try {
            val packageManager = context.packageManager
            val drawable = packageManager.getApplicationIcon(packageName)
            
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            
            bitmap
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * 获取所有可用的图标类型
     */
    fun getAvailableIconTypes(): List<IconType> {
        return listOf(
            IconType("WEATHER", "天气图标", "包含天气信息和温度显示"),
            IconType("ALARM", "闹钟图标", "经典闹钟样式图标"),
            IconType("TIME", "时间图标", "简洁的时钟图标")
        )
    }
    
    /**
     * 图标类型数据类
     */
    data class IconType(
        val id: String,
        val name: String,
        val description: String
    )
    
    // 工具方法
    private fun dpToPx(context: Context, dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        )
    }
    
    private fun spToPx(context: Context, sp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp,
            context.resources.displayMetrics
        )
    }
}