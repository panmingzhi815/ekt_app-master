package com.hbtl.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hbtl.config.CoamBuildVars;
import com.hbtl.utils.CommonUtils;

import java.io.File;

/**
 * Created by yzhang on 2017/12/3.
 */

public class DownloadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case CoamBuildVars.ACTION_DOWNLOAD_PROGRESS:
                int pro = intent.getExtras().getInt("progress");

                // 更新下载进度条...
                CommonUtils.getInstance().updateDownloadingProcess(pro);
                break;
            case CoamBuildVars.ACTION_DOWNLOAD_SUCCESS:
                CommonUtils.getInstance().updateDownloadingSuccess(context, (File) intent.getExtras().getSerializable("file"));

                // 关闭后台下载服务...
                CommonUtils.getInstance().stopDownloadService(context);
                break;
            case CoamBuildVars.ACTION_DOWNLOAD_FAIL:
                CommonUtils.getInstance().updateDownloadingFail();
                // 关闭后台下载服务...
                CommonUtils.getInstance().stopDownloadService(context);
                break;
            default:
                // TODO: ...
                break;
        }
    }
}