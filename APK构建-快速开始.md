# 🚀 天气闹钟APK构建 - 快速开始

## 📋 当前状态

✅ **已完成文件**:
- `build_apk.bat` - Windows自动构建脚本
- `build_apk.sh` - macOS/Linux自动构建脚本
- `APK构建指南.md` - 详细构建文档
- `gradlew` / `gradlew.bat` - Gradle Wrapper脚本
- `gradle/wrapper/gradle-wrapper.properties` - Gradle配置

## ⚠️ 重要提醒

**在构建APK之前，您需要完成以下环境配置：**

### 1. 安装Java开发环境
```bash
# 检查Java版本 (需要JDK 8+)
java -version

# 如果没有Java，请安装JDK 11
```

### 2. 安装Android SDK
```bash
# 下载Android Studio或单独安装SDK
# 确保安装Android SDK 34和Build Tools
```

### 3. 配置环境变量
```bash
# JAVA_HOME - 指向JDK安装目录
# ANDROID_HOME - 指向Android SDK目录
# PATH - 添加tools和platform-tools目录
```

### 4. 配置天气API密钥
**这是应用正常运行的关键！**

在 `WeatherApiService.kt` 文件中：
```kotlin
// 查找并替换这个字段
private const val DEFAULT_API_KEY = "YOUR_API_KEY_HERE"

// 替换为实际的API密钥
private const val DEFAULT_API_KEY = "your_actual_api_key"
```

**推荐的API服务：**
- [心知天气](https://www.seniverse.com/) (免费额度)
- [和风天气](https://dev.qweather.com/) (免费额度)

## 🏗️ 构建APK (3种方法)

### 方法一：自动构建脚本 (最简单)

**Windows:**
1. 双击运行 `build_apk.bat`
2. 等待构建完成
3. 按照提示安装APK

**macOS/Linux:**
1. 打开终端，进入项目目录
2. 运行: `chmod +x build_apk.sh`
3. 运行: `./build_apk.sh`

### 方法二：Gradle命令行

```bash
# 清理项目
./gradlew clean

# 构建Debug APK
./gradlew assembleDebug

# 构建Release APK (需配置签名)
./gradlew assembleRelease
```

### 方法三：Android Studio (推荐开发者)

1. 打开Android Studio
2. 选择 "Open an existing project"
3. 选择本项目目录
4. 点击 "Sync Now"
5. Build → Build Bundle(s)/APK(s) → Build APK(s)

## 📱 安装到手机

### 自动安装 (推荐)
构建成功后，脚本会自动提示安装命令：
```bash
adb install app\build\outputs\apk\debug\app-debug.apk
```

### 手动安装
1. 将生成的APK文件传输到手机
2. 在手机文件管理器中找到APK
3. 点击安装 (可能需要允许"未知来源")

### 启用USB调试
1. 进入手机"设置" → "关于手机"
2. 连续点击"版本号"7次开启开发者模式
3. 在"开发者选项"中启用"USB调试"
4. 连接手机到电脑

## 🎯 功能测试清单

构建并安装成功后，请测试以下功能：

### 基础功能
- [ ] 应用正常启动
- [ ] 主界面显示天气信息
- [ ] 网络权限正常
- [ ] 权限请求弹窗正常

### 核心功能
- [ ] 语音播报功能 (测试TTS)
- [ ] 闹钟设置功能
- [ ] 天气数据获取
- [ ] 锁屏界面正常显示

### 高级功能
- [ ] 闹钟响铃功能
- [ ] 通知权限管理
- [ ] 电池优化设置
- [ ] 后台运行正常

## 🛠️ 常见问题解决

### 构建失败
1. **Java环境问题**
   ```bash
   java -version  # 检查Java版本
   echo $JAVA_HOME  # 检查JAVA_HOME配置
   ```

2. **依赖下载失败**
   - 检查网络连接
   - 配置代理 (如需要)
   - 清理缓存: `./gradlew clean`

3. **SDK版本问题**
   ```bash
   sdkmanager "build-tools;34.0.0"
   sdkmanager "platforms;android-34"
   ```

### 运行时问题
1. **应用崩溃**
   - 检查API密钥配置
   - 查看日志: `adb logcat`
   - 确认网络连接

2. **权限问题**
   - 手动授予必要权限
   - 检查系统权限设置

## 📞 获取帮助

如果遇到问题：

1. **查看构建日志** - 仔细阅读错误信息
2. **检查环境** - 确认Java、SDK、Gradle版本
3. **查看文档** - 参考 `APK构建指南.md`
4. **验证API** - 确认天气API密钥有效

## 📈 APK文件说明

### Debug APK
- **文件名**: `app-debug.apk`
- **大小**: 约15-25MB
- **特点**: 包含调试信息，未压缩优化
- **用途**: 测试和开发

### Release APK (可选)
- **文件名**: `app-release.apk`
- **大小**: 约10-15MB  
- **特点**: 经过压缩优化
- **用途**: 生产发布

---

**🎉 现在您可以开始构建APK了！**

请按照上述步骤操作，如果一切顺利，您的天气闹钟APK很快就能在手机上运行了！

**最后提醒**: 别忘了配置天气API密钥哦！这是应用正常工作的关键。