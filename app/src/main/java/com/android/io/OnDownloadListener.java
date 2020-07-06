package com.android.io;

import java.io.File;

/**
 * Created by Relin
 * on 2018-11-21.
 * 文件下载监听，通过此监听可以监听文件下载过程中<br/>
 * 文件总大小、文件进度，文件是否下载完成，文件下载错误。<br/>
 * file download the listening, through this monitoring <br/>
 * can monitor the file download process files total size, <br/>
 * schedule, and if the file download is complete, the file download errors.
 */
public interface OnDownloadListener {


    /**
     * 文件下载过程监听
     *
     * @param total    文件总大小
     * @param progress 下载中大小
     * @param percent  百分比
     */
    void onDownloading(long total, long progress, int percent);

    /**
     * 文件下载完成
     *
     * @param file 下载完成的文件
     */
    void onDownloadCompleted(File file);

    /**
     * 下载失败
     *
     * @param e 文件异常信息
     */
    void onDownloadFailed(Exception e);

}
