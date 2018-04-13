package com.hbtl.service;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hbtl.app.CoamApplicationLoader;
import com.hbtl.utils.DialogUtils;
import com.hbtl.view.ToastHelper;
import com.hbtl.view.ToastHelper.ToastType;

public class QrCodeManager {
    private Context mContext;
    private String qrInfo;

    public QrCodeManager(Context context, String qrInfo) {
        this.mContext = context;
        this.qrInfo = qrInfo;
    }

    public void dealQrInfo() {
        // 校验字符串不可为空
        if (qrInfo.isEmpty())
            ToastHelper.makeText(mContext, "无法解析空数据", ToastHelper.LENGTH_LONG, ToastType.ERROR).show();

        // 判断字符串格式...
        if (qrInfo.startsWith("http://") || qrInfo.startsWith("https://")) {
            // 处理 URL
            dealUrl();
        } else {
            // 处理字符串
            // 入园码:::
            if (qrInfo.equals("E")) {
                ToastHelper.makeText(mContext, "二维码[qrInfo: " + qrInfo + "]通过,即将进入人脸识别程序...", ToastHelper.LENGTH_LONG, ToastType.INFO).show();

                // 调取摄像头完成人脸识别验证程序...
            } else if (qrInfo.equals("EKT")) {
                ToastHelper.makeText(mContext, "二维码[qrInfo: " + qrInfo + "]本地安全校验通过,请放行...", ToastHelper.LENGTH_LONG, ToastType.INFO).show();

                // 调取摄像头完成人脸识别验证程序...
            } else {
                ToastHelper.makeText(mContext, "无效二维码[qrInfo: " + qrInfo + "]", ToastHelper.LENGTH_LONG, ToastType.ERROR).show();
            }
        }
    }

    public void dealUrl() {
        try {
            DialogUtils.showProgressDialog(mContext, "正在处理...");
            //https://mas.syam.cc/commonService/SearchTruckOrderRoute/{orderId}
            //去掉http://
            String str = qrInfo.substring(7, qrInfo.length());
            String[] temps = str.split("/");
            switch (temps[2]) {
                //整车订单
                case "ShareTOrderInfo":
                    //getTruckOrder(Integer.parseInt(temps[3]));
                    break;
                //零担订单
                case "SharePOrderInfo":
                    //getPipelineOrder(Integer.parseInt(temps[3]));
                    break;
                case "ShareAccountInfo":
                    if (CoamApplicationLoader.getInstance().isSelf(temps[3])) {
                        DialogUtils.closeProgressDialog();
                        showRemindDialog("不能扫自己的名片");
                    } else {
                        //getUserInfo(temps[3]);
                    }
                    break;
                default:
                    DialogUtils.closeProgressDialog();
                    ToastHelper.makeText(mContext, "无法解析", ToastHelper.LENGTH_LONG, ToastType.ERROR).show();
                    break;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            DialogUtils.closeProgressDialog();
            ToastHelper.makeText(mContext, "无法解析[" + this.qrInfo + "]", ToastHelper.LENGTH_LONG, ToastType.ERROR).show();
        }
    }

    private void showRemindDialog(String content) {
        new MaterialDialog.Builder(mContext)
                .title("提示")
                .content(content)
                .positiveText("确定")
                .show();
    }
}
