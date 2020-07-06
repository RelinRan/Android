package com.android.utils;

import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Relin
 * 2019/06/06 18:39:20
 * 网页加载器<br/>
 * 支持直接加载网页，同时可以修改字体的大小.<br/>
 * 控制是否缩放，图片是否自动适应宽度<br/>
 * 监听图片的点击、页面加载、url拦截回调函数<br/>
 * supports loading web pages directly, and can change font size at the same time.<br/>
 * controls whether to scale and whether the image automatically ADAPTS to the width<br/>
 * listen for image clicks, page loads, url block callback functions<br/>
 */
public class WebLoader {

    /**
     * 加载地址
     */
    public final String url;

    /**
     * 网页数据
     */
    public final String webData;

    /**
     * 加载网页数据的基本地址
     */
    public final String baseUrl;
    /**
     * 文字缩放大小
     */
    public final int textZoom;
    /**
     * 是否支持缩放
     */
    public final boolean supportZoom;
    /**
     * 是否支持JavaScript
     */
    public final boolean javaScriptEnabled;
    /**
     * 图片适配
     */
    public final boolean imageFit;
    /**
     * 加载客户端
     */
    public final WebViewClient webViewClient;
    /**
     * 页面加载监听
     */
    public final OnWebPageLoadListener pageLoadListener;
    /**
     * 图片点击监听
     */
    public final OnWebImageClickListener imageClickListener;
    /**
     * 网页Url拦截
     */
    public final OnShouldOverrideUrlLoadingListener shouldOverrideUrlLoadingListener;
    /**
     * 网页加载对象
     */
    private WebView webView;


    public WebLoader(Builder builder) {
        this.webView = builder.webView;
        this.url = builder.url;
        this.webData = builder.webData;
        this.baseUrl = builder.baseUrl;
        this.textZoom = builder.textZoom;
        this.supportZoom = builder.supportZoom;
        this.imageFit = builder.imageFit;
        this.javaScriptEnabled = builder.javaScriptEnabled;
        this.imageClickListener = builder.imageClickListener;
        this.pageLoadListener = builder.pageLoadListener;
        this.shouldOverrideUrlLoadingListener = builder.shouldOverrideUrlLoadingListener;
        this.webViewClient = builder.webViewClient;
        //设置
        WebSettings settings = webView.getSettings();
        settings.setSupportZoom(supportZoom);
        if (textZoom != 0) {
            settings.setTextZoom(textZoom);
        }
        settings.setJavaScriptEnabled(javaScriptEnabled);
        //设置客户端
        webView.setWebChromeClient(new WebChromeClient());
        if (webViewClient != null) {
            webView.setWebViewClient(webViewClient);
        } else {
            webView.setWebViewClient(new DefaultWebViewClient());
        }
        //图片点击事件
        if (imageClickListener != null) {
            webView.addJavascriptInterface(new OnWebImageClickListener() {

                @JavascriptInterface
                @Override
                public void onWebImageClick(String url) {
                    imageClickListener.onWebImageClick(url);
                }
            }, "onWebImageClickListener");
        }
        //加载
        if (url != null) {
            loadUrl(url);
        }
        if (webData != null) {
            loadData(webData);
        }
    }

    public static class Builder {

        private WebView webView;
        private String url;
        private String baseUrl;
        private String webData;
        private int textZoom = 0;
        private boolean supportZoom = true;
        private boolean javaScriptEnabled = true;
        private boolean imageFit = true;
        private WebViewClient webViewClient;
        private OnWebImageClickListener imageClickListener;
        private OnWebPageLoadListener pageLoadListener;
        private OnShouldOverrideUrlLoadingListener shouldOverrideUrlLoadingListener;

        public Builder(WebView webView) {
            this.webView = webView;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder data(String webData) {
            this.webData = webData;
            return this;
        }

        public Builder textZoom(int textZoom) {
            this.textZoom = textZoom;
            return this;
        }

        public Builder supportZoom(boolean enable) {
            this.supportZoom = enable;
            return this;
        }

        public Builder imageFit(boolean enable) {
            this.imageFit = enable;
            return this;
        }

        public Builder javaScriptEnabled(boolean javaScriptEnabled) {
            this.javaScriptEnabled = javaScriptEnabled;
            return this;
        }

        public Builder webViewClient(WebViewClient webViewClient) {
            this.webViewClient = webViewClient;
            return this;
        }

        public Builder imageClickListener(OnWebImageClickListener imageClickListener) {
            this.imageClickListener = imageClickListener;
            return this;
        }

        public Builder pageLoadListener(OnWebPageLoadListener pageLoadListener) {
            this.pageLoadListener = pageLoadListener;
            return this;
        }

        public Builder shouldOverrideUrlLoadingListener(OnShouldOverrideUrlLoadingListener shouldOverrideUrlLoadingListener) {
            this.shouldOverrideUrlLoadingListener = shouldOverrideUrlLoadingListener;
            return this;
        }

        public WebLoader build() {
            return new WebLoader(this);
        }

    }

    /**
     * 字体缩放
     *
     * @param textZoom
     */
    public WebLoader textZoom(int textZoom) {
        webView.getSettings().setTextZoom(textZoom);
        return this;
    }

    /**
     * 获取网页设置对象
     *
     * @return
     */
    public WebSettings webSettings() {
        return webView.getSettings();
    }

    /**
     * WebView对象
     *
     * @return
     */
    public WebView webView() {
        return webView;
    }

    /**
     * 加载地址
     */
    public void loadUrl(String url) {
        webView.loadUrl(url);
    }

    /**
     * 加载数据源
     *
     * @param data
     */
    public void loadData(String data) {
        webView.loadDataWithBaseURL(baseUrl, (imageFit ? "<head><style>img{width:100% !important;height:auto}</style></head>" : "") + data, "text/html", "utf-8", null);
    }

    /**
     * 默认客户端
     */
    private class DefaultWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (pageLoadListener != null) {
                pageLoadListener.onPageFinished(view, url);
            }
            if (imageClickListener != null) {
                view.loadUrl(createWebImageClickJavascript("onWebImageClickListener", "onWebImageClick"));
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (shouldOverrideUrlLoadingListener != null) {
                shouldOverrideUrlLoadingListener.onShouldOverrideUrlLoading(view, request);
                return false;
            }
            return super.shouldOverrideUrlLoading(view, request);
        }
    }

    /**
     * Url拦截监听
     */
    public interface OnShouldOverrideUrlLoadingListener {
        void onShouldOverrideUrlLoading(WebView view, WebResourceRequest request);
    }

    /**
     * 网页加载回调
     */
    public interface OnWebPageLoadListener {

        void onPageFinished(WebView view, String url);

    }

    /**
     * 图片点击回调接口
     */

    public interface OnWebImageClickListener {

        @JavascriptInterface
        void onWebImageClick(String url);

    }

    /**
     * @param javascriptInterface
     * @param method
     * @return
     */
    public String createWebImageClickJavascript(String javascriptInterface, String method) {
        return "javascript:(function(){" +
                "var imgs=document.getElementsByTagName(\"img\");" +
                "for(var i=0;i<imgs.length;i++){" +
                "imgs[i].onclick=function(){" +
                "window." + javascriptInterface + "." + method + "(this.src);" +
                "}}})()";
    }

}
