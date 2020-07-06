package com.android.utils;

import java.util.regex.Pattern;

public class Validator {

    /**
     * 手机号正则
     */
    public static String REGEX_PHONE = "^(13[0-9]|14[5-9]|15[0-3,5-9]|16[2,5,6,7]|17[0-8]|18[0-9]|19[1,3,5,8,9])\\d{8}$";

    /**
     * 座机号正则
     */
    public static String REGEX_LAND_LINE = "0\\d{2,3}-\\d{7,8}";

    /**
     * 身份证正则
     */
    public static String REGEX_ID_CARD = "^(\\d{6})(19|20)(\\d{2})(1[0-2]|0[1-9])(0[1-9]|[1-2][0-9]|3[0-1])(\\d{3})(\\d|X|x)?$";

    /**
     * 微信号正则
     */
    public static String REGEX_WE_CHAT = "^[a-zA-Z]{1}[-_a-zA-Z0-9]{5,19}+$";

    /**
     * 密码正则 （密码长度为8到20位,必须包含字母和数字，字母区分大小写）
     */
    public static String REGEX_PASSWORD = "^(?=.*[0-9])(?=.*[a-zA-Z])(.{8,20})$";

    /**
     * 邮箱正则
     */
    public static String REGEX_EMAIL= "^[a-z0-9A-Z]+[- | a-z0-9A-Z . _]+@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-z]{2,}$";

    /**
     * 数字正则
     */
    public static String REGEX_NUMBER = "^-?[0-9]+$";

    /**
     * 是否是手机号
     *
     * @param number
     * @return
     */
    public static boolean isPhone(String number) {
        Pattern pattern = Pattern.compile(REGEX_PHONE);
        return pattern.matcher(number).matches();
    }

    /**
     * 是否是手机号
     *
     * @param number
     * @return
     */
    public static boolean isLandline(String number) {
        Pattern pattern = Pattern.compile(REGEX_LAND_LINE);
        return pattern.matcher(number).matches();
    }

    /**
     * 是否是身份证号（粗略的校验）
     *
     * @param number
     * @return
     */
    public static boolean isIdCard(String number) {
        Pattern pattern = Pattern.compile(REGEX_ID_CARD);
        return pattern.matcher(number).matches();
    }

    /**
     * 是否是微信号
     *
     * @param number 微信账号仅支持6-20个字母、数字、下划线或减号，以字母开头
     * @return
     */
    public static boolean isWeChat(String number) {
        Pattern pattern = Pattern.compile(REGEX_WE_CHAT);
        return pattern.matcher(number).matches();
    }

    /**
     * 是否是密码
     * @param password 密码长度为8到20位,必须包含字母和数字，字母区分大小写
     * @return
     */
    public static boolean isPassword(String password){
        Pattern pattern = Pattern.compile(REGEX_PASSWORD);
        return pattern.matcher(password).matches();
    }

    /**
     * 数字正则
     * @number number 数字数据
     * @return
     */
    public static boolean isNumeric(String number){
        Pattern pattern = Pattern.compile(REGEX_NUMBER);
        return pattern.matcher(number).matches();
    }

    /**
     * 邮箱正则
     * @param mail 邮箱数据
     * @return
     */
    public static boolean isEmail(String mail){
        Pattern pattern = Pattern.compile(REGEX_EMAIL);
        return pattern.matcher(mail).matches();
    }


    /**
     * QQ号格式
     *
     * @param number
     * @return
     */
    public static boolean isQQ(String number) {
        if (number == null) {
            return false;
        }
        if (number.length() == 0) {
            return false;
        }
        if (number.startsWith("0")) {
            return false;
        }
        if (number.length() < 5) {
            return false;
        }
        if (number.length() > 12) {
            return false;
        }
        return true;
    }

}
