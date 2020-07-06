package com.android.app.proxy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.android.app.page.BaseActivity;
import com.android.app.page.BaseFragment;
import com.android.app.view.ExceptionView;

/**
 * 基础异常视图代理
 */
public abstract class BaseExceptionViewPageProxy implements ExceptionView {

    /**
     * Activity页面
     */
    private BaseActivity aty;
    /**
     * Fragment页面
     */
    private BaseFragment fgt;

    /**
     * 上下文对象
     */
    private Context context;

    /**
     * 异常视图对象
     */
    private View exceptionView;

    /**
     * 设置异常视图的布局
     *
     * @return
     */
    public abstract int setExceptionLayoutById();

    @Override
    public void onCreateExceptionView(Object obj, LayoutInflater inflater, FrameLayout parent) {
        if (obj instanceof BaseActivity) {
            aty = (BaseActivity) obj;
            context = aty;
        }
        if (obj instanceof BaseFragment) {
            fgt = (BaseFragment) obj;
            context = fgt.getContext();
        }
        if (parent == null) {
            return;
        }
        exceptionView = inflater.inflate(setExceptionLayoutById(), null);
    }

    @Override
    public void showExceptionView(FrameLayout parent, String exception) {
        if (exceptionView == null) {
            return;
        }
        if (exceptionView.getParent() != null) {
            parent.removeView(exceptionView);
        }
        parent.addView(exceptionView);
    }

    @Override
    public void dismissExceptionView(FrameLayout parent) {
        if (exceptionView == null) {
            return;
        }
        if (exceptionView.getParent() != null) {
            parent.removeView(exceptionView);
        }
    }

    public BaseActivity getAty() {
        return aty;
    }

    public void setAty(BaseActivity aty) {
        this.aty = aty;
    }

    public BaseFragment getFgt() {
        return fgt;
    }

    public void setFgt(BaseFragment fgt) {
        this.fgt = fgt;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public View getExceptionView() {
        return exceptionView;
    }

    public void setExceptionView(View exceptionView) {
        this.exceptionView = exceptionView;
    }

}
