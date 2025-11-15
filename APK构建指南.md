# 天气闹钟应用 - APK构建指南

## 📋 前置要求

### 开发环境要求
1. **Java Development Kit (JDK)**
   - JDK 8 或更高版本
   - 建议使用 JDK 11
   
2. **Android SDK**
   - Android SDK 34 (compileSdk版本)
   - Android Build Tools
   - Android Platform Tools

3. **环境变量配置**
   - `JAVA_HOME`: 指向JDK安装目录
   - `ANDROID_HOME`: 指向Android SDK目录
   - `PATH`: 添加JDK和Android SDK的tools目录

### 获取API密钥
**重要**: 天气API需要有效的API密钥才能正常工作！

1. **心知天气** (推荐，免费版本)
   - 注册地址: https://www.seniverse.com/
   - 获取免费API Key
   - 在 `WeatherApiService.kt` 中替换 `DEFAULT_API_KEY`

2. **和风天气** (推荐)
   - 注册地址: https://dev.qweather.com/
   - 获取API Key
   - 在 `WeatherApiService.kt` 中配置密钥

## 🏗️ 构建步骤

### 方法一: 使用自动化脚本 (推荐)

#### Windows用户:
1. 双击运行 `build_apk.bat`
2. 等待构建完成
3. 根据脚本输出安装APK

#### macOS/Linux用户:
1. 打开终端，切换到项目目录
2. 执行命令: `chmod +x build_apk.sh`
3. 执行命令: `./build_apk.sh`
4. 等待构建完成

### 方法二: 手动构建

1. **清理项目**
   ```bash
   ./gradlew clean
   ```

2. **构建Debug APK**
   ```bash
   ./gradlew assembleDebug
   ```

3. **构建Release APK** (如果需要发布)
   ```bash
   ./gradlew assembleRelease
   ```

### 方法三: Android Studio集成开发环境 (推荐)

1. **导入项目**
   - 打开Android Studio
   - 选择 "Open an existing project"
   - 选择本项目目录

2. **同步项目**
   - 点击 "Sync Now" 同步Gradle配置
   - 等待依赖下载完成

3. **配置API密钥**
   - 在代码中找到 `WeatherApiService.kt`
   - 替换 `DEFAULT_API_KEY` 为实际的API密钥

4. **构建APK**
   - 菜单栏: Build → Build Bundle(s)/APK(s) → Build APK(s)
   - APK将生成在: `app/build/outputs/apk/debug/`

## 📱 安装到真机

### 1. 启用开发者选项
1. 进入手机 "设置" → "关于手机"
2. 连续点击 "版本号" 7次
3. 返回设置，进入 "开发者选项"
4. 启用 "USB调试"

### 2. 连接手机
1. 使用USB线连接手机和电脑
2. 在手机弹出的对话框中选择 "允许USB调试"
3. 在电脑端验证连接: `adb devices`

### 3. 安装APK

#### 命令行方式:
```bash
adb install app\build\outputs\apk\debug\app-debug.apk
```

#### 直接安装:
- 将APK文件传输到手机
- 使用文件管理器点击安装

#### Android Studio安装:
- 在Android Studio中点击运行按钮 (绿色三角形)

## 🛠️ 故障排除

### 构建失败问题

1. **Gradle版本问题**
   ```bash
   ./gradlew wrapper --gradle-version 7.4
   ```

2. **依赖下载失败**
   - 检查网络连接
   - 配置代理: 在 `gradle.properties` 中添加代理设置

3. **SDK版本问题**
   - 检查Android SDK是否正确安装
   - 更新Build Tools: `sdkmanager "build-tools;34.0.0"`

### 运行时问题

1. **权限拒绝**
   - 检查应用权限设置
   - 手动授予必要权限

2. **网络请求失败**
   - 检查API密钥配置
   - 确认网络连接正常
   - 检查防火墙设置

3. **语音播报不工作**
   - 检查设备TTS引擎
   - 下载中文语音包

## 📦 APK文件说明

### Debug APK
- 文件名: `app-debug.apk`
- 大小: 约15-25MB
- 特点: 包含调试信息，未优化，测试用

### Release APK  
- 文件名: `app-release.apk`
- 大小: 约10-15MB
- 特点: 优化压缩，生产环境使用

## 🔍 测试验证

安装完成后，请按以下步骤验证:

1. **权限检查**
   - 确认网络、闹钟、通知权限已授予

2. **功能测试**
   - 查看天气信息显示
   - 测试语音播报功能
   - 设置闹钟并测试触发

3. **性能检查**
   - 观察应用启动时间
   - 检查内存使用情况
   - 验证电池消耗

## 📞 技术支持

如果遇到问题，请检查:

1. **日志信息**: 使用 `adb logcat` 查看详细日志
2. **系统信息**: Android版本、API级别兼容性
3. **环境配置**: Java版本、SDK版本、构建工具版本

---

**版本**: v1.0  
**更新时间**: 2025-11-14  
**适用范围**: 天气闹钟应用 v1.0