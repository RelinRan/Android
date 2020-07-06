package com.android.net;

import com.android.app.AppConstant;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Relin
 * on 2018-09-21.
 */

public class OnOkHttpListener implements Callback {

    private String url;
    private RequestParams params;
    private HttpHandler httpHandler;
    private OnHttpListener listener;

    public OnOkHttpListener(HttpHandler httpHandler, RequestParams params, String url, OnHttpListener listener) {
        this.params = params;
        this.url = url;
        this.listener = listener;
        this.httpHandler = httpHandler;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        httpHandler.sendExceptionMsg(params, url, -1, e, listener);
    }

    @Override
    public void onResponse(Call call, Response response) {
        try {
            String bod = response.body().string();
            //添加缓存
            if (OkHttp.isCache()) {
                HttpCacheBody cacheBody = new HttpCacheBody();
                cacheBody.setUrl(url);
                cacheBody.setCode(response.code()+"");
                cacheBody.setBody(bod);
                cacheBody.setException(!response.isSuccessful() ? new IOException(AppConstant.HTTP_MSG_RESPONSE_FAILED + response.code()).toString() : "");
                cacheBody.setParams(params == null || params.getStringParams() == null ? "" : params.getStringParams().toString());
                OkHttp.insertCache(cacheBody);
            }
            if (!response.isSuccessful()) {
                httpHandler.sendExceptionMsg(params, url, response.code(), new IOException(AppConstant.HTTP_MSG_RESPONSE_FAILED + response.code()), listener);
                return;
            }
            httpHandler.sendSuccessfulMsg(params, url, response.code(), bod, listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
