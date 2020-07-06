package com.android.utils;

import android.os.Handler;
import android.os.Message;
import android.view.View;

public class DoubleClickHelper implements View.OnClickListener {

    /**
     * 控件ID
     */
    private int id;

    /**
     * 控件
     */
    private View v;

    /**
     * 点击次数
     */
    private int clickCount = 0;

    /**
     * 双击事件
     */
    private OnDoubleClickListener onDoubleClickListener;

    /**
     * 是否双击
     */
    private boolean isDoubleClick = false;


    /**
     * 双击助手
     *
     * @param v
     */
    public DoubleClickHelper(View v) {
        if (id != v.getId()) {
            clickCount = 0;
        }
        this.v = v;
        this.id = v.getId();
        v.setOnClickListener(this);
    }


    /**
     * 双击事件
     *
     * @param onDoubleClickListener 回调函数
     */
    public void setOnDoubleClickListener(OnDoubleClickListener onDoubleClickListener) {
        this.onDoubleClickListener = onDoubleClickListener;
    }

    @Override
    public void onClick(View v) {
        clickCount++;
        isDoubleClick = clickCount >= 2;
        handler.sendEmptyMessageDelayed(0, 200);
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if (clickCount >= 2) {
                    onDoubleClickListener.onDoubleClick(v);
                } else {
                    if (!isDoubleClick) {
                        onDoubleClickListener.onClick(v);
                    }
                }
                clickCount = 0;
            }
        }
    };

    /**
     * 双击回调
     */
    public interface OnDoubleClickListener {

        /**
         * 双击
         *
         * @param v 控件
         */
        void onDoubleClick(View v);

        /**
         * 点击事件
         *
         * @param v
         */
        void onClick(View v);

    }

}
