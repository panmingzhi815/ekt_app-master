package com.hbtl.tm.messenger.browser;

/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2016.
 */

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.hbtl.app.CoamApplicationLoader;
import com.hbtl.ekt.R;
import com.hbtl.tm.components.Theme;
import com.hbtl.tm.LocaleController;
import com.hbtl.tm.messenger.ShareBroadcastReceiver;
import com.hbtl.tm.messenger.support.customtabs.CustomTabsCallback;
import com.hbtl.tm.messenger.support.customtabs.CustomTabsClient;
import com.hbtl.tm.messenger.support.customtabs.CustomTabsIntent;
import com.hbtl.tm.messenger.support.customtabs.CustomTabsServiceConnection;
import com.hbtl.tm.messenger.support.customtabs.CustomTabsSession;
import com.hbtl.tm.messenger.support.customtabsclient.CustomTabsHelper;
import com.hbtl.tm.messenger.support.customtabsclient.ServiceConnection;
import com.hbtl.tm.messenger.support.customtabsclient.ServiceConnectionCallback;

import java.lang.ref.WeakReference;

import timber.log.Timber;

public class Browser {

    private static WeakReference<CustomTabsSession> customTabsCurrentSession;
    private static CustomTabsSession customTabsSession;
    private static CustomTabsClient customTabsClient;
    private static CustomTabsServiceConnection customTabsServiceConnection;
    private static String customTabsPackageToBind;
    private static WeakReference<Activity> currentCustomTabsActivity;

    private static CustomTabsSession getCurrentSession() {
        return customTabsCurrentSession == null ? null : customTabsCurrentSession.get();
    }

    private static void setCurrentSession(CustomTabsSession session) {
        customTabsCurrentSession = new WeakReference<>(session);
    }

    private static CustomTabsSession getSession() {
        if (customTabsClient == null) {
            customTabsSession = null;
        } else if (customTabsSession == null) {
            customTabsSession = customTabsClient.newSession(new NavigationCallback());
            setCurrentSession(customTabsSession);
        }
        return customTabsSession;
    }

    public static void bindCustomTabsService(Activity activity) {
        if (Build.VERSION.SDK_INT < 15) {
            return;
        }
        Activity currentActivity = currentCustomTabsActivity == null ? null : currentCustomTabsActivity.get();
        if (currentActivity != null && currentActivity != activity) {
            unbindCustomTabsService(currentActivity);
        }
        if (customTabsClient != null) {
            return;
        }
        currentCustomTabsActivity = new WeakReference<>(activity);
        try {
            if (TextUtils.isEmpty(customTabsPackageToBind)) {
                customTabsPackageToBind = CustomTabsHelper.getPackageNameToUse(activity);
                if (customTabsPackageToBind == null) {
                    return;
                }
            }
            customTabsServiceConnection = new ServiceConnection(new ServiceConnectionCallback() {
                @Override
                public void onServiceConnected(CustomTabsClient client) {
                    customTabsClient = client;
                    if (customTabsClient != null) {
                        customTabsClient.warmup(0);
                    }
                }

                @Override
                public void onServiceDisconnected() {
                    customTabsClient = null;
                }
            });
            if (!CustomTabsClient.bindCustomTabsService(activity, customTabsPackageToBind, customTabsServiceConnection)) {
                customTabsServiceConnection = null;
            }
        } catch (Exception e) {
            Timber.e("tmessages" + e);
        }
    }

    public static void unbindCustomTabsService(Activity activity) {
        if (Build.VERSION.SDK_INT < 15 || customTabsServiceConnection == null) {
            return;
        }
        Activity currentActivity = currentCustomTabsActivity == null ? null : currentCustomTabsActivity.get();
        if (currentActivity == activity) {
            currentCustomTabsActivity.clear();
        }
        try {
            activity.unbindService(customTabsServiceConnection);
        } catch (Exception e) {
            Timber.e("tmessages" + e);
        }
        customTabsClient = null;
        customTabsSession = null;
    }

    private static class NavigationCallback extends CustomTabsCallback {
        @Override
        public void onNavigationEvent(int navigationEvent, Bundle extras) {
            Timber.e("tmessages" + "code = " + navigationEvent + " extras " + extras);
        }
    }

    public static void openUrl(Context context, String url) {
        openUrl(context, Uri.parse(url), true);
    }

    public static void openUrl(Context context, Uri uri) {
        openUrl(context, uri, true);
    }

    public static void openUrl(Context context, String url, boolean allowCustom) {
        if (context == null || url == null) {
            return;
        }
        openUrl(context, Uri.parse(url), allowCustom);
    }

    public static void openUrl(Context context, Uri uri, boolean allowCustom) {
        if (context == null || uri == null) {
            return;
        }

        try {
            boolean internalUri = isInternalUri(uri);
            boolean canCustomTabs = true;// 自定义显示设置
            allowCustom = true;//是否使用内部打开浏览器
            if (Build.VERSION.SDK_INT >= 15 && allowCustom && canCustomTabs && !internalUri) {
                Intent share = new Intent(CoamApplicationLoader.appContextInstance, ShareBroadcastReceiver.class);
                share.setAction(Intent.ACTION_SEND);

                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder(getSession());
                builder.setToolbarColor(Theme.ACTION_BAR_COLOR);
                builder.setShowTitle(true);
                builder.setActionButton(BitmapFactory.decodeResource(context.getResources(), R.drawable.abc_ic_menu_share_mtrl_alpha), LocaleController.getString("ShareFile", R.string.ShareFile), PendingIntent.getBroadcast(CoamApplicationLoader.appContextInstance, 0, share, 0), false);
                CustomTabsIntent intent = builder.build();
                intent.launchUrl((Activity) context, uri);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                if (internalUri) {
                    ComponentName componentName = new ComponentName(context.getPackageName(), "LaunchActivity.class.getName()");
                    intent.setComponent(componentName);
                }
                intent.putExtra(android.provider.Browser.EXTRA_APPLICATION_ID, context.getPackageName());
                context.startActivity(intent);
            }
        } catch (Exception e) {
            Timber.e("tmessages" + e);
        }
    }

    public static boolean isInternalUrl(String url) {
        return isInternalUri(Uri.parse(url));
    }

    public static boolean isInternalUri(Uri uri) {
        String host = uri.getHost();
        host = host != null ? host.toLowerCase() : "";
        return "tg".equals(uri.getScheme()) || "telegram.me".equals(host) || "telegram.dog".equals(host);
    }
}
