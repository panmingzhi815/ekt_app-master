package com.hbtl.beans;

import com.hbtl.service.NetworkType;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by 亚飞 on 2015-10-21.
 * App 运行全局设备参数类
 */
public class AppDeviceInfo {
    @Getter
    @Setter
    public int screenHeight; // 设备运行时获取 屏幕 高度
    @Getter
    @Setter
    public int screenWidth; // 设备运行时获取 屏幕 宽度
    @Getter
    @Setter
    public int statusBarHeight; // 设备运行时获取 状态栏 高度
    @Getter
    @Setter
    public int actionBarSize;   //设备运行时获取 Toolbar 高度

    // AppRunSettingInfo List...
    //..........................
    @Getter
    @Setter
    public boolean lockAppAuthUploader = false;   // 设备状态是否在忙[是否有在上传任务]...
    @Getter
    @Setter
    public int allowUpTs = 0;   // 设备忙碌等待时间[]...
    @Getter
    @Setter
    public NetworkType networkType = NetworkType.NETWORK_UNKNOWN;   //设备运行时获取 Network 网络状态
}
