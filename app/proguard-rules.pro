# [5 分钟搞定 android 混淆](https://juejin.im/entry/5729c8821ea493006067b110)
# [Android进阶之ProGuard代码混淆](http://hanhailong.com/2015/12/28/Android进阶之ProGuard代码混淆/)

# [@ 定制化区域 @] #####################################################################################################################################################################################
# 1@[实体类] ----------------------------------------------------------------------------------------------------------------------------------------------

# Conversations #
#-keep class org.bouncycastle.mail.smime.**{*;}
-keep class org.eclipse.paho.**{*;}

## 自定义项目内混淆例外配置段 ##
## 需要取消混淆以下代码，否则与服务器的数据交互将出现问题 ##
-keep class com.hbtl.bean.**{*;}
-keep class com.hbtl.api.model.**{*;}
-keep class com.hbtl.api.session.**{*;}

-keep class com.hbtl.ekt.*

## customer clear warning ## #############################################################################################################################

#-keep class ch.imvs.** { *; }
-dontwarn ch.imvs.**

#-keep class internal.org.apache.http.entity.** { *; }
#-dontwarn internal.org.apache.http.entity.**

#-keep class org.slf4j.** { *; }
#-dontwarn org.slf4j.**

#-keep class java.awt.image.BufferedImage
#-keep class java.awt.** { *; }
-dontwarn java.awt.**

#@ [其它暂时注释]......
#-keep class android.net.http.SslError
#-keep class android.webkit.**{*;}

# [Warning:org.bouncycastle.mail.smime.util.FileBackedMimeBodyPart: can't find referenced field 'java.io.InputStream contentStream' in program class org.bouncycastle.mail.smime.util.FileBackedMimeBodyPart]
#-keep class org.bouncycastle.**{*;}
-dontwarn org.bouncycastle.**

# [proguard混淆带第三方库android项目-java mail](http://blog.csdn.net/leansmall/article/details/33394211)
#-keep class javax.mail.**{*;}
#-keep class javax.mail.internet.**{*;}
#-dontwarn javax.mail.**
#-dontwarn javax.mail.internet.**

# 2@[第三方包] ----------------------------------------------------------------------------------------------------------------------------------------------

#@ [Kotlin]混淆配置............................................................................................................
## [How to use Kotlin with Proguard](http://stackoverflow.com/questions/33547643/how-to-use-kotlin-with-proguard)

-dontwarn kotlin.**

#@ [EventBus]混淆配置............................................................................................................

### https://bunnyblue.github.io/post/EventBus-Proguard/
# 不添加如下混淆，运行时会报没有 onEvent 错误
## GreenRobot EventBus specific rules ##
# https://github.com/greenrobot/EventBus/blob/master/HOWTO.md#proguard-configuration
# Refresh ## https://github.com/krschultz/android-proguard-snippets/blob/master/libraries/proguard-eventbus.pro
#-keepclassmembers class ** {
#    public void onEvent*(***);
#}

# Only required if you use AsyncExecutor
#-keepclassmembers class * extends de.greenrobot.event.util.ThrowableFailureEvent {
#    public <init>(java.lang.Throwable);
#}

# Don't warn for missing support classes
#-dontwarn de.greenrobot.event.util.*$Support
#-dontwarn de.greenrobot.event.util.*$SupportManagerFragment


-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

#@ [RxJava]混淆配置............................................................................................................

# rxjava 自己根据warning写的，混淆效果不好
#-keep class rx.internal.util.** { *; }
#-dontwarn rx.internal.util.**

## https://github.com/artem-zinnatullin/RxJavaProGuardRules/blob/master/rxjava-proguard-rules/proguard-rules.txt
-dontwarn sun.misc.**

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

#@ [Glide]混淆配置............................................................................................................
# com.bumptech.glide.Glide: can't find dynamically referenced class com.bumptech.glide.GeneratedAppGlideModuleImpl
# [Release Build Failed for Android Studio 3](https://github.com/bumptech/glide/issues/2354)
# [bumptech/glide](https://github.com/bumptech/glide)
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

#@ [Gson]混淆配置............................................................................................................

#-libraryjars libs/gson-2.2.4.jar
#-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.* { *; }
#-keep class com.google.gson.examples.android.model.* { *; }
#-keep class com.google.gson.* { *;}

## https://github.com/google/gson/blob/master/examples/android-proguard-example/proguard.cfg
##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

#@ [Retrofit2]混淆配置............................................................................................................
## http://square.github.io/retrofit/
#-dontwarn retrofit.**
#-keep class retrofit.** { *; }
#-keepattributes Signature
#-keepattributes Exceptions

# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

#@ [OkHttp3]混淆配置............................................................................................................
## https://github.com/krschultz/android-proguard-snippets/blob/master/libraries/proguard-square-okhttp.pro
# OkHttp
#-keepattributes Signature
#-keepattributes *Annotation*
#-keep class com.squareup.okhttp.** { *; }
#-keep interface com.squareup.okhttp.** { *; }
#-dontwarn com.squareup.okhttp.**

# okhttp框架的混淆
## https://github.com/D-clock/Doc/blob/master/Gradle/3_ProGuard%E5%9F%BA%E7%A1%80%E8%AF%AD%E6%B3%95%E5%92%8C%E6%89%93%E5%8C%85%E9%85%8D%E7%BD%AE.md
#-dontwarn com.squareup.okhttp.internal.http.*
#-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
#-keepnames class com.levelup.http.okhttp.** { *; }
#-keepnames interface com.levelup.http.okhttp.** { *; }
#-keepnames class com.squareup.okhttp.** { *; }
#-keepnames interface com.squareup.okhttp.** { *; }

-dontwarn okhttp3.**

#@ [okio]混淆配置............................................................................................................
#-dontwarn com.squareup.**
#-dontwarn okio.**
# Okio
## https://github.com/krschultz/android-proguard-snippets/blob/master/libraries/proguard-square-okio.pro
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**

#@ [Picasso]混淆配置............................................................................................................
## [square/picasso](https://github.com/square/picasso)

-dontwarn com.squareup.okhttp.**

#@ [七牛]混淆配置............................................................................................................
## [Qiniu Resource (Cloud) Storage SDK for Android](https://github.com/qiniu/android-sdk)
-keep class com.qiniu.**{*;}
-keep class com.qiniu.**{public <init>();}
#-ignorewarnings

#@ [MaterialEditText]混淆配置............................................................................................................
-keep class com.rengwuxian.materialedittext.**{*;}

#@ [ButterKnife]混淆配置............................................................................................................
## http://jakewharton.github.io/butterknife/
# http://stackoverflow.com/questions/27277916/how-to-get-a-simple-android-studio-proguard-config-working
#-keep class butterknife.** {*;}
#-dontwarn butterknife.internal.**
#-keep class **$$ViewBinder {*;}

#-keepclasseswithmembernames class * {
#    @butterknife.* <fields>;
#}

#-keepclasseswithmembernames class * {
#    @butterknife.* <methods>;
#}


# Retain generated class which implement Unbinder.
-keep public class * implements butterknife.Unbinder { public <init>(**, android.view.View); }

# Prevent obfuscation of types which use ButterKnife annotations since the simple name
# is used to reflectively look up the generated ViewBinding.
-keep class butterknife.*
-keepclasseswithmembernames class * { @butterknife.* <methods>; }
-keepclasseswithmembernames class * { @butterknife.* <fields>; }

#@ [FaceBook-Stetho]混淆配置............................................................................................................
## https://github.com/facebook/stetho/tree/master/stetho-js-rhino#proguard
#-keep class com.facebook.stetho.** { *; }
#
## rhino (javascript)
#-dontwarn org.mozilla.javascript.**
#-dontwarn org.mozilla.classfile.**
#-keep class org.mozilla.javascript.** { *; }

# https://github.com/facebook/stetho/blob/master/stetho/proguard-consumer.pro

# Updated as of Stetho 1.1.1
# https://github.com/facebook/stetho/tree/master/stetho-js-rhino#proguard
# Note: Doesn't include Javascript console lines. See
-keep class com.facebook.stetho.** {*;}
-dontwarn com.facebook.stetho.**

# 内部WebView混淆过滤
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

#@ [高德]混淆配置............................................................................................................

#-libraryjars libs/AMap_Services_V2.7.0_20151125.jar
#-libraryjars libs/AMap_android_API_2DMap_Lib_V2.7.0_20151124.jar
#-libraryjars libs/AMap_Location_v2.1.0_20151202.jar

#高德相关混淆文件

# 3D 地图 V5.0.0之后：
#-keep   class com.amap.api.maps.**{*;}
#-keep   class com.autonavi.**{*;}
#-keep   class com.amap.api.trace.**{*;}

# 定位
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}

# 搜索
-keep class com.amap.api.services.**{*;}

# 2D地图
-keep class com.amap.api.maps2d.**{*;}
-keep class com.amap.api.mapcore2d.**{*;}

# 导航
#-keep class com.amap.api.navi.**{*;}
#-keep class com.autonavi.**{*;}

-keep class com.amap.**{*;}
-dontwarn com.amap.**

#@ [Mob-ShareSDK]混淆配置............................................................................................................
-keep class com.mob.**{*;}
-dontwarn com.mob.**
-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class **.R$* {*;}
-keep class **.R{*;}
-dontwarn cn.sharesdk.**
-dontwarn **.R$*
-keep class m.framework.**{*;}

# 3@[与js互相调用的类] ----------------------------------------------------------------------------------------------------------------------------------------------

-keepclasseswithmembers class com.demo.login.bean.ui.MainActivity$JSInterface {
      <methods>;
}

# 4@[反射相关的类和方法] ----------------------------------------------------------------------------------------------------------------------------------------------

# TODO 我的工程里没有...

# [@ 基本不用动区域 @] #####################################################################################################################################################################################
# @[基本指令区] ----------------------------------------------------------------------------------------------------------------------------------------------
#/ 指定代码的压缩级别
-optimizationpasses 5
#/ 是否使用大小写混合
-dontusemixedcaseclassnames
#/ 是否混淆第三方jar
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
#/ 混淆时是否做预校验
-dontpreverify
#/ 混淆时是否记录日志
-verbose
-printmapping proguardMapping.txt
#/ 指定混淆是采用的算法
-optimizations !code/simplification/cast,!field/*,!class/merging/*
#@-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keepattributes *Annotation*,InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable


#/ 优化时允许访问并修改有修饰符的类和类的成员
#-allowaccessmodification
#/ 忽略警告，避免打包时某些警告出现
#-ignorewarnings

# [在AS上使用第三方服务容易出现以下警告，请问如何解决？](https://www.zhihu.com/question/46649910)
# Error:warning: Ignoring InnerClasses attribute for an anonymous inner class
-dontoptimize
#-dontwarn


# @[默认保留区] ----------------------------------------------------------------------------------------------------------------------------------------------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Appliction
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.** {*;}

-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keep class **.R$* {
 *;
}
-keepclassmembers class * {
    void *(**On*Event);
}

# @[其它]..................
# RecyclerView
## https://code.google.com/p/android/issues/detail?id=190250
-keep public class * extends android.support.v7.widget.RecyclerView$LayoutManager {
    public <init>(...);
}
## 解决错误 Caused by: android.view.InflateException: Binary XML file line #77: Error inflating class android.support.v7.widget.RecyclerView
## End ###################################################################################################################################################

# SearchView
## [Cannot instantiate class android.support.v7.widget.SearchView](http://stackoverflow.com/questions/22582006/cannot-instantiate-class-android-support-v7-widget-searchview)
-keep class android.support.v7.widget.SearchView {*;}
## End ###################################################################################################################################################

# @[WebView] ----------------------------------------------------------------------------------------------------------------------------------------------
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.webView, jav.lang.String);
}
# [@ ... @] #####################################################################################################################################################################################

# 不混淆 [ysidcard.jar] 包
-keep class com.ivsign.android.IDCReader.IDCReaderSDK {*;}
-keep class com.otg.idcard.** {*;}

## End ###################################################################################################################################################

#2.0.9后加入语音通话功能，如需使用此功能的api，加入以下keep
#-keep class net.java.sip.** {*;}
#-keep class org.webrtc.voiceengine.** {*;}
#-keep class org.bitlet.** {*;}
#-keep class org.slf4j.** {*;}
#-keep class ch.imvs.** {*;}

-keep class java.util.concurrent.ConcurrentHashMap{*;}

## End ###################################################################################################################################################