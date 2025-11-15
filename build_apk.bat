@echo off
echo ===============================================
echo         天气闹钟应用 APK 构建脚本
echo ===============================================
echo.

:: 检查环境
echo [1/6] 检查构建环境...
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ 错误: 未找到Java环境
    echo 请安装JDK 8或更高版本
    goto :end
)
echo ✅ Java环境检测成功

echo.
echo [2/6] 检查Gradle...
gradlew.bat --version >nul 2>&1
if errorlevel 1 (
    echo ❌ 错误: 未找到gradlew.bat
    echo 请确保在项目根目录运行此脚本
    goto :end
)
echo ✅ Gradle检测成功

echo.
echo [3/6] 清理之前的构建...
if exist app\build (
    rmdir /s /q app\build
    echo ✅ 构建目录已清理
) else (
    echo ✅ 构建目录干净
)

echo.
echo [4/6] 同步Gradle依赖...
gradlew.bat clean
if errorlevel 1 (
    echo ❌ Gradle同步失败
    echo 请检查网络连接和依赖配置
    goto :end
)
echo ✅ 依赖同步成功

echo.
echo [5/6] 构建Debug APK...
gradlew.bat assembleDebug
if errorlevel 1 (
    echo ❌ APK构建失败
    echo 请检查代码错误和依赖问题
    goto :end
)

echo.
echo [6/6] 检查生成的APK...
if exist app\build\outputs\apk\debug\app-debug.apk (
    echo.
    echo 🎉 构建成功！
    echo.
    echo 📱 APK文件位置:
    echo    app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo 📲 安装命令:
    echo    adb install app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo ⚠️  在安装前，请确保：
    echo    1. 手机已开启开发者模式
    echo    2. USB调试已开启
    echo    3. 手机已连接电脑
) else (
    echo ❌ 未找到生成的APK文件
)

:end
echo.
echo 按任意键退出...
pause >nul