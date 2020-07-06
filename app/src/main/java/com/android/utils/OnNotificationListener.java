package com.android.utils;

import android.content.Intent;

/**
 * Created by Renlin
 * on 2017/8/5.
 */

public interface OnNotificationListener {

    /**
     * 通知栏点击事件
     *
     * @param notificationUtils 通知栏工具
     * @param intent            通知栏事件的intent
     * @param obj               传递过来的Object对象
     */
    void onNotificationClick(Notification notificationUtils, Intent intent, Object obj);

    /**
     * 通知栏点击事件
     *
     * @param notificationUtils 通知栏工具
     * @param intent            通知栏事件的intent
     * @param obj               传递过来的Object对象
     */
    void onNotificationCancel(Notification notificationUtils, Intent intent, Object obj);

}
