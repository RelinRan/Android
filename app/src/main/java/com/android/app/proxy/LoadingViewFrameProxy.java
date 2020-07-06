package com.android.app.proxy;

import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.R;
import com.android.app.mode.LoadingMode;
import com.android.app.view.LoadingView;

/**
 * Created by Relin <br/>
 * on 2018-09-21.<br/>
 * 加载动画 - 帧动画<br/>
 * 主要是数据加载的时候的帧动画，用户需要自定义的话就实现<br/>
 * LoadingView接口重写@LoadingView#{onCreateLoadingFrameAnimation()};<br/>
 */
public class LoadingViewFrameProxy implements LoadingView {

    protected View loadingView;
    protected AnimationDrawable animationDrawable;

    @Override
    public AnimationDrawable onCreateLoadingFrameAnimation(View loadingView, int loadingImageViewId) {
        ImageView loadingImageView = loadingView.findViewById(loadingImageViewId);
        return (AnimationDrawable) loadingImageView.getBackground();
    }

    @Override
    public RotateAnimation onCreateLoadingRotateAnimation(View loadingView, int loadingImageViewId) {
        ImageView loadingImageView = loadingView.findViewById(loadingImageViewId);
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1100);
        rotateAnimation.setRepeatCount(-1);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        loadingImageView.setAnimation(rotateAnimation);
        rotateAnimation.start();
        return rotateAnimation;
    }

    @Override
    public void showLoadingView(LayoutInflater inflater, FrameLayout parent, LoadingMode mode, String toast) {
        if (loadingView != null) {
            parent.removeView(loadingView);
        }
        switch (mode) {
            case DIALOG:
                loadingView = inflater.inflate(R.layout.android_dialog_loadding, null);
                break;
            case CONTENT:
                loadingView = inflater.inflate(R.layout.android_content_loadding, null);
                loadingView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                break;
            default:
                Log.e(this.getClass().getSimpleName(), "LoadingMode is LoadingMode.DIALOG or LoadingMode.CONTENT");
                break;
        }
        TextView tv_toast = loadingView.findViewById(R.id.tv_loading_toast);
        if (TextUtils.isEmpty(toast)) {
            tv_toast.setVisibility(View.GONE);
        } else {
            tv_toast.setText(toast);
        }
        animationDrawable = onCreateLoadingFrameAnimation(loadingView, R.id.iv_loading);
        parent.addView(loadingView);
        animationDrawable.start();
    }

    @Override
    public void dismissLoadingView(FrameLayout parent) {
        if (loadingView == null) {
            return;
        }
        if (parent.getParent() != null) {
            parent.removeView(loadingView);
        }
    }

    @Override
    public void onDestroy() {
        loadingView = null;
        animationDrawable = null;
    }
}
