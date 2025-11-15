package com.weather.alarmclock.ui

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.weather.alarmclock.R
import com.weather.alarmclock.utils.IconUtils

/**
 * 图标选择界面
 * 允许用户选择不同的应用图标
 */
class IconSelectActivity : AppCompatActivity() {
    
    private lateinit var rvIcons: RecyclerView
    private lateinit var tvPreview: TextView
    private lateinit var ivPreview: ImageView
    private lateinit var btnSave: Button
    private lateinit var iconAdapter: IconAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_icon_select)
        
        initViews()
        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
        loadCurrentIcon()
    }
    
    /**
     * 初始化视图组件
     */
    private fun initViews() {
        rvIcons = findViewById(R.id.rv_icons)
        tvPreview = findViewById(R.id.tv_preview)
        ivPreview = findViewById(R.id.iv_preview)
        btnSave = findViewById(R.id.btn_save)
    }
    
    /**
     * 设置工具栏
     */
    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "选择图标"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    /**
     * 设置RecyclerView
     */
    private fun setupRecyclerView() {
        val iconTypes = IconUtils.getAvailableIconTypes()
        iconAdapter = IconAdapter(this, iconTypes) { iconType ->
            updatePreview(iconType)
        }
        
        rvIcons.apply {
            layoutManager = GridLayoutManager(this@IconSelectActivity, 2)
            adapter = iconAdapter
        }
    }
    
    /**
     * 设置点击监听器
     */
    private fun setupClickListeners() {
        btnSave.setOnClickListener {
            saveSelectedIcon()
        }
    }
    
    /**
     * 加载当前图标
     */
    private fun loadCurrentIcon() {
        // 从SharedPreferences获取当前选择的图标类型
        val sharedPrefs = getSharedPreferences("icon_prefs", Context.MODE_PRIVATE)
        val currentIconId = sharedPrefs.getString("current_icon_id", "WEATHER") ?: "WEATHER"
        
        val iconType = IconUtils.getAvailableIconTypes().find { it.id == currentIconId }
        iconType?.let { updatePreview(it) }
    }
    
    /**
     * 更新预览
     */
    private fun updatePreview(iconType: IconUtils.IconType) {
        val bitmap = when (iconType.id) {
            "WEATHER" -> IconUtils.createWeatherIcon(this)
            "ALARM" -> IconUtils.createAlarmIcon(this)
            "TIME" -> IconUtils.createTimeIcon(this)
            else -> IconUtils.createWeatherIcon(this)
        }
        
        ivPreview.setImageBitmap(bitmap)
        tvPreview.text = iconType.name
        
        // 高亮选中的图标
        iconAdapter.setSelectedIcon(iconType)
    }
    
    /**
     * 保存选中的图标
     */
    private fun saveSelectedIcon() {
        val selectedIcon = iconAdapter.getSelectedIcon() ?: return
        
        val sharedPrefs = getSharedPreferences("icon_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit()
            .putString("current_icon_id", selectedIcon.id)
            .apply()
        
        // 生成并保存图标文件
        val bitmap = when (selectedIcon.id) {
            "WEATHER" -> IconUtils.createWeatherIcon(this)
            "ALARM" -> IconUtils.createAlarmIcon(this)
            "TIME" -> IconUtils.createTimeIcon(this)
            else -> IconUtils.createWeatherIcon(this)
        }
        
        val success = IconUtils.saveIconToFile(this, bitmap, "app_icon")
        
        if (success) {
            setResult(RESULT_OK)
            finish()
        } else {
            // 显示保存失败的提示
            android.widget.Toast.makeText(this, "保存图标失败", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * 图标适配器
     */
    private inner class IconAdapter(
        private val context: Context,
        private val iconTypes: List<IconUtils.IconType>,
        private val onItemClick: (IconUtils.IconType) -> Unit
    ) : RecyclerView.Adapter<IconAdapter.IconViewHolder>() {
        
        private var selectedIcon: IconUtils.IconType? = null
        
        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): IconViewHolder {
            val view = android.view.LayoutInflater.from(context).inflate(R.layout.item_icon_preview, parent, false)
            return IconViewHolder(view)
        }
        
        override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
            holder.bind(iconTypes[position])
        }
        
        override fun getItemCount(): Int = iconTypes.size
        
        fun setSelectedIcon(iconType: IconUtils.IconType) {
            selectedIcon = iconType
            notifyDataSetChanged()
        }
        
        fun getSelectedIcon(): IconUtils.IconType? = selectedIcon
        
        inner class IconViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
            private val ivIcon: ImageView = itemView.findViewById(R.id.iv_icon)
            private val tvName: TextView = itemView.findViewById(R.id.tv_name)
            private val tvDesc: TextView = itemView.findViewById(R.id.tv_desc)
            private val container: android.view.ViewGroup = itemView.findViewById(R.id.container)
            
            fun bind(iconType: IconUtils.IconType) {
                // 生成图标
                val bitmap = when (iconType.id) {
                    "WEATHER" -> IconUtils.createWeatherIcon(context)
                    "ALARM" -> IconUtils.createAlarmIcon(context)
                    "TIME" -> IconUtils.createTimeIcon(context)
                    else -> IconUtils.createWeatherIcon(context)
                }
                
                ivIcon.setImageBitmap(bitmap)
                tvName.text = iconType.name
                tvDesc.text = iconType.description
                
                // 设置选中状态
                val isSelected = selectedIcon?.id == iconType.id
                container.alpha = if (isSelected) 1.0f else 0.7f
                
                // 点击事件
                container.setOnClickListener {
                    onItemClick(iconType)
                }
            }
        }
    }
}