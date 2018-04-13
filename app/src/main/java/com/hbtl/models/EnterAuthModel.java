package com.hbtl.models;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by yafei on 2016-05-22.
 */
public class EnterAuthModel extends RealmObject {
    @PrimaryKey
    @Index
    public String qrId = UUID.randomUUID().toString();
    @Required
    public String authWay;// 认证验证方式 [noAuth: 尚未开启验证| QrCode: 二维码验证| QrCove: 明文二维码验证| IdCard: 身份证验证] authWay is an required field
    //@Required
    public String authState;//[no:未验证|start:开始验证|waiting:等待验证|detector:人脸识别|verify:接口验证|interrupt:验证中断|complete:验证结束] authState is an options field
    //@Required
    public String icNo = null;// icNo is an options field
    //@Required
    public String qrCode;// [认证验证码] qrCode is an options field
    //@Required
    public String qrCardSn;// qrCardSn is an options field
    //@Required
    public long qrCardTs; // qrCardTs is an options field
    //@Required
    public String qrCardSign; // qrCardSign is an options field
    @Required
    public String captureImgFn; // captureImgFn is an required field
    //@Required
    public long authEnterDts; // 入园天 authEnterDts is an required field

    // ... Generated getters and setters ...
}
