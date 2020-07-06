package com.android.app.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.R;
import com.android.utils.Screen;

/**
 * @author RelinRan
 * @date 2018-10-12.
 * @description 提示框可以设置提示内容、按钮文字，<br/>
 * 监听按钮的点击事件。<br/>
 */
public class AlertDialog {

    /**
     * 上下文对象
     */
    private final Context context;

    /**
     * 提示内容
     */
    private final String msg;

    /**
     * 取消按钮文字
     */
    private final String cancel;

    /**
     * 确认按钮文字
     */
    private final String confirm;

    /**
     * 内容文字颜色
     */
    private final int msgColor;

    /**
     * 取消按钮文字颜色
     */
    private final int cancelColor;

    /**
     * 确认按钮文字颜色
     */
    private final int confirmColor;

    /**
     * 提示框宽度
     */
    private final int width;

    /**
     * 背景是否半透明
     */
    private boolean translucent;

    /**
     * 提示框监听
     */
    private final OnAlertDialogListener listener;

    /**
     * 对话框对象
     */
    private Dialog dialog;

    /**
     * 构造函数
     *
     * @param builder 构造器
     */
    public AlertDialog(Builder builder) {
        this.context = builder.context;
        this.msg = builder.msg;
        this.confirm = builder.confirm;
        this.cancel = builder.cancel;
        this.msgColor = builder.msgColor;
        this.cancelColor = builder.cancelColor;
        this.confirmColor = builder.confirmColor;
        this.width = builder.width;
        this.translucent = builder.translucent;
        this.listener = builder.listener;
        createDialog();
        show();
    }

    /**
     * 创建对话框
     */
    private void createDialog() {
        dialog = new Dialog.Builder(context)
                .width(width)
                .height(LinearLayout.LayoutParams.WRAP_CONTENT)
                .layoutResId(cancel == null ? R.layout.android_dialog_alert_confirm : R.layout.android_dialog_alert_selector)
                .animResId(R.style.android_anim_zoom)
                .themeResId(translucent ? R.style.Android_Theme_Dialog_Translucent_Background : R.style.Android_Theme_Dialog_Transparent_Background)
                .gravity(Gravity.CENTER)
                .build();
    }

    /**
     * 构造器
     */
    public static class Builder {

        /**
         * 上下文对象
         */
        private Context context;

        /**
         * 提示内容
         */
        private String msg;

        /**
         * 确认按钮文字
         */
        private String confirm;

        /**
         * 取消按钮文字
         */
        private String cancel;

        /**
         * 内容文字颜色
         */
        private int msgColor = 0;

        /**
         * 取消按钮文字颜色
         */
        private int cancelColor = 0;

        /**
         * 确认按钮文字颜色
         */
        private int confirmColor = 0;

        /**
         * 提示框宽度
         */
        private int width = (int) (Screen.width() * 0.70F);

        /**
         * 背景是否半透明
         */
        private boolean translucent;

        /**
         * 提示框监听
         */
        private OnAlertDialogListener listener;

        /**
         * 构造函数
         *
         * @param context 上下文
         */
        public Builder(Context context) {
            this.context = context;
        }

        /**
         * 设置消息内容
         *
         * @param msg 消息内容
         * @return Builder
         */
        public Builder msg(String msg) {
            this.msg = msg;
            return this;
        }

        /**
         * 设置确认按钮文字
         *
         * @param confirm 确认按钮文字
         * @return Builder
         */
        public Builder confirm(String confirm) {
            this.confirm = confirm;
            return this;
        }

        /**
         * 设置取消按钮文字
         *
         * @param cancel 取消按钮文字
         * @return Builder
         */
        public Builder cancel(String cancel) {
            this.cancel = cancel;
            return this;
        }

        /**
         * 设置显示宽度
         *
         * @param width 宽度
         * @return Builder
         */
        public Builder width(int width) {
            this.width = width;
            return this;
        }

        /**
         * 设置消息内容颜色
         *
         * @param msgColor 消息内容颜色（16进制）
         * @return Builder
         */
        public Builder msgColor(int msgColor) {
            this.msgColor = msgColor;
            return this;
        }

        /**
         * 设置取消按钮颜色
         *
         * @param cancelColor 取消按钮颜色（16进制）
         * @return Builder
         */
        public Builder cancelColor(int cancelColor) {
            this.cancelColor = cancelColor;
            return this;
        }

        /**
         * 设置确认按钮颜色
         *
         * @param confirmColor 确认按钮颜色（16进制）
         * @return Builder
         */
        public Builder confirmColor(int confirmColor) {
            this.confirmColor = confirmColor;
            return this;
        }

        /**
         * 设置提示框监听
         *
         * @param listener 提示框监听
         * @return Builder
         */
        public Builder listener(OnAlertDialogListener listener) {
            this.listener = listener;
            return this;
        }

        /**
         * 设置是否半透明背景
         *
         * @param translucent 是否半透明背景
         * @return
         */
        public Builder translucent(boolean translucent) {
            this.translucent = translucent;
            return this;
        }

        /**
         * 创建提示框对象
         *
         * @return AlertDialog
         */
        public AlertDialog build() {
            return new AlertDialog(this);
        }
    }

    /**
     * 返回对话框对象
     *
     * @return
     */
    public Dialog dialog() {
        return dialog;
    }

    /**
     * 隐藏对话框对象
     */
    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    /**
     * 显示选择对话框
     */
    public Dialog show() {
        doDialog(dialog);
        dialog.show();
        return dialog;
    }

    /**
     * 处理对话框逻辑
     *
     * @param dialog 对话框
     */
    private void doDialog(final Dialog dialog) {
        TextView tv_content = dialog.contentView.findViewById(R.id.dialog_content);
        TextView dialog_ok = dialog.contentView.findViewById(R.id.dialog_ok);
        tv_content.setText(msg);
        if (msgColor != 0) {
            tv_content.setTextColor(msgColor);
        }
        if (confirmColor != 0) {
            dialog_ok.setTextColor(confirmColor);
        }
        if (cancel != null) {
            TextView dialog_cancel = dialog.contentView.findViewById(R.id.dialog_cancel);
            if (cancelColor != 0) {
                dialog_cancel.setTextColor(cancelColor);
            }
            dialog_cancel.setText(cancel);
            dialog_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onAlertDialogCancel(dialog.dialog);
                    }
                    dialog.dismiss();
                }
            });

        }
        dialog_ok.setText(confirm);
        dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onAlertDialogConfirm(dialog.dialog);
                }
                dialog.dismiss();
            }
        });
    }

    /**
     * 提示对话框监听
     */
    public interface OnAlertDialogListener {

        /**
         * 对话框点击取消
         *
         * @param dialog 对话框
         */
        void onAlertDialogCancel(android.app.Dialog dialog);

        /**
         * 对话框点击确认
         *
         * @param dialog 对话框
         */
        void onAlertDialogConfirm(android.app.Dialog dialog);

    }

}
