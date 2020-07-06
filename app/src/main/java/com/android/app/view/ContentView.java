package com.android.app.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

/**
 * Created by Relin
 * on 2018-10-12.
 */
public interface ContentView {


    /**
     * 创建内容区域
     *
     * @param context
     * @param inflater        布局转换器
     * @param parent          框架容器
     * @param contentLayoutId 的内容布局[setContentLayoutById()]
     * @param type            的内容布局类型 0-> Activity , 1->Fragment
     * @param name
     */
    void onCreateContentView(Context context, LayoutInflater inflater, FrameLayout parent, int contentLayoutId, int type, String name);

    void onDestroy();

}
