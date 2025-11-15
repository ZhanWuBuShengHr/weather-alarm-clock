#!/bin/bash

echo "==============================================="
echo "         天气闹钟应用 APK 构建脚本"
echo "==============================================="
echo

# 检查环境
echo "[1/6] 检查构建环境..."
if ! command -v java &> /dev/null; then
    echo "❌ 错误: 未找到Java环境"
    echo "请安装JDK 8或更高版本"
    exit 1
fi
echo "✅ Java环境检测成功"

echo
echo "[2/6] 检查Gradle..."
if [ ! -f "./gradlew" ]; then
    echo "❌ 错误: 未找到gradlew"
    echo "请确保在项目根目录运行此脚本"
    exit 1
fi
echo "✅ Gradle检测成功"

echo
echo "[3/6] 清理之前的构建..."
if [ -d "app/build" ]; then
    rm -rf app/build
    echo "✅ 构建目录已清理"
else
    echo "✅ 构建目录干净"
fi

echo
echo "[4/6] 同步Gradle依赖..."
./gradlew clean
if [ $? -ne 0 ]; then
    echo "❌ Gradle同步失败"
    echo "请检查网络连接和依赖配置"
    exit 1
fi
echo "✅ 依赖同步成功"

echo
echo "[5/6] 构建Debug APK..."
./gradlew assembleDebug
if [ $? -ne 0 ]; then
    echo "❌ APK构建失败"
    echo "请检查代码错误和依赖问题"
    exit 1
fi

echo
echo "[6/6] 检查生成的APK..."
if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
    echo
    echo "🎉 构建成功！"
    echo
    echo "📱 APK文件位置:"
    echo "   app/build/outputs/apk/debug/app-debug.apk"
    echo
    echo "📲 安装命令:"
    echo "   adb install app/build/outputs/apk/debug/app-debug.apk"
    echo
    echo "⚠️  在安装前，请确保："
    echo "   1. 手机已开启开发者模式"
    echo "   2. USB调试已开启"
    echo "   3. 手机已连接电脑"
else
    echo "❌ 未找到生成的APK文件"
fi

echo
echo "构建脚本执行完成！"