package com.android.caught;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.android.io.IOUtils;
import com.android.utils.DateUtils;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Relin
 * on 2016/9/6.
 * 捕捉异常工具
 */
public class CaughtException implements Thread.UncaughtExceptionHandler {

    /**
     * 线程超时操作
     */
    private Future<?> future;

    /**
     * 保存错误日志监听
     */
    private OnCaughtExceptionListener listener;

    /**
     * 保存日志超时线程池
     */
    private ExecutorService threadPool = Executors.newSingleThreadExecutor();

    /**
     * 捕捉异常对象类
     */
    private Thread.UncaughtExceptionHandler exceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

    /**
     * 上下文对象
     */
    public final Context context;

    /**
     * 文件名
     */
    public final String fileName;

    /**
     * 文件夹名称
     */
    public final String folderName;

    /**
     * 异常捕捉
     *
     * @param builder
     */
    public CaughtException(Builder builder) {
        this.context = builder.context;
        this.folderName = builder.folderName;
        this.fileName = builder.fileName;
        this.listener = builder.listener;
        IOUtils.createFolder(folderName);
        start();
    }

    /**
     * 捕捉
     */
    private void start() {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 异常构建者
     */
    public static class Builder {

        /**
         * 上下文对象
         */
        private Context context;

        /**
         * 文件夹名称
         */
        private String folderName = "ExceptionLog";

        /**
         * 日志监听
         */
        private OnCaughtExceptionListener listener;

        /**
         * 日志名称
         */
        private String fileName = DateUtils.now() + ".txt";


        /**
         * 构建者对象
         *
         * @param context
         */
        public Builder(Context context) {
            this.context = context;
        }

        /**
         * 设置文件夹名称
         *
         * @param folderName
         * @return
         */
        public Builder folderName(String folderName) {
            this.folderName = folderName;
            return this;
        }

        /**
         * 设置文件名称
         *
         * @param fileName 文件名称
         * @return
         */
        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        /**
         * 文件监听
         *
         * @param listener
         * @return
         */
        public Builder listener(OnCaughtExceptionListener listener) {
            this.listener = listener;
            return this;
        }

        /**
         * 构建
         *
         * @return
         */
        public CaughtException build() {
            return new CaughtException(this);
        }
    }

    /**
     * 获取奔溃异常
     *
     * @param thread
     * @param ex
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.e(this.getClass().getSimpleName(), obtainDeviceInfo() + "\n uncaughtException :" + ex.toString());
        if (IOUtils.isExistSDCard()) {
            final File file = new File(IOUtils.createFile(folderName, fileName).getAbsolutePath());
            //保存错误的日志
            saveErrorInfo(obtainDeviceInfo(), file, ex);
            future = threadPool.submit(new Runnable() {
                public void run() {
                    if (listener != null) {
                        listener.onCaughtExceptionSucceed(file);
                    }
                }
            });
            if (!future.isDone()) {
                try {
                    future.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            exceptionHandler.uncaughtException(thread, ex);
        } else {
            if (listener != null) {
                listener.onCaughtExceptionFailure("You Sdcard is not exist!");
            }
            Log.e(this.getClass().getSimpleName(), this.getClass().getSimpleName() + " You Sdcard is not exist! ");
        }
    }

    /**
     * 获取设备信息
     *
     * @return
     */
    private String obtainDeviceInfo() {
        StringBuilder sb = new StringBuilder();
        //项目信息
        sb.append("\n[==Application Information==]").append('\n');
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo ai = context.getApplicationInfo();
        //项目名字
        sb.append("Application Name : ").append(packageManager.getApplicationLabel(ai)).append('\n');
        try {
            PackageInfo pi = packageManager.getPackageInfo(ai.packageName, 0);
            //项目版本号
            sb.append("Application Version Code: ").append(pi.versionCode).append('\n');
            //项目版本名
            sb.append("Application Version Name: ").append(pi.versionName).append('\n');
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //设备信息
        sb.append('\n').append("[==Device Information==]").append('\n');
        //手机品牌
        sb.append("Brand: ").append(Build.BRAND).append('\n');
        //设备名
        sb.append("Device: ").append(Build.DEVICE).append('\n');
        //手机版本
        sb.append("Version Code: ").append(Build.DISPLAY).append('\n');
        //指纹
        sb.append("Fingerprint: ").append(Build.FINGERPRINT).append('\n');
        //制造商
        sb.append("Manufacturer: ").append(Build.MANUFACTURER).append('\n');
        //产品名
        sb.append("Product: ").append(Build.PRODUCT).append('\n');
        return sb.toString();
    }

    /**
     * 同步保存错误日志
     *
     * @param deviceInfo 设备信息
     * @param file       保存错误信息的文件
     * @param ex         保存错误信息
     */
    private synchronized void saveErrorInfo(String deviceInfo, File file, Throwable ex) {
        synchronized (file) {
            FileWriter fileWriter = null;
            BufferedWriter bufferedWriter = null;
            PrintWriter printWriter = null;
            try {
                fileWriter = new FileWriter(file);
                bufferedWriter = new BufferedWriter(fileWriter);
                printWriter = new PrintWriter(fileWriter);
                //拼接应用、设备信息和错误Log
                bufferedWriter.append(DateUtils.now()).append(" ").append("Error").append('/').append("LogUtils").append(" ").append(deviceInfo).append("\n [==Error information==]\n").append(ex.toString());
                bufferedWriter.flush();
                ex.printStackTrace(printWriter);
                printWriter.flush();
                fileWriter.flush();
            } catch (IOException e) {
                if (listener != null) {
                    listener.onCaughtExceptionFailure(e.toString());
                }
                e.printStackTrace();
                closeWriter(fileWriter);
                closeWriter(bufferedWriter);
                closeWriter(printWriter);
            }
        }
    }

    /**
     * 关闭写入流通道
     *
     * @param closeable
     */
    private void closeWriter(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ioe) {
                if (listener != null) {
                    listener.onCaughtExceptionFailure(ioe.toString());
                }
                Log.i(this.getClass().getSimpleName(), CaughtException.class.getClass().getSimpleName() + " " + ioe.toString());
            }
        }
    }

}
