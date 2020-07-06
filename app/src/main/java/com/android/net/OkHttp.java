package com.android.net;

import android.text.TextUtils;

import com.android.app.AppConstant;
import com.android.app.BaseApplication;
import com.android.json.JsonParser;
import com.android.sqlite.SQLiteHelper;
import com.android.utils.ListUtils;
import com.android.utils.Log;
import com.android.utils.Null;
import com.android.utils.Number;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.CacheControl;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Relin
 * on 2018-09-10.
 */
public class OkHttp {

    public static String TAG = "Cache";

    /**
     * 是否缓存数据
     */
    public static boolean cache;
    /**
     * Http异步请求数据处理
     */
    private static HttpHandler httpHandler;

    private OkHttp() {

    }

    /**
     * 连接池
     */
    private static ExecutorService executorService;

    static {
        executorService = Executors.newFixedThreadPool(10);
        if (httpHandler == null) {
            synchronized (HttpHandler.class) {
                if (httpHandler == null) {
                    httpHandler = new HttpHandler();
                }
            }
        }
    }

    /**
     * 青内存
     */
    public static void destroy() {
        if (httpHandler != null) {
            httpHandler.removeCallbacksAndMessages(null);
            httpHandler = null;
        }
    }

    /**
     * Get请求方式
     *
     * @param url
     * @param params
     */
    public static void get(final String url, final RequestParams params, final OnHttpListener listener) {
        if (checkCache(url, params, listener)) {
            return;
        }
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                okhttp3.OkHttpClient okHttpClient = buildOkHttpClient(params);
                String getUrl = "";
                StringBuffer requestUrl = new StringBuffer();
                requestUrl.append(url);
                requestUrl.append("?");
                if (params != null && params.getStringParams() != null) {
                    Map<String, String> stringParams = params.getStringParams();
                    for (String key : stringParams.keySet()) {
                        String value = stringParams.get(key);
                        requestUrl.append(key);
                        requestUrl.append("=");
                        requestUrl.append(value);
                        requestUrl.append("&");
                    }
                    getUrl = requestUrl.toString().substring(0, requestUrl.lastIndexOf("&"));
                }
                //创建一个请求
                Request.Builder requestBuilder = new Request.Builder();
                String agent = params.getOptionParams().get(RequestParams.USER_AGENT);
                requestBuilder.addHeader("User-Agent", TextUtils.isEmpty(agent) ? "Android" : agent);
                requestBuilder.addHeader("Connection", "close");
                //添加头文件
                if (params != null && params.getHeaderParams() != null) {
                    Map<String, String> headerParams = params.getHeaderParams();
                    for (String key : headerParams.keySet()) {
                        String value = headerParams.get(key);
                        requestBuilder.addHeader(key, value);
                    }
                }
                requestBuilder.url(getUrl);
                //如果没有网络就使用缓存
                if (!NetworkUtils.isAvailable(BaseApplication.app)) {
                    requestBuilder.cacheControl(CacheControl.FORCE_CACHE);
                } else {
                    requestBuilder.cacheControl(CacheControl.FORCE_NETWORK);
                }
                String tag = params.getOptionParams().get(RequestParams.REQUEST_TAG);
                Request request = requestBuilder.tag(TextUtils.isEmpty(tag) ? url : tag).build();
                //请求加入调度
                okhttp3.Call call = okHttpClient.newCall(request);
                call.enqueue(new OnOkHttpListener(httpHandler, params, url, listener));
            }
        });

    }

    /**
     * Post请求方式
     *
     * @param url    地址
     * @param params 参数
     */
    public static void post(final String url, final RequestParams params, final OnHttpListener listener) {
        if (checkCache(url, params, listener)) {
            return;
        }
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (params.getOptionParams().get(RequestParams.REQUEST_CONTENT_TYPE).equals(RequestParams.REQUEST_CONTENT_JSON)
                        || params.getOptionParams().get(RequestParams.REQUEST_CONTENT_TYPE).equals(RequestParams.REQUEST_CONTENT_STRING)) {
                    postOther(url, params, listener);
                } else {
                    //创建okHttpClient对象
                    okhttp3.OkHttpClient okHttpClient = buildOkHttpClient(params);
                    MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
                    multipartBodyBuilder.setType(MultipartBody.FORM);
                    multipartBodyBuilder.addFormDataPart("", "");
                    //一般参数
                    if (params != null && params.getStringParams() != null) {
                        Map<String, String> stringParams = params.getStringParams();
                        for (String key : stringParams.keySet()) {
                            String value = stringParams.get(key);
                            multipartBodyBuilder.addFormDataPart(key, value);
                        }
                    }
                    //文件参数
                    if (params != null && params.getFileParams() != null) {
                        Map<String, File> fileParams = params.getFileParams();
                        //设置文件处理类型
                        for (String key : fileParams.keySet()) {
                            File file = fileParams.get(key);
                            String fileName = file.getName();
                            MediaType type = MediaType.parse(MediaTypeRecognition.identifyMediaType(file));
                            RequestBody fileBody = RequestBody.create(type, file);
                            multipartBodyBuilder.addFormDataPart(key, fileName, fileBody);
                        }
                    }
                    //生成请求体
                    RequestBody requestBody = multipartBodyBuilder.build();
                    Request.Builder requestBuilder = new Request.Builder();
                    //添加Header
                    String agent = params.getOptionParams().get(RequestParams.USER_AGENT);
                    requestBuilder.addHeader("User-Agent", TextUtils.isEmpty(agent) ? "Android" : agent);
                    requestBuilder.addHeader("Connection", "close");
                    if (params != null && params.getHeaderParams() != null) {
                        Map<String, String> headerParams = params.getHeaderParams();
                        for (String key : headerParams.keySet()) {
                            requestBuilder.addHeader(key, headerParams.get(key));
                        }
                    }
                    requestBuilder.url(url);
                    requestBuilder.cacheControl(CacheControl.FORCE_NETWORK);
                    requestBuilder.post(requestBody);//传参数、文件或者混合
                    String tag = params.getOptionParams().get(RequestParams.REQUEST_TAG);
                    Request request = requestBuilder.tag(TextUtils.isEmpty(tag) ? url : tag).build();
                    okhttp3.Call call = okHttpClient.newCall(request);
                    call.enqueue(new OnOkHttpListener(httpHandler, params, url, listener));
                }
            }
        });
    }

    /**
     * 检查缓存
     *
     * @param url      地址
     * @param params   参数
     * @param listener 回调
     * @return
     */
    protected static boolean checkCache(final String url, final RequestParams params, final OnHttpListener listener) {
        if (!isCache() && !NetworkUtils.isAvailable(BaseApplication.app)) {
            sendNoNetworkMessage(url, params, listener);
            return false;
        }
        if (isCache() && !NetworkUtils.isAvailable(BaseApplication.app)) {
            List<HttpCacheBody> cacheBodies = queryCache(url, params.getStringParams() == null ? "" : params.getStringParams().toString());
            if (ListUtils.getSize(cacheBodies) != 0) {
                HttpCacheBody cacheBody = cacheBodies.get(0);
                HttpResponse response = new HttpResponse();
                response.setCache(true);
                response.url(url);
                response.body(cacheBody.getBody());
                response.code(Number.formatInt(cacheBody.getCode()));
                response.requestParams(params);
                response.exception(new Exception(cacheBody.getException()));
                if (Number.formatInt(cacheBody.getCode()) == 200) {
                    httpHandler.sendSuccessfulMsg(params, url, response.code(), response.body(), listener);
                } else {
                    httpHandler.sendExceptionMsg(params, url, response.code(), new IOException(AppConstant.HTTP_MSG_RESPONSE_FAILED + response.code()), listener);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Json内容请求
     *
     * @param url
     * @param params
     * @param listener
     */
    private static void postOther(String url, RequestParams params, OnHttpListener listener) {
        if (BaseApplication.app.isDetermineNetwork() && !NetworkUtils.isAvailable(BaseApplication.app)) {
            sendNoNetworkMessage(url, params, listener);
            return;
        }
        OkHttpClient okHttpClient = buildOkHttpClient(params);
        MediaType mediaType = params.getOptionParams().get(RequestParams.REQUEST_CONTENT_TYPE).equals(RequestParams.REQUEST_CONTENT_JSON) ? MediaType.parse("application/json; charset=utf-8") : MediaType.parse("application/octet-stream; charset=utf-8");
        String stringParams;
        String jsonContent = params.getStringBody();
        if (Null.isNull(jsonContent)) {
            stringParams = JsonEscape.escape(JsonParser.parseMap(params.getStringParams()));
        } else {
            stringParams = JsonEscape.escape(jsonContent);
        }
        RequestBody body = RequestBody.create(mediaType, stringParams);
        Request.Builder requestBuilder = new Request.Builder();
        //添加Header
        String agent = params.getOptionParams().get(RequestParams.USER_AGENT);
        requestBuilder.addHeader("User-Agent", TextUtils.isEmpty(agent) ? "Android" : agent);
        if (params != null && params.getHeaderParams() != null) {
            Map<String, String> headerParams = params.getHeaderParams();
            for (String key : headerParams.keySet()) {
                requestBuilder.addHeader(key, headerParams.get(key));
            }
        }
        String tag = params.getOptionParams().get(RequestParams.REQUEST_TAG);
        Request request = requestBuilder.url(url).post(body).tag(TextUtils.isEmpty(tag) ? url : tag).build();
        okhttp3.Call call = okHttpClient.newCall(request);
        call.enqueue(new OnOkHttpListener(httpHandler, params, url, listener));
    }


    /**
     * 创建Http客户端对象
     *
     * @return OkHttpClient
     */
    private static okhttp3.OkHttpClient buildOkHttpClient(RequestParams params) {
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
        okBuilder.protocols(Collections.singletonList(Protocol.HTTP_1_1));
        okBuilder.connectTimeout(RequestParams.DEFAULT_TIME_OUT, TimeUnit.SECONDS);
        okBuilder.readTimeout(RequestParams.DEFAULT_TIME_OUT, TimeUnit.SECONDS);
        okBuilder.writeTimeout(RequestParams.DEFAULT_TIME_OUT, TimeUnit.SECONDS);
        okBuilder.connectionPool(new ConnectionPool(RequestParams.DEFAULT_MAX_IDLE_CONNECTIONS, RequestParams.DEFAULT_KEEP_ALIVE_DURATION, TimeUnit.SECONDS));
        okBuilder.cookieJar(new OkCookie());
        okBuilder.dispatcher(new Dispatcher());
        okBuilder.retryOnConnectionFailure(false);
        setHttpsSetting(okBuilder);
        if (params != null && params.getOptionParams() != null) {
            Map<Integer, String> options = params.getOptionParams();
            if (!TextUtils.isEmpty(options.get(RequestParams.CONNECT_TIME_OUT))) {
                okBuilder.connectTimeout(Long.parseLong(options.get(RequestParams.CONNECT_TIME_OUT)), TimeUnit.SECONDS);
            }
            if (!TextUtils.isEmpty(options.get(RequestParams.READ_TIME_OUT))) {
                okBuilder.readTimeout(Long.parseLong(options.get(RequestParams.READ_TIME_OUT)), TimeUnit.SECONDS);
            }
            if (!TextUtils.isEmpty(options.get(RequestParams.WRITE_TIME_OUT))) {
                okBuilder.writeTimeout(Long.parseLong(options.get(RequestParams.WRITE_TIME_OUT)), TimeUnit.SECONDS);
            }
            if (!TextUtils.isEmpty(options.get(RequestParams.MAX_IDLE_CONNECTIONS)) || !TextUtils.isEmpty(options.get(RequestParams.KEEP_ALIVE_DURATION))) {
                okBuilder.connectionPool(new ConnectionPool(RequestParams.DEFAULT_MAX_IDLE_CONNECTIONS, RequestParams.DEFAULT_KEEP_ALIVE_DURATION, TimeUnit.SECONDS));
            }
        }
        return okBuilder.build();
    }

    /**
     * 发送没有网络的消息
     *
     * @param url
     * @param params
     * @param listener
     */
    private static void sendNoNetworkMessage(String url, RequestParams params, OnHttpListener listener) {
        HttpResponse response = new HttpResponse();
        response.url(url);
        response.code(-11);
        response.requestParams(params);
        response.exception(new Exception("No network connection, unable to request data interface."));
        listener.onHttpFailure(response);
    }

    /**
     * 设置Https连接
     *
     * @param okBuilder
     */
    public static void setHttpsSetting(okhttp3.OkHttpClient.Builder okBuilder) {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        javax.net.ssl.SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        okBuilder.sslSocketFactory(sslSocketFactory);
        okBuilder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
    }


    /**
     * 是否缓存
     *
     * @return
     */
    public static boolean isCache() {
        return cache;
    }

    /**
     * 设置缓存标识
     *
     * @param isCache
     */
    public static void setCache(boolean isCache) {
        OkHttp.cache = isCache;
    }

    /**
     * 插入缓存
     *
     * @param body
     */
    public static void insertCache(HttpCacheBody body) {
        List<HttpCacheBody> bodies = queryCache(body.getUrl(), body.getParams());
        int cacheSize = ListUtils.getSize(bodies);
        Log.i(TAG, "->insertCache cacheSize:" + cacheSize + ",url:" + body.getUrl());
        if (cacheSize == 0) {
            SQLiteHelper.with(BaseApplication.app).insert(body);
        } else {
            updateCache(body);
        }
    }

    /**
     * 更新缓存
     *
     * @param body
     */
    public static void updateCache(HttpCacheBody body) {
        Log.i(TAG, "->updateCache " + "url:" + body.getUrl());
        SQLiteHelper.with(BaseApplication.app).update(body, " url=? and params=? ", new String[]{body.getUrl(), body.getParams()});
    }

    /**
     * 获取缓存
     *
     * @param url    服务器地址
     * @param params 参数
     * @return
     */
    public static List<HttpCacheBody> queryCache(String url, String params) {
        List<HttpCacheBody> items = SQLiteHelper.with(BaseApplication.app).query(HttpCacheBody.class, "select * from " + HttpCacheBody.class.getSimpleName() + " where params = \'" + params + "\' and url = \'" + url + "\'");
        Log.i(TAG, "->queryCache size:" + ListUtils.getSize(items) + ",url:" + url);
        return items;
    }

    /**
     * 清除缓存
     */
    public static void deleteCache() {
        Log.i(TAG, "->deleteCache");
        SQLiteHelper.with(BaseApplication.app).deleteTable(HttpCacheBody.class.getSimpleName());
    }

}
