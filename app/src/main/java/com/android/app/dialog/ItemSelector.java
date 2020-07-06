package com.android.app.dialog;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.R;
import com.android.date.OnDateSelectListener;
import com.android.utils.DateUtils;
import com.android.utils.ListUtils;
import com.android.view.LoopView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ItemSelector {


    /**
     * 上下文
     */
    public final Context context;

    /**
     * 显示对象
     */
    private Dialog dialog;

    /**
     * 标题颜色
     */
    public final int titleBarBackgroundColor;

    /**
     * 标题字体颜色
     */
    public final int titleBarCancelTextColor;

    /**
     * 标题字体颜色
     */
    public final int titleBarConfirmTextColor;

    /**
     * 标题栏字体大小
     */
    public final int titleBarTextSize;

    /**
     * 分割线颜色
     */
    public final int dividerColor;

    /**
     * 选择颜色
     */
    public final int selectedColor;

    /**
     * 未选中颜色
     */
    public final int unselectedColor;

    /**
     * 字体大小
     */
    public final int textSize;

    /**
     * 背景师范半透明
     */
    public final boolean translucent;
    /**
     * 日期选中回调函数
     */
    public final OnItemSelectListener listener;

    public final ArrayList<String> list;

    public final String[] items;


    public ItemSelector(Builder builder) {
        this.context = builder.context;
        this.translucent = builder.translucent;
        this.titleBarBackgroundColor = builder.titleBarBackgroundColor;
        this.titleBarCancelTextColor = builder.titleBarCancelTextColor;
        this.titleBarConfirmTextColor = builder.titleBarConfirmTextColor;
        this.titleBarTextSize = builder.titleBarTextSize;
        this.dividerColor = builder.dividerColor;
        this.selectedColor = builder.selectedColor;
        this.unselectedColor = builder.unselectedColor;
        this.textSize = builder.textSize;
        this.listener = builder.listener;
        this.items = builder.items;
        if (items != null) {
            builder.list = new ArrayList<>();
            for (int i = 0; i < items.length; i++) {
                builder.list.add(items[i]);
            }
        }
        this.list = builder.list;
        createDialog(context, listener);
        show();
    }

    public static class Builder {

        private Context context;

        private boolean translucent = true;

        private int titleBarBackgroundColor = Color.parseColor("#F2F2F2");

        private int titleBarCancelTextColor = Color.parseColor("#454545");

        private int titleBarConfirmTextColor = Color.parseColor("#0EB692");

        private int titleBarTextSize = 16;

        private int dividerColor = Color.parseColor("#CDCDCD");

        private int selectedColor = Color.parseColor("#0EB692");

        private int unselectedColor = Color.parseColor("#AEAEAE");

        private int textSize = 16;

        private OnItemSelectListener listener;

        private ArrayList<String> list;

        private String[] items;

        public Builder(Context context) {
            this.context = context;
        }

        public boolean isTranslucent() {
            return translucent;
        }

        public Builder translucent(boolean translucent) {
            this.translucent = translucent;
            return this;
        }

        public Context context() {
            return context;
        }

        public int titleBarBackgroundColor() {
            return titleBarBackgroundColor;
        }

        public Builder titleBarBackgroundColor(int titleBarBackgroundColor) {
            this.titleBarBackgroundColor = titleBarBackgroundColor;
            return this;
        }

        public int titleBarCancelTextColor() {
            return titleBarCancelTextColor;
        }

        public Builder titleBarCancelTextColor(int titleBarCancelTextColor) {
            this.titleBarCancelTextColor = titleBarCancelTextColor;
            return this;
        }

        public int titleBarConfirmTextColor() {
            return titleBarConfirmTextColor;
        }

        public Builder titleBarConfirmTextColor(int titleBarConfirmTextColor) {
            this.titleBarConfirmTextColor = titleBarConfirmTextColor;
            return this;
        }

        public int titleBarTextSize() {
            return titleBarTextSize;
        }

        public Builder titleBarTextSize(int titleBarTextSize) {
            this.titleBarTextSize = titleBarTextSize;
            return this;
        }

        public int dividerColor() {
            return dividerColor;
        }

        public Builder dividerColor(int dividerColor) {
            this.dividerColor = dividerColor;
            return this;
        }

        public int selectedColor() {
            return selectedColor;
        }

        public Builder selectedColor(int selectedColor) {
            this.selectedColor = selectedColor;
            return this;
        }

        public int unselectedColor() {
            return unselectedColor;
        }

        public Builder unselectedColor(int unselectedColor) {
            this.unselectedColor = unselectedColor;
            return this;
        }

        public int textSize() {
            return textSize;
        }

        public Builder textSize(int textSize) {
            this.textSize = textSize;
            return this;
        }

        public OnItemSelectListener listener() {
            return listener;
        }

        public Builder listener(OnItemSelectListener listener) {
            this.listener = listener;
            return this;
        }

        public ArrayList<String> list() {
            return list;
        }

        public Builder list(ArrayList<String> list) {
            this.list = list;
            return this;
        }

        public String[] items() {
            return items;
        }

        public Builder items(String[] items) {
            this.items = items;
            return this;
        }

        public ItemSelector build() {
            return new ItemSelector(this);
        }
    }

    /**
     * 显示
     */
    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    /**
     * 消失
     */
    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }


    /**
     * 显示日期选择器
     *
     * @param context  上下文
     * @param listener 选择监听
     * @return
     */
    private void createDialog(Context context, final OnItemSelectListener listener) {
        dialog = new Dialog.Builder(context)
                .width(LinearLayout.LayoutParams.MATCH_PARENT)
                .height(LinearLayout.LayoutParams.WRAP_CONTENT)
                .layoutResId(R.layout.android_item_selector)
                .animResId(R.style.android_anim_bottom)
                .themeResId(translucent ? R.style.Android_Theme_Dialog_Translucent_Background : R.style.Android_Theme_Dialog_Transparent_Background)
                .gravity(Gravity.BOTTOM)
                .build();
        LinearLayout ll_bar = dialog.contentView.findViewById(R.id.ll_bar);
        TextView tv_cancel = dialog.contentView.findViewById(R.id.tv_cancel);
        TextView tv_complete = dialog.contentView.findViewById(R.id.tv_complete);
        final LoopView lv_loop = dialog.contentView.findViewById(R.id.lv_loop);

        ll_bar.setBackgroundColor(titleBarBackgroundColor);
        tv_cancel.setTextColor(titleBarCancelTextColor);
        tv_complete.setTextColor(titleBarConfirmTextColor);
        tv_cancel.setTextSize(titleBarTextSize);
        tv_complete.setTextSize(titleBarTextSize);

        lv_loop.setDividerColor(dividerColor);
        lv_loop.setCenterTextColor(selectedColor);
        lv_loop.setOuterTextColor(unselectedColor);
        lv_loop.setTextSize(textSize);

        if (list != null) {
            lv_loop.setItems(list);
        }
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog.dialog == null) {
                    return;
                }
                dialog.dialog.dismiss();
            }
        });
        tv_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && ListUtils.getSize(list) != 0) {
                    listener.onItemSelect(list.get(lv_loop.getSelectedItem()), lv_loop.getSelectedItem());
                }
                dialog.dialog.dismiss();
            }
        });
    }

}
