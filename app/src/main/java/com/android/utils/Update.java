package com.android.utils;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.R;
import com.android.app.dialog.AlertDialog;
import com.android.app.dialog.Dialog;
import com.android.io.Downloader;
import com.android.io.OnDownloadListener;

import java.io.File;

/**
 * Created by Relin
 * on 2016/6/29.
 * APP更新工具<br/>
 * 如果使用Service更新app,必须注册UpdateService服务类<br/>
 * 如果使用Dialog更新app,只需要show()方法<br/>
 */
public class Update {

    public static final String APK_URL = "APP_URL";
    public static final String APK_PATH = "APK_PATH";
    public static final String APP_ICON_RES_ID = "APP_ICON_RES_ID";
    public static final String APP_NAME = "APP_NAME";
    public static final String IS_SHOW_DEFAULT = "IS_SHOW_DEFAULT";

    /**
     * 获取版本号
     *
     * @return
     */
    public static int getVersionCode(Context context) {
        return App.getVersionCode(context);
    }

    /**
     * 获取版本名称
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        return App.getVersionName(context);
    }

    /**
     * 显示更新对话框
     *
     * @param context
     * @param appIconResId APP图标
     * @param appName      APP名称
     * @param apkUrl       APP下载地址
     */
    public static void show(final Context context, final int appIconResId, final String appName, final String apkUrl, String updateInfo, final boolean isShowDefault) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.msg(updateInfo);
        builder.confirm("立即更新");
        builder.cancel("取消更新");
        builder.translucent(true);
        builder.listener(new AlertDialog.OnAlertDialogListener() {
            @Override
            public void onAlertDialogCancel(android.app.Dialog dialog) {

            }

            @Override
            public void onAlertDialogConfirm(android.app.Dialog dialog) {
                Intent intent = new Intent(context, UpdateService.class);
                intent.putExtra(APK_URL, apkUrl);
                intent.putExtra(APP_ICON_RES_ID, appIconResId);
                intent.putExtra(APP_NAME, appName);
                intent.putExtra(IS_SHOW_DEFAULT, isShowDefault);
                context.startService(intent);
                dialog.dismiss();
            }
        });
        builder.build().show();
    }


    /**
     * 显示对话框
     *
     * @param context 上下文对象
     * @param message 提示信息
     * @param url     下载url
     */
    public static void show(final Context context, String message, final String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.msg(message);
        builder.confirm("更新");
        builder.cancel("取消");
        builder.translucent(true);
        builder.listener(new AlertDialog.OnAlertDialogListener() {
            @Override
            public void onAlertDialogCancel(android.app.Dialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void onAlertDialogConfirm(android.app.Dialog dialog) {
                download(context, url);
            }
        });
        builder.build().show();
    }


    /**
     * 显示下载对话框
     *
     * @param context 上下文对象
     * @param url     下载地址
     */
    public static void download(final Context context, String url) {
        Dialog.Builder builder = new Dialog.Builder(context);
        builder.gravity(Gravity.CENTER);
        builder.width(LinearLayout.LayoutParams.MATCH_PARENT);
        builder.height(LinearLayout.LayoutParams.MATCH_PARENT);
        builder.themeResId(com.android.R.style.Android_Theme_Dialog_Translucent_Background);
        builder.layoutResId(R.layout.android_dialog_update);
        builder.canceledOnTouchOutside(false);
        builder.cancelable(false);
        final Dialog dialog = builder.build();
        final ProgressBar pbr_download = (ProgressBar) dialog.contentView.findViewById(R.id.android_pbr_download);
        final TextView tv_percent = (TextView) dialog.contentView.findViewById(R.id.android_tv_percent);
        Downloader.Builder downloadBuilder = new Downloader.Builder();
        downloadBuilder.url(url);
        downloadBuilder.listener(new OnDownloadListener() {
            @Override
            public void onDownloading(long l, long l1, int i) {
                pbr_download.setProgress(i);
                tv_percent.setText(i + "%");
            }

            @Override
            public void onDownloadCompleted(File file) {
                dialog.dismiss();
                App.installApk(context, file);
            }

            @Override
            public void onDownloadFailed(Exception e) {
                dialog.dismiss();
            }
        });
        downloadBuilder.build();
        dialog.show();
    }


}
