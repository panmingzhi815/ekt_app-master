package com.hbtl.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.hbtl.beans.CrossUploaderAuthInfoBus;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

import timber.log.Timber;

/**
 * Created by Administrator on 2017/11/2.
 */
// [后台执行的定时任务](http://www.jianshu.com/p/4b10de256ddf)
// [Android四大组件:BroadcastReceiver史上最全面解析](http://www.jianshu.com/p/ca3d87a4cdf3)
// [Android定时任务详解](http://jellybins.github.io/2016/01/26/Android%E5%AE%9A%E6%97%B6%E4%BB%BB%E5%8A%A1%E8%AF%A6%E8%A7%A3/)
public class RunningService extends Service {

    public static final String ACTION = "com.hbtl.service.RunningService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Timber.d("[####]RunningService" + " - Executed at " + new Date().toString());
                // TODO 定时上传数据...

                // 心跳包上传...
                //CommonUtils.getInstance().appBeatHeart();

                // 离线数据上报...
                //CommonUtils.getInstance().appGateDataReport();
                // 构建[离线数据上报]事件通知...
                CrossUploaderAuthInfoBus crossUploaderAuthInfoBus = new CrossUploaderAuthInfoBus();
                crossUploaderAuthInfoBus.uploaderWay = "RUN_SERVICE_ING";
                crossUploaderAuthInfoBus.uploaderState = "notifyUploader";
                EventBus.getDefault().post(crossUploaderAuthInfoBus);

                // AppPingGate...
                //CommonUtils.getInstance().appGatePing();
            }
        }).start();

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //int anHour = 60 * 60 * 1000; // 这是一小时的毫秒数
        int anHour = 10 * 1000; // 这是一小时的毫秒数
        long triggerAtTime = System.currentTimeMillis() + anHour;
        Intent i = new Intent(this, AlarmReceiver.class);
        i.setAction(RunningService.ACTION);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtTime, pi);


//        //获取AlarmManager系统服务
//        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//
//        //包装需要执行Service的Intent
//        Intent iii = new Intent(this, AlarmReceiver.class);
//        iii.setAction(RunningService.ACTION);
//        PendingIntent pendingIntent = PendingIntent.getService(this, 0, iii, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        //触发服务的起始时间
//        long triggerAtTime = SystemClock.elapsedRealtime();
//
//        //使用AlarmManger的setRepeating方法设置定期执行的时间间隔(seconds秒)和需要执行的Service
//        manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, 3 * 1000, pendingIntent);

        return super.onStartCommand(intent, flags, startId);
    }
}