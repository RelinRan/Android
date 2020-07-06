package com.android.utils;

public class Encode {

    public static void main(String[] args) {
        String value = "测试文字AaZz";
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int intChar = (int) chars[i];
            System.out.println("intChar-->" + (intChar));
        }
    }

}
