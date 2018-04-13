package com.hbtl.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.hbtl.config.CoamBuildVars;

import java.io.File;

import timber.log.Timber;

public class DownloadService extends Service {
    public static final int Flag_Init = 0; // 初始状态
    public static final int Flag_Down = 1; // 下载状态
    public static final int Flag_Pause = 2; // 暂停状态
    public static final int Flag_Done = 3; // 完成状态

    String url;
    String filePath;
    String fileName;

    // 下载进度
    private int progress = 0;

    public int getProgress() {
        return progress;
    }

    // 下载状态标志
    private int flag;

    public int getFlag() {
        return flag;
    }

    DownThread mThread;
    Downloader downloader;

    private static DownloadService instance;

    public static DownloadService getInstance() {
        return instance;
    }

//    // 参考单例设计模式
//    private static volatile DownloadService Instance = null;  // <<< 这里添加了 volatile
//
//    public static DownloadService getInstance() {
//        DownloadService inst = Instance;  // <<< 在这里创建临时变量
//        if (inst == null) {
//            synchronized (DownloadService.class) {
//                inst = Instance;
//                if (inst == null) {
//                    //inst = new DownloadService();
//                    inst = DownloadService.this;
//                    Instance = inst;
//                }
//            }
//        }
//        return inst;  // <<< 注意这里返回的是临时变量
//    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Timber.i("service.........onCreate");
        instance = this;
        flag = Flag_Init;
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String msg = intent.getExtras().getString("flag");
        url = intent.getExtras().getString("url");
        filePath = intent.getExtras().getString("filePath");
        fileName = intent.getExtras().getString("fileName");
        if (mThread == null) {
            mThread = new DownThread();
        }
        if (downloader == null) {
            downloader = new Downloader(this, url, filePath, fileName);
        }
        downloader.setDownloadListener(downListener);

        switch (msg) {
            case "start":
                startDownload();
                break;
            case "pause":
                downloader.pause();
                break;
            case "resume":
                downloader.resume();
                break;
            case "stop":
                downloader.cancel();
                stopSelf();
                break;
            default:
                Timber.e("unknown [msg: " + msg + "] error...");
                break;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void startDownload() {
        if (flag == Flag_Init || flag == Flag_Pause) {
            if (mThread != null && !mThread.isAlive()) {
                mThread = new DownThread();
            }
            mThread.start();
        }
    }

    @Override
    public void onDestroy() {
        Timber.i("service...........onDestroy");
        try {
            flag = 0;
            mThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mThread = null;
        super.onDestroy();
    }

    class DownThread extends Thread {

        @Override
        public void run() {

            if (flag == Flag_Init || flag == Flag_Done) {
                flag = Flag_Down;
            }

            downloader.start();
        }
    }

    private DownloadListener downListener = new DownloadListener() {

        int fileSize;
        Intent intent = new Intent();

        @Override
        public void onSuccess(File file) {
            intent.setAction(CoamBuildVars.ACTION_DOWNLOAD_SUCCESS);
            intent.putExtra("progress", 100);
            intent.putExtra("file", file);
            sendBroadcast(intent);
        }

        @Override
        public void onStart(int fileByteSize) {
            fileSize = fileByteSize;
            flag = Flag_Down;
        }

        @Override
        public void onResume() {
            flag = Flag_Down;
        }

        @Override
        public void onProgress(int receivedBytes) {
            if (flag == Flag_Down) {
                progress = (int) ((receivedBytes / (float) fileSize) * 100);
                intent.setAction(CoamBuildVars.ACTION_DOWNLOAD_PROGRESS);
                intent.putExtra("progress", progress);
                sendBroadcast(intent);

                if (progress == 100) {
                    flag = Flag_Done;
                }
            }
        }

        @Override
        public void onPause() {
            flag = Flag_Pause;
        }

        @Override
        public void onFail() {
            intent.setAction(CoamBuildVars.ACTION_DOWNLOAD_FAIL);
            sendBroadcast(intent);
            flag = Flag_Init;
        }

        @Override
        public void onCancel() {
            progress = 0;
            flag = Flag_Init;
        }
    };
}
