package com.android.app.proxy;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.R;
import com.android.app.BaseApplication;
import com.android.app.dialog.Dialog;
import com.android.app.view.ContentView;
import com.android.net.HttpResponse;
import com.android.utils.ListUtils;
import com.android.utils.Screen;
import com.android.view.DragTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Relin
 * on 2018-10-12.
 * 内容布局的代理者
 */
public class ContentViewProxy implements ContentView, View.OnClickListener {

    /**
     * 上下文
     */
    private Context context;

    /**
     * 调试标题
     */
    private String debugTitle;

    /**
     * 网络请求结果
     */
    private List<HttpResponse> responses;

    /**
     * 可拖动的View
     */
    private DragTextView android_debug_btn;

    /**
     * 对话框
     */
    private Dialog dialog;

    /**
     * 调试内容显示区域
     */
    private TextView android_debug_content;

    /**
     * 内容付控件
     */
    private FrameLayout parent;

    @Override
    public void onCreateContentView(Context context, LayoutInflater inflater, FrameLayout parent, int contentLayoutId, int type, String name) {
        this.parent = parent;
        this.context = context;
        parent.removeAllViews();
        View contentLayout = inflater.inflate(contentLayoutId, parent, false);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        parent.addView(contentLayout, params);
        //调试
        if (BaseApplication.app.isShowDebug() && context != null) {
            debugTitle = name;
            addDebugView(parent, type, name);
        }
        contentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(this.getClass().getSimpleName(), "You are clicking contentLayoutId.");
            }
        });
    }

    /**
     * 添加调试视图
     *
     * @param parent 父控件
     * @param type   类型 0:Fragment 1:Activity
     * @param name   拖动控件显示名称
     */
    private void addDebugView(FrameLayout parent, int type, String name) {
        if (android_debug_btn == null && context != null) {
            android_debug_btn = new DragTextView(context);
            android_debug_btn.setTextSize(Screen.spToPx(5));
            android_debug_btn.setGravity(Gravity.CENTER);
            android_debug_btn.setBackgroundResource(R.drawable.android_debug_btn_bg);
            android_debug_btn.setTextColor(context.getResources().getColor(R.color.color_white));
            android_debug_btn.setText(name);
            int padding = (int) Screen.dpToPx(5);
            android_debug_btn.setPadding(padding, padding, padding, padding);
        }
        //添加视图到主页面
        FrameLayout.LayoutParams debugParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        debugParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        debugParams.rightMargin = (int) Screen.dpToPx(10);
        debugParams.bottomMargin = (int) (type == 0 ? Screen.dpToPx(120) : Screen.dpToPx(160));
        android_debug_btn.setLayoutParams(debugParams);
        android_debug_btn.setOnClickListener(this);
        if (parent != null && android_debug_btn != null) {
            parent.addView(android_debug_btn, debugParams);
        }
    }

    @Override
    public void onClick(View v) {
        if (android_debug_btn != null && android_debug_btn.isDrag()) {
            return;
        }
        if (dialog == null) {
            Dialog.Builder builder = new Dialog.Builder(context);
            builder.gravity(Gravity.BOTTOM);
            builder.width(LinearLayout.LayoutParams.MATCH_PARENT);
            builder.height(LinearLayout.LayoutParams.WRAP_CONTENT);
            builder.animResId(com.android.R.style.android_anim_bottom);
            builder.themeResId(com.android.R.style.Android_Theme_Dialog_Translucent_Background);
            builder.layoutResId(R.layout.android_dialog_debug);
            builder.animResId(R.style.android_anim_bottom);
            builder.canceledOnTouchOutside(false);
            builder.cancelable(false);
            dialog = builder.build();
            TextView android_debug_title = dialog.contentView.findViewById(R.id.android_debug_title);
            android_debug_content = dialog.contentView.findViewById(R.id.android_debug_content);
            ImageView android_debug_close = dialog.contentView.findViewById(R.id.android_debug_close);
            android_debug_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            android_debug_title.setText(debugTitle);
        }
        if (android_debug_content != null) {
            android_debug_content.setText(getAllHttpResponse().toString());
        }
        dialog.show();
    }

    /**
     * 设置网络请求结果
     *
     * @param response 请求结果
     */
    public void setHttpResponse(HttpResponse response) {
        if (responses == null) {
            responses = new ArrayList<>();
        }
        responses.add(response);
    }

    /**
     * 获取请求结果数据
     *
     * @return
     */
    private StringBuffer getAllHttpResponse() {
        StringBuffer sb = new StringBuffer("");
        int size = ListUtils.getSize(responses);
        for (int i = 0; i < size; i++) {
            HttpResponse response = responses.get(i);
            if (response != null) {
                sb.append(response.url());
                sb.append(System.getProperty("line.separator"));
                sb.append("code:" + response.code());
                sb.append(System.getProperty("line.separator"));
                //参数
                StringBuffer paramsBuffer = new StringBuffer("");
                if (response.requestParams().getStringParams() != null) {
                    for (String key : response.requestParams().getStringParams().keySet()) {
                        paramsBuffer.append("\"" + key + "\":" + "\"" + response.requestParams().getStringParams().get(key) + "\"");
                        paramsBuffer.append(System.getProperty("line.separator"));
                    }
                }
                if (response.requestParams().getStringBody() != null) {
                    paramsBuffer.append(response.requestParams().getStringBody());
                    paramsBuffer.append(",");
                    paramsBuffer.append("\"" + response.requestParams().getStringBody() + "\"");
                    paramsBuffer.append(System.getProperty("line.separator"));
                }
                if (response.requestParams().getFileParams() != null) {
                    for (String key : response.requestParams().getFileParams().keySet()) {
                        paramsBuffer.append("\"" + key + "\":" + "\"" + response.requestParams().getFileParams().get(key).getAbsolutePath() + "\"");
                        paramsBuffer.append(System.getProperty("line.separator"));
                    }
                }
                sb.append(paramsBuffer);
                //结果
                if (response.body() != null) {
                    if (response.body().startsWith("{")) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            sb.append(jsonObject.toString(4));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (response.body().startsWith("[")) {
                        try {
                            JSONArray jsonArray = new JSONArray(response.body());
                            sb.append(jsonArray.toString(4));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (i != size - 1 && size != 1) {
                        sb.append(System.getProperty("line.separator"));
                    }
                }
                sb.append(System.getProperty("line.separator"));
            }
        }
        return sb;
    }

    /**
     * 清空数据
     */
    public void onDestroy() {
        context = null;
        if (responses != null) {
            responses.clear();
        }
        responses = null;
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        if (parent != null) {
            parent.removeAllViews();
        }
    }

}
