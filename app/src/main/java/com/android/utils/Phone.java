package com.android.utils;

public class Phone {

    /**
     * 显示隐藏值电话号
     *
     * @param phone
     * @return
     */
    public static String secure(String phone) {
        int length = phone.length();
        String start = phone.substring(0, 3);
        String end = phone.substring(phone.length() - 4, phone.length());
        StringBuffer sb = new StringBuffer();
        sb.append(start);
        for (int i = 0; i < length - 7; i++) {
            sb.append("*");
        }
        sb.append(end);
        return sb.toString();
    }


}
