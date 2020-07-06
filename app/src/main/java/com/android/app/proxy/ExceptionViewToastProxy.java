package com.android.app.proxy;

import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.android.app.AppConstant;
import com.android.app.page.BaseActivity;
import com.android.app.page.BaseFragment;
import com.android.app.view.ExceptionView;

/**
 * Created by Relin</br>
 * on 2018-09-21.</br>
 * 异常提示框</br>
 * 该类和ExceptionViewPageProxy类功能是一样滴，</br>
 * 支持自定义异常，只是这个类是弹提示来提示用户。</br>
 */
public class ExceptionViewToastProxy implements ExceptionView {

    private BaseActivity aty;
    private BaseFragment fgt;

    @Override
    public void onCreateExceptionView(Object obj, LayoutInflater inflater, FrameLayout parent) {
        if (obj instanceof BaseActivity) {
            aty = (BaseActivity) obj;
        }
        if (obj instanceof BaseFragment) {
            fgt = (BaseFragment) obj;
        }
    }

    @Override
    public void showExceptionView(FrameLayout parent, String msg) {
        if (fgt != null) {
            if (msg.contains(AppConstant.HTTP_MSG_NET_OFFLINE)) {
                fgt.showToast(AppConstant.EXCEPTION_MSG_NET_OFFLINE);
            } else {
                fgt.showToast(msg);
            }
        }
        if (aty != null) {
            if (msg.contains(AppConstant.HTTP_MSG_NET_OFFLINE)) {
                aty.showToast(AppConstant.EXCEPTION_MSG_NET_OFFLINE);
            } else {
                aty.showToast(msg);
            }
        }
    }

    @Override
    public void dismissExceptionView(FrameLayout parent) {

    }

}
