package com.android.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.R;

/**
 * Created by Ice on 2016/6/29.
 * 通知工具
 */
public class Notification {

    //装信息来的时候的消息声音文件夹
    public final String MUSIC_FOLDER = "NotificationMusic";
    //点击通知的Action
    public static final String ACTION_NOTIFICATION_CLICK = "ACTION_NOTIFICATION_CLICK";
    //删除通知的Action
    public static final String ACTION_NOTIFICATION_DELETE = "ACTION_NOTIFICATION_DELETE";
    //通知栏ID [1-100]的随机数
    public static final String NOTIFICATION_ID = "NOTIFICATION_ID";
    //通知栏类型
    public static final String NOTIFICATION_TYPE = "NOTIFICATION_TYPE";

    //一般类型的通知
    public static final int NOTIFICATION_TYPE_NORMAL = 1;
    //默认的带有精度条的
    public static final int NOTIFICATION_TYPE_PROGRESS_BAR_DEFAULT = 2;
    //自定义的带有精度条的
    public static final int NOTIFICATION_TYPE_PROGRESS_BAR_DEFINE = 3;

    public final Context context;
    public final int id;
    public final int ico;
    public final String tick;
    public final String title;
    public final String content;
    public final boolean define;
    public final boolean vibrate;
    public final String sound;
    public final int progress;
    public final OnNotificationListener listener;

    //获取通知管理器对象
    private NotificationManager mNotificationManager;
    //取消/点击通知之后的通知
    private NotificationReceiver receiver;
    //自定义布局
    private RemoteViews remoteViews;
    //删除意图、内容意图
    private PendingIntent deletePendingIntent;
    private PendingIntent contentPendingIntent;

    private NotificationCompat.Builder compatBuilder;
    private android.app.Notification.Builder version26Builder;
    private android.app.Notification sysNotification;


    public Notification(Builder builder) {
        this.context = builder.context;
        this.id = builder.id;
        this.tick = builder.tick;
        this.title = builder.title;
        this.content = builder.content;
        this.ico = builder.ico;
        this.vibrate = builder.vibrate;
        this.sound = builder.sound;
        this.define = builder.define;
        this.progress = builder.progress;
        this.listener = builder.listener;
        //注册广播
        unregisterNotificationReceiver(context);
        receiver = new NotificationReceiver(this);
        receiver.setOnNotificationListener(listener);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_NOTIFICATION_CLICK);
        filter.addAction(ACTION_NOTIFICATION_DELETE);
        context.registerReceiver(receiver, filter);
        //删除的意图
        Intent deleteIntent = new Intent(ACTION_NOTIFICATION_DELETE);
        deleteIntent.putExtra(NOTIFICATION_ID, id);
        deleteIntent.putExtra(NOTIFICATION_TYPE, NOTIFICATION_TYPE_NORMAL);
        deletePendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, deleteIntent, 0);
        //构建一个Intent
        Intent resultIntent = new Intent(ACTION_NOTIFICATION_CLICK);
        resultIntent.putExtra(NOTIFICATION_ID, id);
        resultIntent.putExtra(NOTIFICATION_TYPE, NOTIFICATION_TYPE_NORMAL);
        //封装一个Intent
        contentPendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, resultIntent, 0);
        //创建通知栏
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            version26Builder = define ? buildVersion26HighDefine() : buildVersionHigh26();
            sysNotification = version26Builder.build();
        } else {
            compatBuilder = define ? buildVersion26LowDefine() : buildVersion26Low();
            sysNotification = compatBuilder.build();
        }
        getNotificationManager().notify(id, sysNotification);
    }

    public static class Builder {

        private Context context;
        private int ico;
        private int id = (int) (Math.random() * 10000000) + 259438;
        private String tick;
        private String title;
        private String content;
        private boolean vibrate = false;
        private String sound;
        private boolean define = false;
        private int progress = -1;

        private OnNotificationListener listener;

        public Builder(Context context) {
            this.context = context;
        }

        public int id() {
            return id;
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public int getIco() {
            return ico;
        }

        public Builder ico(@DrawableRes int ico) {
            this.ico = ico;
            return this;
        }

        public String tick() {
            return title;
        }

        public Builder tick(String tick) {
            this.tick = tick;
            return this;
        }

        public String title() {
            return title;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public String content() {
            return content;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Context context() {
            return context;
        }

        public Builder context(Context context) {
            this.context = context;
            return this;
        }

        public OnNotificationListener listener() {
            return listener;
        }

        public Builder listener(OnNotificationListener listener) {
            this.listener = listener;
            return this;
        }

        public boolean vibrate() {
            return vibrate;
        }

        public Builder vibrate(boolean vibrate) {
            this.vibrate = vibrate;
            return this;
        }

        public boolean define() {
            return define;
        }

        public Builder define(boolean define) {
            this.define = define;
            return this;
        }

        public String sound() {
            return sound;
        }

        public Builder sound(String sound) {
            this.sound = sound;
            return this;
        }

        public int progress() {
            return progress;
        }

        public Builder progress(int progress) {
            this.progress = progress;
            return this;
        }

        public Notification build() {
            return new Notification(this);
        }
    }

    /**
     * 创建26版本以下的通知栏
     *
     * @return
     */
    public NotificationCompat.Builder buildVersion26Low() {
        Bitmap btm = BitmapFactory.decodeResource(context.getResources(), ico);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(ico).setContentTitle(title).setContentText(content);
        mBuilder.setLargeIcon(btm);//设置图标
        mBuilder.setTicker(tick);//第一次提示消息的时候显示在通知栏上
        mBuilder.setNumber(1);////设置通知集合的数量
        if (progress != -1) {
            mBuilder.setProgress(100, progress, false);
        }
        mBuilder.setWhen(System.currentTimeMillis());//显示时间
        //设置提示方式
        if (vibrate && sound != null) {
            mBuilder.setDefaults(android.app.Notification.DEFAULT_VIBRATE);
        }
        if (!vibrate && sound == null) {
            mBuilder.setDefaults(android.app.Notification.DEFAULT_SOUND);
        }
        if (vibrate && sound == null) {
            mBuilder.setDefaults(android.app.Notification.DEFAULT_ALL);
        }
        if (sound != null) {
            mBuilder.setSound(Uri.parse(App.openFileFromAssets(sound, MUSIC_FOLDER).getAbsolutePath()));
        }
        mBuilder.setAutoCancel(true);//自己维护通知的消失
        //删除的意图
        mBuilder.setDeleteIntent(deletePendingIntent);
        // 设置通知主题的意图
        mBuilder.setContentIntent(contentPendingIntent);
        return mBuilder;
    }


    /**
     * 自定义26版本一下的通知栏
     *
     * @return
     */
    public NotificationCompat.Builder buildVersion26LowDefine() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        if (ico != 0) {
            mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), ico));
            mBuilder.setSmallIcon(ico);
        } else {
            Log.e(this.getClass().getSimpleName(), " buildUpdateNotification ico is null!");
        }
        //自定义布局
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.android_update_notification);
        remoteViews.setImageViewResource(R.id.elvenImgvIcon, ico);
        remoteViews.setTextViewText(R.id.elvenTvTitle, title);
        if (progress != -1) {
            remoteViews.setProgressBar(R.id.elvenProgressbar, 100, progress, false);
        }
        remoteViews.setTextViewText(R.id.elvenTvContent, content);
        mBuilder.setContent(remoteViews);
        //其他参数设置
        mBuilder.setTicker(tick);//第一次提示消息的时候显示在通知栏上
        mBuilder.setNumber(1);//设置通知集合的数量
        mBuilder.setWhen(System.currentTimeMillis());//显示时间
        mBuilder.setAutoCancel(true);//自己维护通知的消失
        //设置提示方式
        if (vibrate && sound != null) {
            mBuilder.setDefaults(android.app.Notification.DEFAULT_VIBRATE);
        }
        if (!vibrate && sound == null) {
            mBuilder.setDefaults(android.app.Notification.DEFAULT_SOUND);
        }
        if (vibrate && sound == null) {
            mBuilder.setDefaults(android.app.Notification.DEFAULT_ALL);
        }
        if (sound != null) {
            mBuilder.setSound(Uri.parse(App.openFileFromAssets(sound, MUSIC_FOLDER).getAbsolutePath()));
        }
        // 设置通知主题的意图
        mBuilder.setContentIntent(contentPendingIntent);
        //删除的意图
        mBuilder.setDeleteIntent(deletePendingIntent);
        return mBuilder;
    }


    /**
     * 创建26版本以上的通知栏
     *
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public android.app.Notification.Builder buildVersionHigh26() {
        NotificationChannel mChannel = new NotificationChannel(id + "", context.getPackageName(), NotificationManager.IMPORTANCE_LOW);
        getNotificationManager().createNotificationChannel(mChannel);
        android.app.Notification.Builder builder = new android.app.Notification.Builder(context);
        builder.setChannelId(id + "");
        builder.setTicker(tick);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setSmallIcon(ico);
        builder.setNumber(1);////设置通知集合的数量
        if (progress != -1) {
            builder.setProgress(100, progress, false);
        }
        builder.setWhen(System.currentTimeMillis());//显示时间
        builder.setDeleteIntent(deletePendingIntent);
        builder.setContentIntent(contentPendingIntent);
        builder.setAutoCancel(true);//自己维护通知的消失
        //设置提示方式
        if (vibrate && sound != null) {
            builder.setDefaults(android.app.Notification.DEFAULT_VIBRATE);
        }
        if (!vibrate && sound == null) {
            builder.setDefaults(android.app.Notification.DEFAULT_SOUND);
        }
        if (vibrate && sound == null) {
            builder.setDefaults(android.app.Notification.DEFAULT_ALL);
        }
        if (sound != null) {
            builder.setSound(Uri.parse(App.openFileFromAssets(sound, MUSIC_FOLDER).getAbsolutePath()));
        }
        return builder;
    }


    /**
     * 自定义26版本以上的通知栏
     *
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public android.app.Notification.Builder buildVersion26HighDefine() {
        NotificationChannel mChannel = new NotificationChannel(id + "", context.getPackageName(), NotificationManager.IMPORTANCE_LOW);
        getNotificationManager().createNotificationChannel(mChannel);
        android.app.Notification.Builder mBuilder = new android.app.Notification.Builder(context);
        if (ico != 0) {
            mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), ico));
            mBuilder.setSmallIcon(ico);
        } else {
            Log.e(this.getClass().getSimpleName(), " buildUpdateNotification ico is null!");
        }
        //自定义布局
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.android_update_notification);
        remoteViews.setImageViewResource(R.id.elvenImgvIcon, ico);
        remoteViews.setTextViewText(R.id.elvenTvTitle, title);
        if (progress != -1) {
            remoteViews.setProgressBar(R.id.elvenProgressbar, 100, progress, false);
        }
        remoteViews.setTextViewText(R.id.elvenTvContent, content);
        mBuilder.setContent(remoteViews);
        //其他参数设置
        mBuilder.setTicker(tick);//第一次提示消息的时候显示在通知栏上
        mBuilder.setNumber(1);//设置通知集合的数量
        mBuilder.setWhen(System.currentTimeMillis());//显示时间
        mBuilder.setAutoCancel(true);//自己维护通知的消失
        //设置提示方式
        if (vibrate && sound != null) {
            mBuilder.setDefaults(android.app.Notification.DEFAULT_VIBRATE);
        }
        if (!vibrate && sound == null) {
            mBuilder.setDefaults(android.app.Notification.DEFAULT_SOUND);
        }
        if (vibrate && sound == null) {
            mBuilder.setDefaults(android.app.Notification.DEFAULT_ALL);
        }
        if (sound != null) {
            mBuilder.setSound(Uri.parse(App.openFileFromAssets(sound, MUSIC_FOLDER).getAbsolutePath()));
        }
        // 设置通知主题的意图
        mBuilder.setContentIntent(contentPendingIntent);
        //删除的意图
        mBuilder.setDeleteIntent(deletePendingIntent);
        return mBuilder;
    }

    public void notify(int id) {
        getNotificationManager().notify(id, getSystemNotification());
    }


    /**
     * 摧毁通知栏点击Receiver[注意:在不需要用的时候，或者用完的时候就把Receiver进行摧毁操作]
     *
     * @param context
     */
    public void unregisterNotificationReceiver(Context context) {
        if (receiver != null) {
            context.unregisterReceiver(receiver);
        }
    }

    /**
     * 获取远程Views
     *
     * @return
     */
    public RemoteViews getRemoteViews() {
        return remoteViews;
    }

    public Notification progress(int progress) {

        getNotificationManager().notify(id, sysNotification);
        return this;
    }

    public android.app.Notification getSystemNotification() {
        if (sysNotification != null) {
            return sysNotification;
        }
        if (compatBuilder != null) {
            compatBuilder.setProgress(100, progress, false);
            sysNotification = compatBuilder.build();
        }
        if (version26Builder != null) {
            version26Builder.setProgress(100, progress, false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                sysNotification = version26Builder.build();
            }
        }
        return sysNotification;
    }

    /**
     * 获取NotificationManager
     *
     * @return
     */
    public NotificationManager getNotificationManager() {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mNotificationManager;
    }


    /**
     * 清除通知
     *
     * @param id
     */
    public void clear(int id) {
        Log.i(this.getClass().getSimpleName(), "clear notification by id");
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        mNotificationManager.cancel(id);
    }

    /**
     * 清除所有通知
     */
    public void clearAll() {
        Log.i(this.getClass().getSimpleName(), "clear all notification");
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        mNotificationManager.cancelAll();
    }

}
