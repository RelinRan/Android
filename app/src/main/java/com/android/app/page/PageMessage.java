package com.android.app.page;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.app.AppConstant;
import com.android.net.HttpCode;

/**
 * Created by Relin
 * on 2018-09-21.
 * 页面消息通知
 * 主要利用Handler处理数据类更新UI
 */
public class PageMessage extends Handler {

    /**
     * 数据对象
     */
    private Object object;

    /**
     * 页面数据构造函数
     *
     * @param obj 数据对象
     */
    public PageMessage(Object obj) {
        this.object = obj;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        String message = msg.getData().getString(AppConstant.MSG_KEY);
        String code = msg.getData().getString(AppConstant.MSG_CODE);
        if (code == null || code.equals("null") || code.length() == 0) {
            code = "0";
        }
        switch (msg.what) {
            case AppConstant.WHAT_MSG_NET_OFFLINE:
                Log.e(this.getClass().getSimpleName(), AppConstant.HTTP_MSG_NET_OFFLINE);
                if (object instanceof BaseActivity) {
                    BaseActivity activity = (BaseActivity) object;
                    activity.dismissLoadingDialog();
                    activity.showExceptionDialog(message);
                }
                if (object instanceof BaseFragment) {
                    BaseFragment baseFragment = (BaseFragment) object;
                    baseFragment.dismissLoadingDialog();
                    baseFragment.showExceptionDialog(message);
                }
                break;
            case AppConstant.WHAT_MSG_RESPONSE_FAILED:
                Log.e(this.getClass().getSimpleName(), AppConstant.HTTP_MSG_RESPONSE_FAILED + code);
                if (object instanceof BaseActivity) {
                    BaseActivity activity = (BaseActivity) object;
                    activity.dismissLoadingDialog();
                    activity.showExceptionDialog(HttpCode.parseCode(Integer.parseInt(code)));
                }
                if (object instanceof BaseFragment) {
                    BaseFragment baseFragment = (BaseFragment) object;
                    baseFragment.dismissLoadingDialog();
                    baseFragment.showExceptionDialog(HttpCode.parseCode(Integer.parseInt(code)));
                }
                break;
        }
    }

}
