package com.hbtl.service;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

// 注册信息验证时的用户体验提升 http://hello1010.com/verifycode/
public class SmsContentObserver extends ContentObserver {
    private static final String TAG = "SmsContentObserver";
    private Context mContext;
    private Handler mHandler;
    public static final int RECEIVER_IDENTIFY_CODE = 0x10;

    public SmsContentObserver(Context context, Handler handler) {
        super(handler);
        this.mContext = context;
        this.mHandler = handler;
    }

    //短信格式:【腾旅一卡通】您正在使用手机注册腾旅一卡通平台,您的验证码为633842,请在2个小时内输入验证码完成后续操作
    @Override
    public void onChange(boolean selfChange) {
        Uri inBoxUri = Uri.parse("content://sms/inbox");
        Cursor c = mContext.getContentResolver().query(inBoxUri, null, null, null, "date desc");
        if (c != null) {
            while (c.moveToNext()) {
                String body = c.getString(c.getColumnIndex("body"));
                if (body.startsWith("【腾旅一卡通】")) {
                    int index = body.indexOf("验证码");
                    String verifyCode = body.substring(index + 4, index + 10);
                    Message msg = Message.obtain();
                    msg.what = RECEIVER_IDENTIFY_CODE;
                    msg.obj = verifyCode;
                    mHandler.sendMessage(msg);
                    break;
                }
            }
            c.close();
        }
    }
}
