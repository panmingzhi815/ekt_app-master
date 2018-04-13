package com.hbtl.beans;

/**
 * Created by 亚飞 on 2015-10-13.
 */
public class CrossUploaderAuthInfoBus {
    // 上传事件通知方式...
    public String uploaderWay;// [RUN_SERVICE_ING: 后台定时服务上传事件通知|AUTH_UPLOAD_DATA: 上传完成事件通知|NETWORK_CHANGE:网络状态改变事件通知]...
    // 上传事件通知状态...
    public String uploaderState;// [notifyUploader:通知后台上传|waitingUploader:等待后台上传|startUploader:开始上传|uploaderSuccess: 后台上传验证成功|uploaderFailed: 后台上传验证失败|uploaderError: 后台网络上传失败]...

    // 返回具体数据
//    public JSONObject RunInfo;
}
