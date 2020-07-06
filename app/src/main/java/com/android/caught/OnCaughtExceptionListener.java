package com.android.caught;

import java.io.File;

/**
 * Created by Relin on 2016/9/6.
 * 保存错误日志的监听
 */

public interface OnCaughtExceptionListener {

    /**
     * 保存错误日志成功回调函数
     *
     * @param file 保存错误日志的文件
     */
    void onCaughtExceptionSucceed(File file);

    /**
     * 保存错误日志失败的回调函数
     *
     * @param error 错误信息
     */
    void onCaughtExceptionFailure(String error);

}
