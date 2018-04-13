package com.hbtl.service;

/**
 * Created by yzhang on 2017/11/24.
 */

public enum NetworkType {
    NETWORK_WIFI("WiFi"),
    NETWORK_5G("5G"),
    NETWORK_4G("4G"),
    NETWORK_3G("3G"),
    NETWORK_2G("2G"),
    NETWORK_UNKNOWN("UNKNOWN"),
    NETWORK_NO("DISCONNECTION");

    private String desc;

    NetworkType(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return desc;
    }
}
