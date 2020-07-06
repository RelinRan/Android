package com.android.utils;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Price {

    /**
     * 格式价格字符串
     *
     * @param price
     * @return
     */
    public static String format(String price) {
        if (price == null || price.length() == 0 || price.equals("null") || price.equals("0")) {
            return "0.00";
        }
        if (!price.contains(".")) {
            price += ".00";
        }
        DecimalFormat format = new DecimalFormat("0.00");
        return format.format(Double.parseDouble(price)) + "";
    }

    /**
     * 价格格式输入
     *
     * @param editText
     */
    public static void format(EditText editText) {
        format(editText, 2);
    }

    /**
     * 设置过滤器
     *
     * @param editText     输入控件
     * @param charSequence 字符
     * @param decimalPoint 小数点
     */
    public static void setFilter(EditText editText, CharSequence charSequence, int decimalPoint) {
        String regex = "^\\d+.$";
        Pattern r = Pattern.compile(regex);
        Matcher matcher = r.matcher(charSequence);
        if (matcher.matches()) {
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(charSequence.length() + decimalPoint)});
        }
    }

    /**
     * 显示输入小数点位数
     *
     * @param editText
     * @param decimalPoint
     */
    public static void format(EditText editText, int decimalPoint) {
        format(editText, decimalPoint, null);
    }

    /**
     * 显示输入小数点位数
     *
     * @param editText
     * @param decimalPoint
     */
    public static void format(final EditText editText, final int decimalPoint, final addTextChangedListener listener) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                setFilter(editText, charSequence, decimalPoint);
                if (listener != null) {
                    listener.onTextChanged(charSequence, start, before, count);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public interface addTextChangedListener {

        void onTextChanged(CharSequence charSequence, int start, int before, int count);

    }
}
