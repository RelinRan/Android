package com.android.upload;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

public class UploadParams {


    public static final String USER_AGENT = "User-Agent";

    /**
     * 文件参数
     */
    private Map<String, File> fileParams;

    /**
     * 字符串参数
     */
    private Map<String, String> stringParams;
    /**
     * 头文件参数
     */
    private Map<String, String> headerParams;

    /**
     * 添加参数
     *
     * @param key  键
     * @param file 值
     */
    public void add(String key, File file) {
        if (fileParams == null) {
            fileParams = new TreeMap<>();
        }
        fileParams.put(key, file);
    }

    /**
     * 添加参数
     *
     * @param key   键
     * @param value 值
     */
    public void add(String key, String value) {
        if (stringParams == null) {
            stringParams = new TreeMap<>();
        }
        stringParams.put(key, value);
    }

    /**
     * 添加参数
     *
     * @param key   键
     * @param value 值
     */
    public void addHeader(String key, String value) {
        if (headerParams == null) {
            headerParams = new TreeMap<>();
        }
        headerParams.put(key, value);
    }

    /**
     * 获取文件参数
     *
     * @return
     */
    public Map<String, File> getFileParams() {
        return fileParams;
    }

    /**
     * 获取字符参数
     *
     * @return
     */
    public Map<String, String> getStringParams() {
        return stringParams;
    }

    /**
     * @return
     */
    public Map<String, String> getHeaderParams() {
        if (headerParams == null) {
            headerParams = new TreeMap<>();
            headerParams.put(USER_AGENT, "Android");
        }
        return headerParams;
    }

}
