package com.android.app;

/**
 * Created by Relin
 * on 2018-09-21.
 */
public class AppConstant {

    /**
     * 无网络-What
     */
    public static final int WHAT_MSG_NET_OFFLINE = 0xa2;

    /**
     * 请求失败-What
     */
    public static final int WHAT_MSG_RESPONSE_FAILED = 0xa3;

    /**
     * 授权-请求码
     */
    public static final int REQUEST_CODE_PERMISSIONS = 0xa4;

    /**
     * Handler-消息Key
     */
    public static final String MSG_KEY = "0x1";

    /**
     * Handler-消息Code
     */
    public static final String MSG_CODE = "0x2";

    /**
     * 无网络的提示
     */
    public static final String EXCEPTION_MSG_NET_OFFLINE = "连网失败";

    /**
     * 请求失败的提示
     */
    public static final String EXCEPTION_MSG_RESPONSE_FAILED = "请求失败";

    /**
     * 请求失败的提示
     */
    public static final String EXCEPTION_MSG_REQUEST_TIMEOUT = "请求超时";

    /**
     * 没有网络
     */
    public static final String HTTP_MSG_NET_OFFLINE = "Network connection failed,please check the mobile network.";

    /**
     * 请求失败
     */
    public static final String HTTP_MSG_RESPONSE_FAILED = "The request data failed and the response code is not 200,code = ";



}
