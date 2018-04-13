package com.hbtl.utils;

/**
 * Created by yzhang on 2017/11/29.
 */

// [让Gradle构建支持NDK](http://www.iloveandroid.net/2015/09/18/GradleNdkSupport/)
// [Android NDK 介绍与使用示例](http://jk2k.com/2016/09/how-to-use-ndk-and-generate-so-file-in-android-studio/)
// [如何优雅地使用NDK](http://blog.majiajie.me/2016/03/27/%E5%A6%82%E4%BD%95%E4%BC%98%E9%9B%85%E5%9C%B0%E4%BD%BF%E7%94%A8NDK/)
public class EktCso {
    static {
        System.loadLibrary("EktCso");
    }

    public static native String getString();
}
