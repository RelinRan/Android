package com.android.utils;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.android.R;
import com.android.io.Downloader;
import com.android.io.OnDownloadListener;

import java.io.File;

/**
 * Created by Relin on 2016/6/29.
 * 更新服务
 */
public class UpdateService extends Service implements OnDownloadListener {

    private final String TAG = this.getClass().getSimpleName();
    private String appName;
    private int appIconResId;
    private String apkUrl;
    private int notificationId = 520;
    private boolean isShowDefaultNotification;
    private Notification.Builder builder;
    private Notification notification;
    private Downloader downloader;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        apkUrl = intent.getStringExtra(Update.APK_URL);
        appIconResId = intent.getIntExtra(Update.APP_ICON_RES_ID, 0);
        appName = intent.getStringExtra(Update.APP_NAME);
        isShowDefaultNotification = intent.getBooleanExtra(Update.IS_SHOW_DEFAULT, true);

        builder = new Notification.Builder(this);
        builder.tick(appName + "有更新");
        builder.id(notificationId);
        builder.title(appName);
        builder.ico(appIconResId);
        builder.progress(0);
        builder.define(false);
        builder.content("最新安装包下载进度0%");
        builder.build();

        //下载对应Apk文件
        downloadUpdateFile(apkUrl);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new UpdateBinder();
    }

    @Override
    public void onDownloading(long total, long progress, int percent) {
        updateDownloadValue(percent);
    }

    @Override
    public void onDownloadCompleted(File file) {
        Notification notification = builder.build();
        notification.clear(builder.id());
        App.installApk(UpdateService.this, file);
    }

    @Override
    public void onDownloadFailed(Exception e) {

    }


    public class UpdateBinder extends Binder {
        public UpdateBinder() {
            super();
        }
    }

    /**
     * 取消更新
     */
    public void cancelUpdate() {
        if (downloader != null) {
            Log.i(this.getClass().getSimpleName(), "cancelUpdate");
            downloader.cancel();
        }
    }

    /**
     * 下载更新文件
     *
     * @param url 文件url
     */
    public void downloadUpdateFile(String url) {
        downloader = new Downloader.Builder()
                .url(url).name(appName + ".apk")
                .listener(this)
                .build();
        downloader.start();
        Log.i(getResources().getString(R.string.frame_name), this.getClass().getSimpleName() + " downloadUpdateFile url:" + url);
    }

    /**
     * 更新下载值
     *
     * @param progress
     */
    long current = 0;

    private void updateDownloadValue(final int progress) {
        if (System.currentTimeMillis() - current < 1000) {
            return;
        }
        builder = new Notification.Builder(this);
        builder.id(notificationId);
        builder.tick(appName + "有更新");
        builder.title(appName);
        builder.ico(appIconResId);
        builder.progress(progress);
        builder.define(false);
        builder.content("最新安装包下载进度" + progress + "%");
        builder.build();
        current = System.currentTimeMillis();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        notification.unregisterNotificationReceiver(getApplicationContext());
    }

}
