package com.android.app.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Relin
 * on 2018-07-17.<br/>
 * 你可以通过此接口创建页面的
 * 基本元素，包含自定义导航栏、内容区域<br/>
 * You can use this interface to create basic
 * elements of the page, including custom navigation and content areas
 */

public interface NavigationBarView {

    /**
     * 创建标题栏
     *
     * @param inflater           布局装换器
     * @param frameView          框架布局
     * @param titleContainerView 标题栏容器
     * @param titleBarViewId     标题栏id
     * @param titleBarLayoutId   标题栏id
     */
    void onCreateNavigationBarView(LayoutInflater inflater, View frameView, ViewGroup titleContainerView, int titleBarViewId, int titleBarLayoutId);

    /**
     * 设置标题可见性
     *
     * @param visibility              可见性
     * @param defineNavigationBarView 自定义标题栏视图
     */
    void setNavigationBarVisibility(int visibility, View defineNavigationBarView);

    /**
     * 销毁所有引用对象
     */
    void onDestroy();
    
}
