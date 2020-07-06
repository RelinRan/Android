package com.android.app.proxy;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.R;
import com.android.app.AppConstant;
import com.android.view.ActionView;
import com.android.view.ShapeButton;

/**
 * Created by Relin on 2018-09-21.</br>
 * 异常视图代理</br>
 * 主要处理app异常了显示对应的视图，用户需要自定义异常的话，</br>
 * 需要实现ExceptionView接口。</br>
 */
public class ExceptionViewPageProxy extends BaseExceptionViewPageProxy implements View.OnClickListener {

    /**
     * 动作显示
     */
    private ActionView actionView;
    /**
     * 错误提示
     */
    private TextView errorHint;
    /**
     * 错误按钮
     */
    private ShapeButton errorButton;
    /**
     * 提示信息
     */
    protected final String ERROR_HINT_TEXT = "，请稍后再试";

    @Override
    public int setExceptionLayoutById() {
        return R.layout.android_exception;
    }

    @Override
    public void onCreateExceptionView(Object obj, LayoutInflater inflater, FrameLayout parent) {
        super.onCreateExceptionView(obj,inflater,parent);
        getExceptionView().setOnClickListener(this);
        actionView = getExceptionView().findViewById(R.id.android_action_view);
        actionView.setType(ActionView.WIRELESS);
        errorHint = getExceptionView().findViewById(R.id.android_tv_error_hint);
        errorButton = getExceptionView().findViewById(R.id.android_btn_loading);
        errorButton.setOnClickListener(this);
    }

    @Override
    public void showExceptionView(FrameLayout parent, String msg) {
        errorHint.setText(msg + ERROR_HINT_TEXT);
        if (msg.equals(AppConstant.EXCEPTION_MSG_NET_OFFLINE)){
            actionView.setType(ActionView.WIRELESS);
        }else{
            actionView.setType(ActionView.EXCLAMATION);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.android_btn_loading) {
            if (getFgt() != null) {
                getFgt().onHttpRequest();
            }
            if (getAty() != null) {
                getAty().onHttpRequest();
            }
        }
    }

}
