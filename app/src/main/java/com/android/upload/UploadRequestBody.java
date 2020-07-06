package com.android.upload;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;

public class UploadRequestBody extends RequestBody {

    /**
     * 服务器地址
     */
    private String url;
    /**
     * 进度
     */
    private int progress;
    /**
     * 上传内容
     */
    private RequestBody body;
    /**
     * 上传监听
     */
    private OnUploadListener listener;

    /**
     * 构造函数
     *
     * @param url      地址
     * @param body     上传内容
     * @param listener 上传监听
     */
    public UploadRequestBody(String url, RequestBody body, OnUploadListener listener) {
        this.url = url;
        this.body = body;
        this.listener = listener;
    }

    @Override
    public MediaType contentType() {
        return body.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return body.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        //这里需要另一个代理类来获取写入的长度
        ForwardingSink forwardingSink = new ForwardingSink(sink) {
            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                //这里可以获取到写入的长度
                progress += byteCount;
                //回调进度
                if (listener != null) {
                    UploadResponse response = new UploadResponse();
                    response.setUrl(url);
                    response.setContentLength(contentLength());
                    response.setProgress(progress);
                    listener.onUploadProgress(response, response.contentLength(), progress);
                }
                super.write(source, byteCount);
            }
        };
        //转一下
        BufferedSink bufferedSink = Okio.buffer(forwardingSink);
        //写数据
        body.writeTo(bufferedSink);
        //刷新一下数据
        bufferedSink.flush();
    }
}
