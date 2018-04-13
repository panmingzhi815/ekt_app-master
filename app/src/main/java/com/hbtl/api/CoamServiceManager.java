package com.hbtl.api;

import org.reactivestreams.Subscription;

//import rx.Subscriber;
//import rx.Subscription;

public class CoamServiceManager {

    private CoamHttpService mCoamService;


    // Realm 数据库实例
    //Realm mRealmInstance;
    private Subscription mSubscription;

    public CoamServiceManager() {
        mCoamService = CoamHttpService.getInstance();

        //mRealmInstance = Realm.getDefaultInstance();
    }

    private static volatile CoamServiceManager Instance = null;  // <<< 这里添加了 volatile

    public static CoamServiceManager getInstance() {
        CoamServiceManager inst = Instance;  // <<< 在这里创建临时变量
        if (inst == null) {
            synchronized (CoamServiceManager.class) {
                inst = Instance;
                if (inst == null) {
                    inst = new CoamServiceManager();
                    Instance = inst;
                }
            }
        }
        return inst;  // <<< 注意这里返回的是临时变量
    }
}
