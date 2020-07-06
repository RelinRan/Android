package com.android.io;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.android.net.OkHttp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Relin
 * on 2018/11/21.<br/>
 * 下载助手可以帮助你简单的调用函数进行下载，同时如果你需要自定义下载网络的方式，<br/>
 * 只需要继承该类然后重写download方法，在获取到文件流之后调用doHttpResponse（）<br/>
 * 处理对应逻辑就行了。<br/>
 * download assistant can help you simply calling a function for download, at the same time,< br / >
 * if you need a custom download network way, < br / >
 * only needs to inherit the class and then rewrite the download method,< br / >
 * after the access to file stream call doHttpResponse () < br / >
 * handle corresponding logic. < br / >
 */
public class Downloader {

    /**
     * 缓存文件夹
     */
    public static final String CACHE_FOLDER = "Downloader";

    public static final int WHAT_DOWNLOADING = 0x001;
    public static final int WHAT_DOWNLOAD_COMPLETED = 0x002;
    public static final int WHAT_DOWNLOAD_FAILED = 0x003;

    /**
     * 总的大小
     */
    private long totalSize = 0;

    /**
     * 是否取消
     */
    private boolean isCancel;

    /**
     * 是否暂停
     */
    private boolean isPause;

    /**
     * 是否在下载中
     */
    private boolean isDownloading;

    /**
     * 资源地址
     */
    public final String url;
    /**
     * 文件名称
     */
    public final String name;
    /**
     * 缓存文件夹
     */
    public final String folder;
    /**
     * 实发支持断点下载
     */
    public final boolean isBreakpoint;
    /**
     * 下载监听
     */
    public OnDownloadListener onDownloadListener;


    public Downloader(Builder builder) {
        this.url = builder.url;
        this.name = builder.name;
        this.folder = builder.folder;
        this.isBreakpoint = builder.isBreakpoint;
        this.onDownloadListener = builder.onDownloadListener;
        start();
    }

    public static class Builder {
        private String url;
        private String name;
        private String folder;
        private boolean isBreakpoint;
        private OnDownloadListener onDownloadListener;


        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder folder(String folder) {
            this.folder = folder;
            return this;
        }

        public Builder isBreakpoint(boolean isBreakpoint) {
            this.isBreakpoint = isBreakpoint;
            return this;
        }

        public Builder listener(OnDownloadListener onDownloadListener) {
            this.onDownloadListener = onDownloadListener;
            return this;
        }

        public Downloader build() {
            return new Downloader(this);
        }
    }

    protected boolean isBreakpoint() {
        return isBreakpoint;
    }

    protected boolean isPause() {
        return isPause;
    }

    protected boolean isCancel() {
        return isCancel;
    }

    protected boolean isDownloading() {
        return isDownloading;
    }

    public void setDownloading(boolean downloading) {
        this.isDownloading = downloading;
    }

    /**
     * 开始下载
     */
    public void start() {
        isPause = false;
        isCancel = false;
        if (!isDownloading) {
            download();
        }
    }

    /**
     * 暂停下载
     */
    public void pause() {
        isPause = true;
    }

    /**
     * 取消下载
     */
    public void cancel() {
        isCancel = true;
    }

    /**
     * 销毁下载
     */
    public void destory() {
        cancel();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    /**
     * 创建文件
     *
     * @param url 资源地址
     * @return
     */
    protected File createFile(String url) {
        File cacheFolder = new File(new IOUtils().createFolder(folder == null ? CACHE_FOLDER : folder));
        if (cacheFolder.isDirectory() && !cacheFolder.exists()) {
            cacheFolder.mkdirs();
        }
        return new File(cacheFolder.getAbsolutePath() + File.separator + (name == null ? createName(url) : name));
    }

    /**
     * 创建Url文件名称
     *
     * @param url 资源地址
     * @return
     */
    private String createName(String url) {
        if (url.contains("/") && url.contains(".")) {
            return url.substring(url.lastIndexOf("/") + 1);
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        return format.format(format) + ".zip";
    }

    /**
     * 是否存在
     *
     * @param url
     * @return
     */
    public boolean isExistFile(String url) {
        File file = createFile(url);
        return file.exists();
    }

    /**
     * 下载文件
     */
    private void download() {
        if (TextUtils.isEmpty(url)) {
            sendFailedMsg(new IOException("File download network address is empty."));
            return;
        }
        if (!url.toUpperCase().startsWith("HTTP")) {
            sendFailedMsg(new IOException("File download address error, unable to download normal."));
            return;
        }
        setDownloading(true);
        download(url);
    }

    protected void download(final String url) {
        final long downloadedLength = calculateDownloadedLength(url);
        Request request = new Request.Builder()
                .addHeader("User-Agent", "Android")
                .header("Content-Type", "text/html; charset=utf-8;")
                .addHeader("RANGE", "bytes=" + downloadedLength + "-")
                .url(url)
                .build();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttp.setHttpsSetting(builder);
        OkHttpClient okHttpClient = builder.build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailedMsg(e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                doHttpResponse(response.body().byteStream(), response.body().contentLength(), downloadedLength, createFile(url));
            }
        });
    }

    /**
     * 计算已经下载过的文件大小
     *
     * @param url
     * @return
     */
    private long calculateDownloadedLength(String url) {
        File file = createFile(url);
        if (file.exists()) {
            if (isBreakpoint()) {
                return file.length();
            } else {
                file.delete();
            }
        }
        return 0;
    }

    /**
     * 处理服务器返回数据
     */
    protected void doHttpResponse(InputStream is, long contentLength, long downloadedLength, File file) {
        long downloading = 0;
        byte[] buf = new byte[2048];
        int len;
        RandomAccessFile randomAccessFile = null;
        try {
            if (downloadedLength == 0) {
                totalSize = contentLength;
            } else {
                totalSize = downloadedLength + contentLength;
            }
            if (totalSize == downloadedLength) {
                //已下载字节和文件总字节相等，说明下载已经完成了
                sendCompletedMsg(file);
                return;
            }
            if (totalSize == 0) {
                if (downloadedLength == 0) {
                    sendFailedMsg(new IOException("The file length value is 0 and cannot be downloaded properly"));
                } else {
                    if (isBreakpoint()) {
                        sendCompletedMsg(file);
                    } else {
                        file.delete();
                    }
                }
                return;
            }
            randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(downloadedLength);
            while ((len = is.read(buf)) != -1) {
                if (isPause() || isCancel()) {
                    break;
                }
                randomAccessFile.write(buf, 0, len);
                downloading += len;
                long downSum = downloading + downloadedLength;
                //传递更新信息
                int percentage = (int) ((downloadedLength + downloading) * 100 / totalSize);
                sendDownloadingMsg(totalSize, downSum, percentage);
            }
            randomAccessFile.close();
            sendCompletedMsg(file);
        } catch (Exception e) {
            sendFailedMsg(e);
        } finally {
            setDownloading(false);
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                sendFailedMsg(e);
            }
            try {
                if (randomAccessFile != null)
                    randomAccessFile.close();
            } catch (IOException e) {
                sendFailedMsg(e);
            }
        }
    }

    /**
     * 发送成功的信息
     *
     * @param file
     */
    protected void sendCompletedMsg(File file) {
        Message msg = handler.obtainMessage();
        msg.what = WHAT_DOWNLOAD_COMPLETED;
        msg.obj = file;
        handler.sendMessage(msg);
    }


    /**
     * 发送下载失败信息
     *
     * @param e 文件异常
     */
    protected void sendFailedMsg(Exception e) {
        Message msg = handler.obtainMessage();
        msg.what = WHAT_DOWNLOAD_FAILED;
        msg.obj = e;
        handler.sendMessage(msg);
    }

    /**
     * 发送下载信息
     *
     * @param total    文件总大小
     * @param progress 文件进度
     * @param percent  百分比
     */
    protected void sendDownloadingMsg(long total, long progress, int percent) {
        Message message = handler.obtainMessage();
        message.what = WHAT_DOWNLOADING;
        Bundle bundle = new Bundle();
        bundle.putLong("total", total);
        bundle.putLong("progress", progress);
        bundle.putInt("percent", percent);
        message.setData(bundle);
        handler.sendMessage(message);
    }

    /**
     * 下载Handler处理
     */
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (onDownloadListener == null) {
                return;
            }
            Bundle data = msg.getData();
            Object obj = msg.obj;
            switch (msg.what) {
                case WHAT_DOWNLOADING:
                    onDownloadListener.onDownloading(data.getLong("total"), data.getLong("progress"), data.getInt("percent"));
                    break;
                case WHAT_DOWNLOAD_COMPLETED:
                    onDownloadListener.onDownloadCompleted((File) obj);
                    break;
                case WHAT_DOWNLOAD_FAILED:
                    onDownloadListener.onDownloadFailed((Exception) obj);
                    break;
            }
        }
    };

}
