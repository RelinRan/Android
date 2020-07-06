package com.android.app.proxy;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.R;
import com.android.app.mode.LoadingMode;
import com.android.utils.Screen;
import com.android.view.SwipeRequestView;

/**
 * Created by Relin
 * on 2018/4/26.
 * 加载动画 - SwipeLoading<br/>
 * 主要是数据加载的时候的SwipeLoading，用户需要自定义的话就实现<br/>
 * LoadingView接口重写LoadingView@onCreateLoadingFrameAnimation();<br/>
 */

public class LoadingViewSwipeHintProxy extends LoadingViewFrameProxy {

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
                loadingView = inflater.inflate(R.layout.android_swipe_hint_dialog_loadding, parent, false);
                break;
            case CONTENT:
                loadingView = inflater.inflate(R.layout.android_swipe_hint_content_loadding, parent, false);
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
        final TextView tv_loading = loadingView.findViewById(R.id.tv_loading);
        if (!TextUtils.isEmpty(toast)) {
            tv_loading.setText(toast);
        }
        srv = loadingView.findViewById(R.id.android_srv);
        srv.setArcRadius(Screen.dpToPx(12));
        srv.setShadowColor(Color.parseColor("#FFFFFF"));
        srv.setOnSchemeColorChangeListener(new SwipeRequestView.OnSchemeColorChangeListener() {
            @Override
            public void onSchemeColor(int[] colors, int position) {
                tv_loading.setTextColor(colors[position]);
            }
        });

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
