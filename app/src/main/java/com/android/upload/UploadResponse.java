package com.android.upload;

import java.io.IOException;
import java.io.Serializable;

import okhttp3.Call;
import okhttp3.Response;

public class UploadResponse implements Serializable {

    private UploadParams requestParams;
    private int code;
    private String body;
    private String url;
    private long contentLength;
    private long progress;
    private Call call;
    private IOException exception;
    private Response response;


    public UploadParams requestParams() {
        return requestParams;
    }

    public void setRequestParams(UploadParams requestParams) {
        this.requestParams = requestParams;
    }

    public long contentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public Response response() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public long progress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public Call getCall() {
        return call;
    }

    public void setCall(Call call) {
        this.call = call;
    }

    public IOException exception() {
        return exception;
    }

    public void setException(IOException exception) {
        this.exception = exception;
    }

    public int code() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String body() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String url() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}