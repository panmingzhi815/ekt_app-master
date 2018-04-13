package com.hbtl.beans;

import com.google.gson.annotations.Expose;

/**
 * Created by 亚飞 on 2015-10-13.
 */
public class CrossCsUploaderBus {
    //    @Expose
//    public String ebWay;// 当做 EventBus 事件传递时的传递标识 [None:默认非 EventBus 消息|fromPhotoAlbumPickerEvent:发表空间评论消息事件|]
    // 通用返回值
    @Expose
    public String uploaderWay;
    @Expose
    public String uploaderInfo;
    @Expose
    public String uploaderId;
    @Expose
    public String uploaderFn;
    @Expose
    public String uploaderFk;//上传的云端文件名

    // 其它参数...
    public int upPos;
    public String upFgTag;

    @Expose
    public int upCloudId;
    @Expose
    public Boolean upResult;
    @Expose
    public int upReCode;
    @Expose
    public String upReDesc;
}
