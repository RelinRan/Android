package com.android.app.proxy;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.R;
import com.android.app.mode.ToastMode;
import com.android.app.view.ToastView;
import com.android.utils.Screen;
import com.android.view.ActionView;

/**
 * Created by Relin<br/>
 * on 2018-07-18.<br/>
 * 提示文字代理<br/>
 * 用户如果需要显示自己的自定义提示的话就实现<br/>
 * ToastView接口<br/>
 */
public class ToastViewProxy implements ToastView {

    @Override
    public Toast showToast(Context context, String msg) {
        Toast toast = new Toast(context);
        View view = LayoutInflater.from(context).inflate(R.layout.android_toast_system, null);
        toast.setView(view);
        toast.setGravity(Gravity.BOTTOM, 0, (int) Screen.dpToPx(70));
        TextView tv_toast = view.findViewById(R.id.android_tv_toast);
        tv_toast.setText(msg);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
        return toast;
    }

    @Override
    public Toast showToast(Context context, LayoutInflater inflater, ToastMode mode, String msg) {
        Toast toast = new Toast(context);
        View view = inflater.inflate(R.layout.android_toast_action, null);
        TextView tv_toast = view.findViewById(R.id.android_tv_toast);
        ActionView action = view.findViewById(R.id.android_action);
        switch (mode) {
            case SUCCEED:
                action.setType(ActionView.TICK);
                break;
            case FAILURE:
                action.setType(ActionView.EXCLAMATION);
                break;
            case NOT_NET:
                action.setType(ActionView.WIRELESS);
                break;
        }
        tv_toast.setText(msg);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
        return toast;
    }


}
