package com.android.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Ice on 2017/8/5.
 */

public class NotificationReceiver extends BroadcastReceiver {

    private Notification notification;
    private Object object;
    private OnNotificationListener listener;

    public NotificationReceiver(Notification notification) {
        this.notification = notification;
    }

    public NotificationReceiver(Notification notification, Object object) {
        this.object = object;
        this.notification = notification;
    }

    public void setOnNotificationListener(OnNotificationListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Notification.ACTION_NOTIFICATION_CLICK)) {
            Log.i(this.getClass().getSimpleName(), "NotificationReceiver ACTION_NOTIFICATION_CLICK");
            if (listener != null) {
                listener.onNotificationClick(notification, intent, object);
            }
            notification.unregisterNotificationReceiver(context);
        }
        if (intent.getAction().equals(Notification.ACTION_NOTIFICATION_DELETE)) {
            int type = intent.getIntExtra(Notification.NOTIFICATION_TYPE, Notification.NOTIFICATION_TYPE_NORMAL);
            int id = intent.getIntExtra(Notification.NOTIFICATION_ID,0);
            Log.i(this.getClass().getSimpleName(), "NotificationReceiver ACTION_NOTIFICATION_DELETE type:"+type);
            UpdateService service = (UpdateService) object;
            if (service!=null){
                service.cancelUpdate();
                notification.clear(id);
            }
            if (listener != null) {
                listener.onNotificationCancel(notification, intent, object);
            }
            notification.unregisterNotificationReceiver(context);
        }
    }

}