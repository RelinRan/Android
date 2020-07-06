package com.android.utils;

import com.android.app.BaseApplication;

/**
 * Author: Relin
 * Describe:程序缓存
 * Date:2020/5/25 11:48
 */
public class Cache {

    /**
     * Token Key
     */
    private final String TOKEN = "CACHE_TOKEN";

    /**
     * Base Url Key
     */
    private final String BASE_URL = "CACHE_BASE_URL";

    /**
     * 设置Token
     *
     * @param value
     */
    public void token(String value) {
        DataStorage.with(BaseApplication.app).put(TOKEN, value);
    }

    /**
     * 获取Token
     *
     * @return
     */
    public String token() {
        return DataStorage.with(BaseApplication.app).getString(TOKEN, "");
    }

    /**
     * 设置缓存URL
     *
     * @param value
     */
    public void url(String value) {
        DataStorage.with(BaseApplication.app).put(BASE_URL, value);
    }

    /**
     * 获取缓存URL
     *
     * @return
     */
    public String url() {
        return DataStorage.with(BaseApplication.app).getString(BASE_URL, "");
    }

    /**
     * 内联URL
     *
     * @param url
     * @return
     */
    public String jointUrl(String url) {
        return url() + url;
    }

}
