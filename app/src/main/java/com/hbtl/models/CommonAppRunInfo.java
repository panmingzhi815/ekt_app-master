package com.hbtl.models;

import com.google.gson.annotations.Expose;

/**
 * Created by 亚飞 on 2015-10-21.
 */
public class CommonAppRunInfo {
    @Expose
    public String arId;
    @Expose
    public String bpcId; // 绑定推送客户端 ClientId
    @Expose
    public String bptId; // 绑定推送主题
    @Expose
    public double lat;
    @Expose
    public double lng;
    @Expose
    public String location;
    @Expose
    public int appVersion;//程序运行版本
    @Expose
    public String runPlatform;//运行系统平台及版本
    @Expose
    public String runTime;//运行GPS更新时间
    @Expose
    public String loginIp;//登陆IP地址
    @Expose
    public String loginTime;//登陆时间

    // AppRunSettingInfo List...
    //..........................
    @Expose
    public String deviceId;// 设备编号

    @Expose
    public int oUpDn = 1;// 离线数据单次上报量

    @Expose
    public String token;// 授权认证 token ...
    @Expose
    public long tokenEts;// 授权认证 token 过期时间...

    @Expose
    public String aesKey;// 授权认证 aesKey ...
    @Expose
    public String aesIv;// 授权认证 aesIv ...
    @Expose
    public String rsaPublicKey;// 授权认证 rsaPublicKey ...
    //@Expose
    //public long aesEts;// 授权认证 aes 过期时间...

    @Expose
    public String mpiServer;// 本地设置的访问服务器地址
}
