package com.android.utils;

import android.app.Instrumentation;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.android.widget.RecyclerAdapter;


/**
 * 焦点助手
 */
public class FocusHelper {

    /**
     * 刷新Adapter延迟时间
     */
    public static int DELAY_ADAPTER_NOTIFY = 100;

    /**
     * 刷新焦点延迟时间
     */
    public static int DELAY_REQUEST_FOCUS = 100;

    /**
     * 点击监听回调
     */
    public interface OnFocusClickListener {

        /**
         * 获取焦点控件点击回调
         *
         * @param v
         */
        void onFocusClick(View v);

    }

    /**
     * 添加点击监听
     *
     * @param v        焦点控件
     * @param listener 点击回调
     */
    public static void addFocusClickListener(final View v, final OnFocusClickListener listener) {
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int code, KeyEvent keyEvent) {
                if ((code == KeyEvent.KEYCODE_ENTER || code == KeyEvent.KEYCODE_DPAD_CENTER) && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (listener != null) {
                        listener.onFocusClick(v);
                    }
                }
                return false;
            }
        });
    }

    /**
     * Adapter焦点刷新
     *
     * @param adapter  Adapter
     * @param position 刷新位置
     */
    public static void notify(final RecyclerAdapter adapter, final int position) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.notifyItemChanged(position);
            }
        }, DELAY_ADAPTER_NOTIFY);
    }

    /**
     * 触发下一个焦点
     */
    public static void nextFocus() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Instrumentation inst = new Instrumentation();
                inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_RIGHT);
            }
        }.start();
    }

    /**
     * 请求焦点
     *
     * @param recyclerView RecyclerView
     * @param position     位置
     * @param id           控件id
     */
    public static void requestFocus(final RecyclerView recyclerView, final int position, final @IdRes int id) {
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                View itemView = recyclerView.getLayoutManager().findViewByPosition(position);
                if (itemView != null && itemView.findViewById(id) != null) {
                    ImageView iv_stroke = (ImageView) itemView.findViewById(id);
                    if (iv_stroke != null) {
                        iv_stroke.requestFocus();
                    }
                }
            }
        }, DELAY_REQUEST_FOCUS);
    }

}