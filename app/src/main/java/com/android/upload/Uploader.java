package com.android.upload;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.android.app.BaseApplication;
import com.android.json.JsonParser;
import com.android.net.JsonEscape;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Uploader implements OnUploadListener, Callback {

    public static final String TAG = "Uploader";

    /**
     * 进度
     */
    private static final int WHAT_PROGRESS = 0x1;

    /**
     * 失败
     */
    private static final int WHAT_FAILURE = 0x2;

    /**
     * 请求返回
     */
    private static final int WHAT_RESPONSE = 0x3;

    /**
     * 表单类型
     */
    public static final int MEDIA_TYPE_FORM = 0;

    /**
     * JSON类型
     */
    public static final int MEDIA_TYPE_JSON = 1;

    /**
     * 数据类型
     */
    public final int mediaType;

    /**
     * 服务器地址
     */
    public final String url;

    /**
     * 文件
     */
    public final File file;

    /**
     * 上传监听
     */
    public final OnUploadListener listener;

    /**
     * 上传参数
     */
    public final UploadParams params;


    public Uploader(Builder builder) {
        this.mediaType = builder.mediaType;
        this.url = builder.url;
        this.file = builder.file;
        this.listener = builder.listener;
        this.params = builder.params;
        Executors.newFixedThreadPool(10).execute(new Runnable() {
            @Override
            public void run() {
                if (mediaType == MEDIA_TYPE_FORM) {
                    formUpload(url, params, Uploader.this);
                }
                if (mediaType == MEDIA_TYPE_JSON) {
                    jsonUpload(url, params, Uploader.this);
                }
            }
        });
    }

    @Override
    public void onUploadProgress(UploadResponse response, long max, long progress) {
        Message message = handler.obtainMessage();
        message.what = WHAT_PROGRESS;
        message.obj = response;
        handler.sendMessage(message);
    }

    @Override
    public void onUploadFailure(UploadResponse response, IOException e) {
        Message message = handler.obtainMessage();
        message.what = WHAT_FAILURE;
        message.obj = response;
        handler.sendMessage(message);
    }

    @Override
    public void onUploadResponse(UploadResponse response) {
        Message message = handler.obtainMessage();
        message.what = WHAT_RESPONSE;
        message.obj = response;
        handler.sendMessage(message);
    }

    @Override
    public void onFailure(Call call, IOException e) {
        UploadResponse uploadResponse = new UploadResponse();
        uploadResponse.setRequestParams(params);
        uploadResponse.setCall(call);
        uploadResponse.setException(e);
        uploadResponse.setUrl(url);
        uploadResponse.setCode(-1);
        onUploadFailure(uploadResponse, e);
    }

    @Override
    public void onResponse(Call call, Response response) {
        UploadResponse uploadResponse = new UploadResponse();
        uploadResponse.setRequestParams(params);
        uploadResponse.setResponse(response);
        uploadResponse.setCall(call);
        uploadResponse.setUrl(url);
        uploadResponse.setCode(response.code());
        try {
            uploadResponse.setBody(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        onUploadResponse(uploadResponse);
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            UploadResponse response = (UploadResponse) msg.obj;
            if (msg.what == WHAT_PROGRESS) {
                if (listener != null) {
                    listener.onUploadProgress(response, response.contentLength(), response.progress());
                }
            }
            if (msg.what == WHAT_FAILURE) {
                printDebugLog(response);
                if (listener != null) {
                    listener.onUploadFailure(response, response.exception());
                }
            }
            if (msg.what == WHAT_RESPONSE) {
                printDebugLog(response);
                if (listener != null) {
                    listener.onUploadResponse(response);
                }
            }
        }
    };

    public static class Builder {

        private int mediaType;
        private String url;
        private File file;
        private OnUploadListener listener;
        private UploadParams params;

        public int mediaType() {
            return mediaType;
        }

        public Builder mediaType(int mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public String url() {
            return url;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public File file() {
            return file;
        }

        public Builder file(File file) {
            this.file = file;
            return this;
        }

        public OnUploadListener getListener() {
            return listener;
        }

        public Builder listener(OnUploadListener listener) {
            this.listener = listener;
            return this;
        }

        public UploadParams params() {
            return params;
        }

        public Builder params(UploadParams params) {
            this.params = params;
            return this;
        }

        public Uploader build() {
            return new Uploader(this);
        }
    }

    /**
     * 表单上传
     *
     * @param url      服务器地址
     * @param params   上传参数
     * @param listener 上传回调
     */
    private void formUpload(String url, UploadParams params, final OnUploadListener listener) {
        OkHttpClient okHttpClient = buildOkHttpClient();
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        multipartBodyBuilder.addFormDataPart("", "");
        //字符参数
        if (params != null && params.getStringParams() != null) {
            for (String key : params.getStringParams().keySet()) {
                String value = params.getStringParams().get(key);
                multipartBodyBuilder.addFormDataPart(key, value);
            }
        }
        //文件参数
        if (params != null && params.getFileParams() != null) {
            for (String key : params.getFileParams().keySet()) {
                File file = params.getFileParams().get(key);
                String fileName = file.getName();
                String mediaType = identifyMediaType(fileName);
                MediaType type = MediaType.parse(mediaType);
                RequestBody fileBody = RequestBody.create(type, file);
                multipartBodyBuilder.addFormDataPart(key, fileName, fileBody);
            }
        }
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.addHeader("Connection", "close");
        //添加Header
        if (params != null && params.getHeaderParams() != null) {
            Map<String, String> headerParams = params.getHeaderParams();
            for (String key : headerParams.keySet()) {
                requestBuilder.addHeader(key, headerParams.get(key));
            }
        }
        MultipartBody multipartBody = multipartBodyBuilder.build();
        UploadRequestBody uploadRequestBody = new UploadRequestBody(url, multipartBody, listener);
        Request request = requestBuilder
                .url(url)
                .post(uploadRequestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(this);
    }

    /**
     * Json文件上传
     *
     * @param url      服务器地址
     * @param params   上传参数
     * @param listener 上传回调
     */
    private void jsonUpload(String url, UploadParams params, final OnUploadListener listener) {
        OkHttpClient okHttpClient = buildOkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        String stringParams = JsonEscape.escape(JsonParser.parseMap(params.getStringParams()));
        RequestBody requestBody = RequestBody.create(mediaType, stringParams);
        UploadRequestBody body = new UploadRequestBody(url, requestBody, listener);
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.addHeader("Connection", "close");
        //添加Header
        if (params != null && params.getHeaderParams() != null) {
            Map<String, String> headerParams = params.getHeaderParams();
            for (String key : headerParams.keySet()) {
                requestBuilder.addHeader(key, headerParams.get(key));
            }
        }
        Request request = requestBuilder.url(url).post(body).build();
        okhttp3.Call call = okHttpClient.newCall(request);
        call.enqueue(this);
    }

    /**
     * 创建客户端
     *
     * @return
     */
    private OkHttpClient buildOkHttpClient() {
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
        okBuilder.protocols(Collections.singletonList(Protocol.HTTP_1_1));
        okBuilder.writeTimeout(60, TimeUnit.SECONDS);
        okBuilder.readTimeout(100, TimeUnit.SECONDS);
        okBuilder.connectTimeout(60, TimeUnit.SECONDS);
        return okBuilder.build();
    }

    /**
     * 验证文件类型
     *
     * @param name
     * @return
     */
    public static String identifyMediaType(String name) {
        if (TextUtils.isEmpty(name)) {
            return "*/*";
        }
        if (name.toUpperCase().endsWith(".PNG")) {
            return "image/png";
        }
        if (name.toUpperCase().endsWith(".GIF")) {
            return "image/gif";
        }
        if (name.toUpperCase().endsWith(".BMP")) {
            return "image/bmp";
        }
        if (name.toUpperCase().endsWith(".JPEG") || name.toUpperCase().endsWith(".JPG")) {
            return "image/jpeg";
        }
        if (name.toUpperCase().endsWith(".ZIP")) {
            return "application/zip";
        }
        return "application/octet-stream";
    }

    /**
     * 打印调试日志
     *
     * @param response
     */
    private void printDebugLog(UploadResponse response) {
        if (BaseApplication.app.isDebug()) {
            StringBuffer logBuffer = new StringBuffer("Program interface debug mode");
            logBuffer.append("\n");
            logBuffer.append("┌──────────────────────────────────────");
            logBuffer.append("\n");
            logBuffer.append("│" + response.url());
            logBuffer.append("\n");
            logBuffer.append("├┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄");
            StringBuffer paramsBuffer = new StringBuffer("");
            if (response.requestParams().getStringParams() != null) {
                for (String key : response.requestParams().getStringParams().keySet()) {
                    paramsBuffer.append("│\"" + key + "\":" + "\"" + response.requestParams().getStringParams().get(key) + "\"");
                    paramsBuffer.append("\n");
                }
            }
            if (response.requestParams().getFileParams() != null) {
                for (String key : response.requestParams().getFileParams().keySet()) {
                    paramsBuffer.append("│\"" + key + "\":" + "\"" + response.requestParams().getFileParams().get(key).getAbsolutePath() + "\"");
                    paramsBuffer.append("\n");
                }
            }
            if (paramsBuffer.toString().length() != 0) {
                logBuffer.append("\n");
                logBuffer.append(paramsBuffer);
                logBuffer.append("├┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄");
            }
            if (response != null) {
                logBuffer.append("\n");
                logBuffer.append("│\"" + "code:" + "\"" + response.code() + "\"");
                logBuffer.append("\n");
                if (response.exception() != null) {
                    logBuffer.append("│  \"" + "exception:" + "\"" + response.exception() + "\"");
                    logBuffer.append("\n");
                }
            }
            logBuffer.append("│\"" + "body:" + response.body());
            logBuffer.append("\n");
            logBuffer.append("└──────────────────────────────────────");
            com.android.utils.Log.i(TAG, logBuffer.toString());
        }
    }

}
