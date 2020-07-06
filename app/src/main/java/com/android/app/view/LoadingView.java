package com.android.app.view;

import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;

import com.android.app.mode.LoadingMode;

/**
 * Created by Relin
 * on 2018-09-21.<br/>
 * 你可以通过此接口创建一个数据
 * 正在加载一个加载视图.<br/>
 * You can use this interface to
 * create a data that is loading a load view
 */
public interface LoadingView {

    /**
     * 创建对应的加载帧动画
     *
     * @param loadingView        加载视图
     * @param loadingImageViewId 加载视图的图片
     * @return
     */
    AnimationDrawable onCreateLoadingFrameAnimation(View loadingView, int loadingImageViewId);

    /**
     * 创建对应的加载旋转动画
     *
     * @param loadingView        加载视图
     * @param loadingImageViewId 加载视图的图片
     * @return
     */
    RotateAnimation onCreateLoadingRotateAnimation(View loadingView, int loadingImageViewId);

    /***
     * 显示加载视图
     * @param inflater    布局转换器
     * @param parent        加载视图的父控件
     * @param mode        加载视图的模式
     * @param toast        加载视图的提示
     */
    void showLoadingView(LayoutInflater inflater, FrameLayout parent, LoadingMode mode, String toast);

    /**
     * 消失加载视图
     *
     * @param parent 加载视图父控件
     */
    void dismissLoadingView(FrameLayout parent);

    /**
     * 摧毁数据
     */
    void onDestroy();

}
