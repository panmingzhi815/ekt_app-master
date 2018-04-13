
*****************************************************************************************************************************************************************************************

### adb 命令

```
// 卸载软件
adb uninstall com.hbtl.ekt
// 安装软件 [The -r option tells adb to reinstall the app]
adb install -r yourapp.apk
// 其它用法
adb shell pm list packages | grep "name of your app here"
```

```
adb root
adb shell
pm list packages
pm uninstall com.android.chrome
```

*****************************************************************************************************************************************************************************************
