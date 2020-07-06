package com.android.app.proxy;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.android.R;
import com.android.app.mode.LoadingMode;
import com.android.utils.Screen;
import com.android.view.SwipeRequestView;

/**
 * Created by Relin
 * on 2018/4/26.
 * app应用控件架构视图
 */

public class LoadingViewSwipeProxy extends LoadingViewFrameProxy {

    private final static int DISMISS_LOADING_VIEW_DELAY_TIME = 0;
    private final static int SHOW_LOADING_VIEW_DELAY_TIME = 0;
    private final static int WHAT_DISMISS_LOADING_VIEW = 1;
    private final static int WHAT_SHOW_LOADING_VIEW = 2;

    private FrameLayout loadingViewParent;
    protected SwipeRequestView srv;

    @Override
    public void showLoadingView(LayoutInflater inflater, FrameLayout parent, LoadingMode mode, String toast) {
        if (loadingView != null) {
            parent.removeView(loadingView);
        }
        switch (mode) {
            case DIALOG:
                loadingView = inflater.inflate(R.layout.android_swipe_dialog_loadding, parent, false);
                break;
            case CONTENT:
                loadingView = inflater.inflate(R.layout.android_swipe_content_loadding, parent, false);
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
        parent.addView(loadingView);
        loadingViewParent = parent;
        srv = loadingView.findViewById(R.id.android_srv);
        srv.setArcRadius(Screen.dpToPx(8));
        srv.setShadowColor(Color.parseColor("#25808080"));
        handler.sendEmptyMessageDelayed(WHAT_SHOW_LOADING_VIEW, SHOW_LOADING_VIEW_DELAY_TIME);
    }


    @Override
    public void dismissLoadingView(FrameLayout parent) {
        loadingViewParent = parent;
        if (loadingView == null) {
            return;
        }
        if (loadingView.getParent() != null) {
            handler.sendEmptyMessageDelayed(WHAT_DISMISS_LOADING_VIEW, DISMISS_LOADING_VIEW_DELAY_TIME);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        loadingViewParent = null;
        srv = null;
        handler.removeCallbacksAndMessages(null);
        handler = null;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (srv == null) {
                return;
            }
            switch (msg.what) {
                case WHAT_SHOW_LOADING_VIEW:
                    srv.start();
                    break;
                case WHAT_DISMISS_LOADING_VIEW:
                    loadingViewParent.removeView(loadingView);
                    if (srv != null) {
                        srv.cancel();
                    }
                    break;
            }
        }
    };


}
