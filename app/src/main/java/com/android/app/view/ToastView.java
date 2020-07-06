package com.android.app.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.android.app.mode.ToastMode;

/**
 * Created by Relin
 * on 2018-07-18.<br/>
 * 你可以通过此接口创建页面提示信息的视图<br/>
 * You can use this interface to
 * create a view of the page prompt message
 */
public interface ToastView {

    /**
     * 显示提示
     *
     * @param context 上下文
     * @param msg     提示内容
     * @return
     */
    Toast showToast(Context context, String msg);

    /**
     * 显示自定义提示
     *
     * @param context
     * @param inflater
     * @param mode     提示类型
     * @param msg      提示内容
     * @return
     */
    Toast showToast(Context context, LayoutInflater inflater, ToastMode mode, String msg);

}
