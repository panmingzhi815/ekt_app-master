package com.hbtl.beans;

/**
 * Created by 亚飞 on 2015-10-13.
 */
public class CrossLoadTokenInfoBus {
    // 加载 Token 方式...
    public String loadWay;// [DeviceRegister: 设备注册获取 Token]...
    // 加载 Token 状态...
    public String loadState;// [success: 成功|failed: 失败|error: 错误异常]...
    // Auth 类型...
    public String authType;// [Basic: 账户认证|Bearer: token认证] ...
    // Token 类型...
    public String tokenType;// [tmp: 临时-Token|verify: 长效-Token] ...
    // 授权 token...
    public String token;// required ...
}
