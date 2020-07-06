package com.android.date;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.R;
import com.android.app.dialog.Dialog;
import com.android.utils.DateUtils;
import com.android.view.LoopView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DateSelector {

    /**
     * 选择日期
     */
    public static final int TYPE_DATE = 0x001;

    /**
     * 选择时间
     */
    public static final int TYPE_TIME = 0x002;

    /**
     * 选择日期时间
     */
    public static final int TYPE_DATE_TIME = 0x003;

    /**
     * 选择日期年月
     */
    public static final int TYPE_DATE_YY_MM = 0x004;

    /**
     * 选择小时分钟
     */
    public static final int TYPE_TIME_HH_MM = 0x005;

    /**
     * 选择日期年月日时分
     */
    public static final int TYPE_DATE_YY_MM_DD_HH_MM = 0x006;

    /**
     * 选中位置
     */
    private static int todayPosition = 0;

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
     * 选择类型
     */
    public final int type;

    /**
     * 年份
     */
    public final int year;

    /**
     * 年份前面
     */
    public final int yearBefore;

    /**
     * 年份后边
     */
    public final int yearBehind;

    /**
     * 月份
     */
    public final int month;

    /**
     * 天数
     */
    public final int day;

    /**
     * 小时
     */
    public final int hour;

    /**
     * 分钟
     */
    public final int minute;

    /**
     * 秒
     */
    public final int second;

    /**
     * 日期选中回调函数
     */
    public final OnDateSelectListener listener;


    public DateSelector(Builder builder) {
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
        this.type = builder.type;
        this.listener = builder.listener;
        this.year = builder.year;
        this.yearBefore = builder.yearBefore;
        this.yearBehind = builder.yearBehind;
        this.month = builder.month;
        this.day = builder.day;
        this.hour = builder.hour;
        this.minute = builder.minute;
        this.second = builder.second;
        createDialog(context, type, listener);
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

        private int type = TYPE_DATE;

        private int year = Calendar.getInstance().get(Calendar.YEAR);

        private int yearBefore = 60;

        private int yearBehind = 20;

        private int month = Calendar.getInstance().get(Calendar.MONTH) + 1;

        private int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        private int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        private int minute = Calendar.getInstance().get(Calendar.MINUTE);

        private int second = Calendar.getInstance().get(Calendar.SECOND);

        private OnDateSelectListener listener;

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

        public int type() {
            return textSize;
        }

        public Builder type(int type) {
            this.type = type;
            return this;
        }

        public int year() {
            return year;
        }

        public Builder year(int year) {
            this.year = year;
            return this;
        }

        public Builder yearBefore(int yearBefore) {
            this.yearBefore = yearBefore;
            return this;
        }

        public Builder yearBehind(int yearBehind) {
            this.yearBehind = yearBehind;
            return this;
        }

        public int month() {
            return month;
        }

        public Builder month(int month) {
            this.month = month;
            return this;
        }

        public int day() {
            return day;
        }

        public Builder day(int day) {
            this.day = day;
            return this;
        }

        public int hour() {
            return hour;
        }

        public Builder hour(int hour) {
            this.hour = hour;
            return this;
        }

        public int minute() {
            return minute;
        }

        public Builder minute(int minute) {
            this.minute = minute;
            return this;
        }

        public int second() {
            return second;
        }

        public Builder second(int second) {
            this.second = second;
            return this;
        }

        public OnDateSelectListener listener() {
            return listener;
        }

        public Builder listener(OnDateSelectListener listener) {
            this.listener = listener;
            return this;
        }

        public DateSelector build() {
            return new DateSelector(this);
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
     * @param type     类型
     * @param listener 选择监听
     * @return
     */
    private void createDialog(Context context, final int type, final OnDateSelectListener listener) {
        dialog = new Dialog.Builder(context)
                .width(LinearLayout.LayoutParams.MATCH_PARENT)
                .height(LinearLayout.LayoutParams.WRAP_CONTENT)
                .layoutResId(R.layout.android_dialog_date_time)
                .animResId(R.style.android_anim_bottom)
                .themeResId(translucent ? R.style.Android_Theme_Dialog_Translucent_Background : R.style.Android_Theme_Dialog_Transparent_Background)
                .gravity(Gravity.BOTTOM)
                .build();
        LinearLayout ll_bar = dialog.contentView.findViewById(R.id.ll_bar);
        TextView tv_cancel = dialog.contentView.findViewById(R.id.tv_cancel);
        TextView tv_complete = dialog.contentView.findViewById(R.id.tv_complete);
        final LoopView lv_year = dialog.contentView.findViewById(R.id.lv_year);
        final LoopView lv_month = dialog.contentView.findViewById(R.id.lv_month);
        final LoopView lv_day = dialog.contentView.findViewById(R.id.lv_day);
        final LoopView lv_hour = dialog.contentView.findViewById(R.id.lv_hour);
        final LoopView lv_minute = dialog.contentView.findViewById(R.id.lv_minute);
        final LoopView lv_second = dialog.contentView.findViewById(R.id.lv_second);

        ll_bar.setBackgroundColor(titleBarBackgroundColor);
        tv_cancel.setTextColor(titleBarCancelTextColor);
        tv_complete.setTextColor(titleBarConfirmTextColor);
        tv_cancel.setTextSize(titleBarTextSize);
        tv_complete.setTextSize(titleBarTextSize);

        lv_year.setDividerColor(dividerColor);
        lv_month.setDividerColor(dividerColor);
        lv_day.setDividerColor(dividerColor);
        lv_hour.setDividerColor(dividerColor);
        lv_minute.setDividerColor(dividerColor);
        lv_second.setDividerColor(dividerColor);

        lv_year.setCenterTextColor(selectedColor);
        lv_month.setCenterTextColor(selectedColor);
        lv_day.setCenterTextColor(selectedColor);
        lv_hour.setCenterTextColor(selectedColor);
        lv_minute.setCenterTextColor(selectedColor);
        lv_second.setCenterTextColor(selectedColor);

        lv_year.setOuterTextColor(unselectedColor);
        lv_month.setOuterTextColor(unselectedColor);
        lv_day.setOuterTextColor(unselectedColor);
        lv_hour.setOuterTextColor(unselectedColor);
        lv_minute.setOuterTextColor(unselectedColor);
        lv_second.setOuterTextColor(unselectedColor);

        lv_year.setTextSize(textSize);
        lv_month.setTextSize(textSize);
        lv_day.setTextSize(textSize);
        lv_hour.setTextSize(textSize);
        lv_minute.setTextSize(textSize);
        lv_second.setTextSize(textSize);

        Calendar calendar = Calendar.getInstance();
        final ArrayList<String> yearList = new ArrayList<>();
        final ArrayList<String> monthList = new ArrayList<>();
        final ArrayList<String> dayList = new ArrayList<>();
        final ArrayList<String> hourList = new ArrayList<>();
        final ArrayList<String> minuteList = new ArrayList<>();
        final ArrayList<String> secondList = new ArrayList<>();
        switch (type) {
            case TYPE_DATE_TIME:
                createYearMonthDay(yearList, monthList, dayList, lv_year, lv_month, lv_day);
                createHourMinuteSecond(calendar, hourList, minuteList, secondList, lv_hour, lv_minute, lv_second);
                break;
            case TYPE_DATE_YY_MM_DD_HH_MM:
                lv_second.setVisibility(View.GONE);
                createYearMonthDay(yearList, monthList, dayList, lv_year, lv_month, lv_day);
                createHourMinuteSecond(calendar, hourList, minuteList, secondList, lv_hour, lv_minute, lv_second);
                break;
            case TYPE_DATE_YY_MM:
                lv_day.setVisibility(View.GONE);
                lv_hour.setVisibility(View.GONE);
                lv_minute.setVisibility(View.GONE);
                lv_second.setVisibility(View.GONE);
                createYearMonthDay(yearList, monthList, dayList, lv_year, lv_month, lv_day);
                break;
            case TYPE_DATE:
                lv_hour.setVisibility(View.GONE);
                lv_minute.setVisibility(View.GONE);
                lv_second.setVisibility(View.GONE);
                createYearMonthDay(yearList, monthList, dayList, lv_year, lv_month, lv_day);
                break;
            case TYPE_TIME:
                lv_year.setVisibility(View.GONE);
                lv_month.setVisibility(View.GONE);
                lv_day.setVisibility(View.GONE);
                createHourMinuteSecond(calendar, hourList, minuteList, secondList, lv_hour, lv_minute, lv_second);
                break;
            case TYPE_TIME_HH_MM:
                lv_year.setVisibility(View.GONE);
                lv_month.setVisibility(View.GONE);
                lv_day.setVisibility(View.GONE);
                lv_second.setVisibility(View.GONE);
                createHourMinuteSecond(calendar, hourList, minuteList, secondList, lv_hour, lv_minute, lv_second);
                break;
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
            public void onClick(View view) {
                if (listener != null) {
                    switch (type) {
                        case TYPE_DATE_TIME:
                            listener.onDateSelected(yearList.get(lv_year.getSelectedItem()).replace("年", "-")
                                    + monthList.get(lv_month.getSelectedItem()).replace("月", "-")
                                    + dayList.get(lv_day.getSelectedItem()).replace("日", "") + " "
                                    + hourList.get(lv_hour.getSelectedItem()).replace("时", ":")
                                    + minuteList.get(lv_minute.getSelectedItem()).replace("分", ":")
                                    + secondList.get(lv_second.getSelectedItem()).replace("秒", ""));
                            break;
                        case TYPE_DATE_YY_MM_DD_HH_MM:
                            listener.onDateSelected(yearList.get(lv_year.getSelectedItem()).replace("年", "-")
                                    + monthList.get(lv_month.getSelectedItem()).replace("月", "-")
                                    + dayList.get(lv_day.getSelectedItem()).replace("日", "") + " "
                                    + hourList.get(lv_hour.getSelectedItem()).replace("时", ":")
                                    + minuteList.get(lv_minute.getSelectedItem()).replace("分", ""));
                            break;
                        case TYPE_DATE:
                            listener.onDateSelected(yearList.get(lv_year.getSelectedItem()).replace("年", "-")
                                    + monthList.get(lv_month.getSelectedItem()).replace("月", "-")
                                    + dayList.get(lv_day.getSelectedItem()).replace("日", ""));
                            break;
                        case TYPE_DATE_YY_MM:
                            listener.onDateSelected(yearList.get(lv_year.getSelectedItem()).replace("年", "-")
                                    + monthList.get(lv_month.getSelectedItem()).replace("月", ""));
                            break;
                        case TYPE_TIME:
                            listener.onDateSelected(hourList.get(lv_hour.getSelectedItem()).replace("时", ":")
                                    + minuteList.get(lv_minute.getSelectedItem()).replace("分", ":")
                                    + secondList.get(lv_second.getSelectedItem()).replace("秒", ""));
                            break;
                        case TYPE_TIME_HH_MM:
                            listener.onDateSelected(hourList.get(lv_hour.getSelectedItem()).replace("时", ":")
                                    + minuteList.get(lv_minute.getSelectedItem()).replace("分", ""));
                            break;
                    }

                }
                dialog.dialog.dismiss();
            }
        });
    }


    /**
     * 创建-时：分：秒
     *
     * @param calendar   日历对象
     * @param hourList   小时数据
     * @param minuteList 分钟数据
     * @param secondList 秒数据
     * @param lv_hour    小时控件
     * @param lv_minute  分钟控件
     * @param lv_second  秒控件
     */
    private void createHourMinuteSecond(Calendar calendar, final ArrayList<String> hourList, final ArrayList<String> minuteList, final ArrayList<String> secondList, final LoopView lv_hour, final LoopView lv_minute, final LoopView lv_second) {
        final DecimalFormat decimalFormat = new DecimalFormat("00");
        //时
        int hourPosition = 0;
        for (int i = 0; i < 24; i++) {
            hourList.add(decimalFormat.format(i) + "时");
            if (i == hour) {
                hourPosition = i;
            }
        }
        lv_hour.setItems(hourList);
        lv_hour.setInitPosition(hourPosition);

        //分
        int minutePosition = 0;
        for (int i = 0; i < 60; i++) {
            if (i == minute) {
                minutePosition = i;
            }
            minuteList.add(decimalFormat.format(i) + "分");
        }
        lv_minute.setItems(minuteList);
        lv_minute.setInitPosition(minutePosition);

        //秒
        int secondPosition = 0;
        for (int j = 0; j < 59; j++) {
            if (j == second) {
                secondPosition = j;
            }
            secondList.add(decimalFormat.format(j) + "秒");
        }
        lv_second.setItems(secondList);
        lv_second.setCurrentPosition(secondPosition);
    }

    /**
     * 创建年-月-日数据
     *
     * @param yearList  年份数据
     * @param monthList 月份数据
     * @param dayList   日期数据
     * @param lv_year   年份控件
     * @param lv_month  月份控件
     * @param lv_day    日期控件
     */
    private void createYearMonthDay(final ArrayList<String> yearList, final ArrayList<String> monthList, final ArrayList<String> dayList, final LoopView lv_year, final LoopView lv_month, final LoopView lv_day) {
        //年份
        int yearPosition = 0;
        final int nowYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = nowYear - yearBefore; i < nowYear + yearBehind; i++) {
            yearList.add(i + "年");
            if (i == year) {
                yearPosition = yearList.size() - 1;
            }
        }
        lv_year.setItems(yearList);
        lv_year.setInitPosition(yearPosition);
        //月份
        int nowMonth = Calendar.getInstance().get(Calendar.MONTH);
        final DecimalFormat decimalFormat = new DecimalFormat("00");
        int monthPosition = 0;
        for (int i = 1; i < 13; i++) {
            if (i == month - 1) {
                monthPosition = i;
            }
            monthList.add(decimalFormat.format(i) + "月");
        }
        lv_month.setItems(monthList);
        lv_month.setInitPosition(monthPosition);
        //日
        for (int j = 1; j <= DateUtils.dayOfMonth(nowYear, nowMonth + 1); j++) {
            if (j == day) {
                todayPosition = j;
            }
            dayList.add(decimalFormat.format(j) + "日");
        }
        lv_month.setOnItemSelectedListener(new LoopView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                dayList.clear();
                for (int j = 1; j <= DateUtils.dayOfMonth(nowYear, i + 1); j++) {
                    if (j == day) {
                        todayPosition = j;
                    }
                    dayList.add(decimalFormat.format(j) + "日");
                }
                lv_day.setItems(dayList);
                lv_day.setCurrentPosition(todayPosition - 1);
            }
        });
        lv_day.setItems(dayList);
        lv_day.setCurrentPosition(todayPosition - 1);
    }

}
