
#### 集成迁移项目 SwitchButton 指南

[GitHub](https://github.com/kyleduo/SwitchButton)

#### 迁移

将 \library\src\main\java\com\kyleduo\switchbutton/* 拷贝到 com/coam/plugins/switchbutton/* 下

修改包名路径

将 res 下的各项文件全部迁移或合并到 coam 项目下

修复 R.id.* 错误

#### 使用问题

将 com.kyleduo

    <com.kyleduo.switchbutton.SwitchButton
        android:id="@+id/sb_disable_control"
        style="@style/SwitchButtonStyle"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:checked="true" />

全部改成 com.coam.plugins

    <com.coam.plugins.switchbutton.SwitchButton
        android:id="@+id/sb_disable_control"
        style="@style/SwitchButtonStyle"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:checked="true" />
