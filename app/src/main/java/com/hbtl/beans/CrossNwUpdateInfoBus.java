package com.hbtl.beans;

import com.hbtl.service.NetworkType;

/**
 * Created by 亚飞 on 2015-10-13.
 */
public class CrossNwUpdateInfoBus {
    // 上传事件通知方式...
    public String updateWay;// [NetworkStateService: 后台网络服务事件通知]...
    // 上传事件通知状态...
    public NetworkType nwsType;// 当前网络状态 ...
}
