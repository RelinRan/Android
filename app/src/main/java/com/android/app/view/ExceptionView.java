package com.android.app.view;

import android.view.LayoutInflater;
import android.widget.FrameLayout;

/**
 * Created by Relin
 * on 2018-09-21.<br/>
 * 创建错误视图接口，你可以通过接口
 * 创建属于自己的错误显示的视图。<br/>
 * Create error view interface.
 * You can create your own error view through the interface
 */
public interface ExceptionView {

    /**
     * 创建错误视图
     *
     * @param obj      Activity或者Fragment
     * @param inflater 布局转换器
     * @param parent   异常视图父控件
     * @return
     */
    void onCreateExceptionView(Object obj, LayoutInflater inflater, FrameLayout parent);

    /**
     * 显示异常视图
     *
     * @param parent    异常视图父控件
     * @param exception 异常信息
     */
    void showExceptionView(FrameLayout parent, String exception);

    /**
     * 消失异常视图
     *
     * @param parent 异常视图父控件
     */
    void dismissExceptionView(FrameLayout parent);
}
