package com.hbtl.utils;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;

public class DialogUtils {
    private static MaterialDialog mMaterialDialog;

    public static void showProgressDialog(Context context, String msg) {
        mMaterialDialog = new MaterialDialog.Builder(context)
                .title(null)
                .content(msg)
                .progress(true, 0)
                .show();
    }

    public static void closeProgressDialog() {
        if (mMaterialDialog != null) {
            mMaterialDialog.dismiss();
        }
    }

    public static void setProgressDialogContent(String content) {
        if (mMaterialDialog != null) {
            mMaterialDialog.setContent(content);
        }
    }
}

