package com.hbtl.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hbtl.config.CoamBuildVars;
import com.hbtl.ekt.R;

public class ToastHelper {

    public static final int LENGTH_LONG = 3500;
    public static final int LENGTH_SHORT = 2000;
    private Context mContext;
    private Handler mHandler;
    private String mToastContent = "";
    private int duration = 0;
    private int animStyleId = R.style.toastStyle;
    private int backgroundColor = Color.RED;
    private int textColor = Color.WHITE;
    private PopupWindow mPopupWindow;
    private View mView;
    private TextView mTextView;
    private View mToastLayout;
    private int statusBarHeight;

    private ToastHelper(Context context) {
        this.mContext = context;
        mHandler = new Handler();
        init();
    }

    private void init() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.app_toast_layout, null);
        mTextView = (TextView) mView.findViewById(R.id.textView);
        mToastLayout = mView.findViewById(R.id.toastLayout);
        mPopupWindow = new PopupWindow(mView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setAnimationStyle(animStyleId);
        mView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mContext instanceof Activity) {
                    Activity activity = (Activity) mContext;
                    if (activity != null && !activity.isFinishing()) {
                        mPopupWindow.dismiss();
                    }
                }
            }
        });
        statusBarHeight = mContext.getSharedPreferences(CoamBuildVars.STATUSBAR_HEIGHT, Context.MODE_PRIVATE).getInt(CoamBuildVars.STATUSBAR_HEIGHT, 0);
    }

    public static ToastHelper makeText(Context context, String content, int duration) {
        ToastHelper helper = new ToastHelper(context);
        helper.setDuration(duration);
        helper.setContent(content);
        return helper;
    }

    public static ToastHelper makeText(Context context, String content, int duration, ToastType toastType) {
        ToastHelper helper = new ToastHelper(context);
        helper.setDuration(duration);
        helper.setContent(content);
        helper.setToastType(toastType);
        return helper;
    }

    public static ToastHelper makeText(Context context, int strId, int duration, ToastType toastType) {
        ToastHelper helper = makeText(context, strId, duration);
        helper.setToastType(toastType);
        return helper;
    }

    public static ToastHelper makeText(Context context, int strId, int duration) {
        ToastHelper helper = new ToastHelper(context);
        helper.setDuration(duration);
        helper.setContent(context.getString(strId));
        return helper;
    }

    private ToastHelper setContent(String content) {
        this.mToastContent = content;
        return this;
    }

    private ToastHelper setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public void show() {
        mTextView.setText(mToastContent);
        mToastLayout.setBackgroundColor(backgroundColor);
        if (mContext instanceof Activity) {
            Activity activity = (Activity) mContext;
            if (activity != null && !activity.isFinishing()) {
                mPopupWindow.showAtLocation(mView, Gravity.TOP, 0, statusBarHeight);
            }
        }
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (mContext instanceof Activity) {
                    Activity activity = (Activity) mContext;
                    if (activity != null && !activity.isFinishing()) {
                        mPopupWindow.dismiss();
                    }
                }
            }
        }, duration);
    }

    private void setToastType(ToastType toastType) {
        switch (toastType) {
            case INFO:
                backgroundColor = Color.parseColor("#3498DB");
                break;
            case WARNING:
                backgroundColor = Color.parseColor("#F1C40F");
                break;
            case SUCCESS:
                backgroundColor = Color.parseColor("#2ECC71");
                break;
            case ERROR:
                backgroundColor = Color.parseColor("#E74C3C");
                break;
            case DEFAULT:
                backgroundColor = Color.parseColor("#34495E");
                break;
            default:
                backgroundColor = Color.parseColor("#34495E");
                break;
        }
    }

    public enum ToastType {
        INFO, WARNING, SUCCESS, ERROR, DEFAULT;
    }
}
