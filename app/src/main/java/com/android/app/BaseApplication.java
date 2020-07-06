package com.android.app;

import android.app.Application;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.StrictMode;

import com.android.net.HttpsTrustManager;
import com.android.net.HucHttp;
import com.android.net.OkHttp;
import com.android.video.VideoScanner;

public class BaseApplication extends Application {

    /**
     * 是否调试
     */
    private boolean isDebug = false;
    /**
     * 是否显示
     */
    private boolean isShow = false;
    /**
     * 设置屏幕显示模式
     */
    private int requestedOrientation = REQUEST_ORATION_PORTRAIT;
    /**
     * 横竖屏自动
     */
    public static int REQUEST_ORATION_AUTO = ActivityInfo.SCREEN_ORIENTATION_SENSOR;
    /**
     * 竖屏模式
     */
    public static int REQUEST_ORATION_PORTRAIT = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    /**
     * 横屏模式
     */
    public static int REQUEST_ORATION_LAND = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    /**
     * BaseApplication对象
     */
    public static BaseApplication app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        checkStrictMode();
        HttpsTrustManager.trust();
        setDebugMode(true, false);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        VideoScanner.list = null;
        HucHttp.destroy();
        OkHttp.destroy();
    }

    public boolean isDetermineNetwork() {
        return true;
    }

    /**
     * 检查严格模式
     */
    private void checkStrictMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }

    /**
     * 获取屏幕方向
     *
     * @return
     */
    public int getRequestedOrientation() {
        return requestedOrientation;
    }

    /**
     * 设置屏幕方向
     *
     * @param requestedOrientation
     */
    public void setRequestedOrientation(int requestedOrientation) {
        this.requestedOrientation = requestedOrientation;
    }

    /**
     * 是否是调试环境
     *
     * @return
     */
    public boolean isDebug() {
        return isDebug;
    }

    /**
     * 设置调试模式
     *
     * @param debug
     */
    public void setDebugMode(boolean debug) {
        isDebug = debug;
        isShow = false;
    }

    /**
     * 设置调试环境
     *
     * @param enable
     * @param show   显示
     */
    public void setDebugMode(boolean enable, boolean show) {
        isDebug = enable;
        isShow = show;
    }

    /**
     * @return
     */
    public boolean isShowDebug() {
        return isShow;
    }

}
