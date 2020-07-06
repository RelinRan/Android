package com.android.utils;

import android.app.Notification;
import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * 手机系统桌面角标
 */
public class Badge {

    /**
     * 数量
     */
    private final static String KEY_NUMBER = "BADGE_NUMBER";

    /**
     * 程序包名
     */
    private static String packageName;

    /**
     * 启动类名
     */
    private static String launcherClassName;


    /**
     * 标记数量
     *
     * @param context
     * @return
     */
    public static int number(Context context) {
        return DataStorage.with(context).getInt(KEY_NUMBER, 0);
    }

    /**
     * 设置标记数量
     *
     * @param context
     * @param number
     */
    public static void setNumber(Context context, int number) {
        DataStorage.with(context).put(KEY_NUMBER, number);
    }

    /**
     * 添加标记数量
     *
     * @param context
     */
    public static void add(Context context) {
        int number = number(context);
        number++;
        set(context, number);
    }

    /**
     * 减少标记数量
     *
     * @param context
     */
    public static void subtract(Context context) {
        int number = number(context);
        number--;
        set(context, number);
    }


    /**
     * 重置标记数量
     *
     * @param context
     */
    public static void reset(Context context) {
        set(context, 0);
    }

    /**
     * 设置角标
     *
     * @param context
     * @param number
     */
    public static void set(Context context, int number) {
        Log.i(Badge.class.getSimpleName(), "mobile system:" + Build.MANUFACTURER.toLowerCase());
        if (number > -1) {
            if (packageName == null) {
                packageName = App.getPackageName(context);
            }
            if (launcherClassName == null) {
                launcherClassName = App.getLauncherClassName(context);
            }
            Log.i(Badge.class.getSimpleName(), "packageName:" + packageName);
            Log.i(Badge.class.getSimpleName(), "launcherClassName:" + launcherClassName);
            setHuaWei(context, number);
            setMIUI(number);
            setSamsung(context, number);
            setOPPO(context, number);
            setVIVO(context, number);
            setZuk(context, number);
            setHTC(context, number);
            setSony(context, number);
        }
        setNumber(context, number);
    }

    /**
     * 华为系统
     * <uses-permission android:name="com.huawei.android.launcher.permission.CHANGE_BADGE " />
     *
     * @param context
     * @param number  通知个数
     */
    private static void setHuaWei(Context context, int number) {
        if (Build.MANUFACTURER.toLowerCase().contains("huawei") || Build.MANUFACTURER.toLowerCase().contains("honor")) {
            try {
                Bundle extra = new Bundle();
                extra.putString("package", packageName);
                extra.putString("class", launcherClassName);
                extra.putInt("badgenumber", number);
                context.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, extra);
            } catch (Exception e) {
                Log.i(Badge.class.getSimpleName(), " 非华为系统，不支持圆点显示");
            }
        }
    }

    /**
     * 小米系统
     * <uses-permission android:name="android.permission.READ_APP_BADGE" />
     *
     * @param number
     */
    private static void setMIUI(int number) {
        if (Build.MANUFACTURER.toLowerCase().contains("xiaomi")) {
            try {
                Notification notification = new Notification();
                Field field = notification.getClass().getDeclaredField("extraNotification");
                Object extraNotification = field.get(notification);
                Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);
                method.invoke(extraNotification, number);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(Badge.class.getSimpleName(), " 非小米系统，不支持圆点显示");
            }
        }
    }

    /**
     * 联想系统
     *
     * @param context
     * @param number
     */
    private static void setZuk(Context context, int number) {
        if (Build.MANUFACTURER.toLowerCase().contains("zuk")) {
            try {
                Bundle extra = new Bundle();
                extra.putInt("app_badge_count", number);
                context.getContentResolver().call(Uri.parse("content://com.android.badge/badge"), "setAppBadgeCount", null, extra);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(Badge.class.getSimpleName(), " 非联想系统，不支持圆点显示");
            }
        }
    }

    /**
     * HTC
     *
     * @param context
     * @param number
     */
    private static void setHTC(Context context, int number) {
        if (Build.MANUFACTURER.toLowerCase().contains("htc")) {
            try {
                Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
                intent.putExtra("badge_count", number);
                intent.putExtra("badge_count_package_name", packageName);
                intent.putExtra("badge_count_class_name", launcherClassName);
                context.sendBroadcast(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(Badge.class.getSimpleName(), " 非HTC系统，不支持圆点显示");
            }
        }
    }

    /**
     * OPPO系统
     *
     * @param context
     * @param number
     */
    private static void setOPPO(Context context, int number) {
        if (Build.MANUFACTURER.toLowerCase().contains("oppo")) {
            try {
                Bundle extras = new Bundle();
                extras.putInt("app_badge_count", number);
                context.getContentResolver().call(Uri.parse("content://com.android.badge/badge"), "setAppBadgeCount", String.valueOf(number), extras);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(Badge.class.getSimpleName(), " 非OPPO系统，不支持圆点显示");
            }
        }
    }

    /**
     * VIVO系统
     *
     * @param context
     * @param number
     */
    private static void setVIVO(Context context, int number) {
        if (Build.MANUFACTURER.toLowerCase().contains("vivo")) {
            try {
                Intent intent = new Intent("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
                intent.putExtra("packageName", packageName);
                intent.putExtra("className", launcherClassName);
                intent.putExtra("notificationNum", number);
                context.sendBroadcast(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(Badge.class.getSimpleName(), " 非VIVO系统，不支持圆点显示");
            }
        }
    }


    /***
     * 三星权限
     * <uses-permission android:name="com.sec.android.provider.badge.permission.READ" />
     * <uses-permission android:name="com.sec.android.provider.badge.permission.WRITE" />
     * @param context
     * @param count
     */
    private static void setSamsung(Context context, int count) {
        if (Build.MANUFACTURER.toLowerCase().contains("samsung") || Build.MANUFACTURER.toLowerCase().contains("lg")) {
            Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            intent.putExtra("badge_count", count);
            intent.putExtra("badge_count_package_name", packageName);
            intent.putExtra("badge_count_class_name", launcherClassName);
            context.sendBroadcast(intent);
        }
    }


    private static AsyncQueryHandler asyncQueryHandler;

    /**
     * 索尼权限
     * <uses-permission android:name="com.sonymobile.home.permission.PROVIDER_INSERT_BADGE" />
     * <uses-permission android:name="com.sonyericsson.home.permission.BROADCAST_BADGE" />
     * <uses-permission android:name="com.sonyericsson.home.action.UPDATE_BADGE" />
     */
    private static void setSony(Context context, int number) {
        if (Build.MANUFACTURER.toLowerCase().contains("sony")) {
            try {
                if (asyncQueryHandler == null) {
                    asyncQueryHandler = new AsyncQueryHandler(context.getContentResolver()) {
                    };
                }
                try {
                    //官方给出方法
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("badge_count", number);
                    contentValues.put("package_name", packageName);
                    contentValues.put("activity_name", launcherClassName);
                    asyncQueryHandler.startInsert(0, null, Uri.parse("content://com.sonymobile.home.resourceprovider/badge"), contentValues);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(Badge.class.getSimpleName(), " 使用索尼系统官方给出方法失败");
                    try {
                        //网上大部分使用方法
                        Intent intent = new Intent("com.sonyericsson.home.action.UPDATE_BADGE");
                        intent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", number > 0);
                        intent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME", launcherClassName);
                        intent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", String.valueOf(number > 0 ? number : ""));
                        intent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", context.getPackageName());
                        context.sendBroadcast(intent);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        Log.i(Badge.class.getSimpleName(), " 使用索尼网上大部分使用方法失败");
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
                Log.i(Badge.class.getSimpleName(), " 非索尼系统，不支持圆点显示");
            }
        }
    }

}
