package com.hbtl.service;

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.telephony.TelephonyManager
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Created by yzhang on 2017/12/6.
 * [Android:判断用户2G/3G/4G移动数据网络](https://www.zybuluo.com/cxm-2016/note/501999)
 */

class NetUtils(val context: Context) {
    // 当前WIFI是否可用
    val WIFI_AVAILABLE: Boolean
        get() {
            val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = connManager.activeNetworkInfo
            return (info != null && info.isConnected && info.type == ConnectivityManager.TYPE_WIFI)
        }
    // 当前网络是否可用
    val NETWORK_ENABLE: Boolean
        get() {
            val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = connManager.activeNetworkInfo
            return info.state == NetworkInfo.State.CONNECTED
        }
    // 手机管理器
    val TELEPHONY_MANAGER = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    // WiFi管理器
    val WIFI_MANAGER = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    // 连接活动管理器
    val CONNECTIVITY_MANAGER = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    // WiFi网卡的Mac地址
    val WIFI_MAC_ADDRESS: String?
        get() {
            val info = WIFI_MANAGER.connectionInfo
            if (WIFI_MANAGER.isWifiEnabled) {
                val localMac = info.macAddress
                if (localMac != null) return localMac.toUpperCase()
            }
            return null
        }
    /**
     * 通过callCmd("busybox ifconfig","HWaddr")获取mac地址
     * @attention 需要设备装有busybox工具
     * @return Mac Address
     */
    val MAC_FROM_CALL_CMD: String?
        get() {
            val result = callCmd("busybox ifconfig", "HWaddr")
            if (result == null || result.length <= 0) {
                return null
            }
            // 对该行数据进行解析
            // 例如:eth0 Link encap:Ethernet HWaddr 00:16:E8:3E:DF:67
            if (result.length > 0 && result.contains("HWaddr") == true) {
                val Mac = result.substring(result.indexOf("HWaddr") + 6, result.length - 1)
                if (Mac.length > 1) {
                    return Mac.replace(" ".toRegex(), "")
                }
            }
            return result
        }

    // CMD调用
    private fun callCmd(cmd: String, filter: String): String? {
        val pro = Runtime.getRuntime().exec(cmd)
        val br = BufferedReader(InputStreamReader(pro.inputStream))
        // 执行命令cmd,只取结果中含有filter的这一行
        var line: String? = ""
        do {
            if (line!!.contains(filter)) break
            line = br.readLine()
        } while (line != null)
        return line
    }

    // 本机Mac地址
    val MAC_ADDRESS: String?
        get() {
            return if (WIFI_AVAILABLE) {
                WIFI_MAC_ADDRESS
            } else {
                try {
                    MAC_FROM_CALL_CMD
                } catch (e: Throwable) {
                    Log.e("Mac-CMD", e.message)
                    null
                }
            }
        }
    // 中国运营商的名字
    val PROVIDER: String
        @SuppressLint("MissingPermission", "HardwareIds")
        get() {
            val IMSI = TELEPHONY_MANAGER.subscriberId
            if (IMSI == null) {
                if (TelephonyManager.SIM_STATE_READY == TELEPHONY_MANAGER.simState) {
                    val operator = TELEPHONY_MANAGER.simOperator
                    if (operator != null) {
                        if (operator.equals("464000") || operator.equals("464002") || operator.equals("464007")) {
                            return "中国移动"
                        } else if (operator.equals("464001")) {
                            return "中国联通"
                        } else if (operator.equals("464003")) {
                            return "中国电信"
                        }
                    }
                }
            } else {
                if (IMSI.startsWith("46000") || IMSI.startsWith("46002") || IMSI.startsWith("46007")) {
                    return "中国移动"
                } else if (IMSI.startsWith("46001")) {
                    return "中国联通"
                } else if (IMSI.startsWith("46003")) {
                    return "中国电信"
                }
            }
            return "UnKnown"
        }
    // 当前网络的连接类型
    val NET_WORK_CLASS: Int?
        get() {
            val netWorkInfo = CONNECTIVITY_MANAGER.activeNetworkInfo
            if (netWorkInfo != null && netWorkInfo.isAvailable && netWorkInfo.isConnected) {
                return netWorkInfo.type
            } else {
                return null
            }
        }
    // 当前网络的连接类型
    val NET_WORK_TYPE: NetworkType
        get() {
            if (WIFI_AVAILABLE) return NetworkType.NETWORK_WIFI
            return when (NET_WORK_CLASS) {
                TelephonyManager.NETWORK_TYPE_GPRS,
                TelephonyManager.NETWORK_TYPE_EDGE,
                TelephonyManager.NETWORK_TYPE_CDMA,
                TelephonyManager.NETWORK_TYPE_1xRTT,
                TelephonyManager.NETWORK_TYPE_IDEN -> {
                    NetworkType.NETWORK_2G
                }
                TelephonyManager.NETWORK_TYPE_UMTS,
                TelephonyManager.NETWORK_TYPE_EVDO_0,
                TelephonyManager.NETWORK_TYPE_EVDO_A,
                TelephonyManager.NETWORK_TYPE_HSDPA,
                TelephonyManager.NETWORK_TYPE_HSUPA,
                TelephonyManager.NETWORK_TYPE_HSPA,
                TelephonyManager.NETWORK_TYPE_EVDO_B,
                TelephonyManager.NETWORK_TYPE_EHRPD,
                TelephonyManager.NETWORK_TYPE_HSPAP -> {
                    NetworkType.NETWORK_3G
                }
                TelephonyManager.NETWORK_TYPE_LTE -> {
                    NetworkType.NETWORK_4G
                }
                else -> {
                    NetworkType.NETWORK_UNKNOWN
                }
            }
        }
    // 当前WiFi信号强度,单位"dBm"
    val WIFI_RSSI: Int
        get() {
            if (NET_WORK_TYPE == NetworkType.NETWORK_WIFI) {
                val wifiInfo = WIFI_MANAGER.connectionInfo
                return if (wifiInfo == null) 0 else wifiInfo.rssi
            }
            return 0
        }
    // 获得连接WiFi的名称
    val WIFI_SSID: String?
        get() {
            if (NET_WORK_TYPE == NetworkType.NETWORK_WIFI) {
                val wifiInfo = WIFI_MANAGER.connectionInfo
                return wifiInfo?.ssid
            }
            return null
        }

    // 当前SIM卡状态
    val SIM_STATE: Boolean = !(TELEPHONY_MANAGER.simState == TelephonyManager.SIM_STATE_ABSENT || TELEPHONY_MANAGER.simState == TelephonyManager.SIM_STATE_UNKNOWN)

    // 手机串号
    @SuppressLint("MissingPermission", "HardwareIds")
    val IMEI: String? = TELEPHONY_MANAGER.deviceId
    // 国际移动用户标识码
    @SuppressLint("MissingPermission", "HardwareIds")
    val IMSI: String? = TELEPHONY_MANAGER.subscriberId
}