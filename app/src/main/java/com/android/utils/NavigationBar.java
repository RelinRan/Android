package com.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.lang.reflect.Method;

/**
 * 解决底部屏幕按键适配<br/>
 * Created by Relin<br/>
 * on 2018/05/10.<br/>
 * 主要因为全屏显示弹出输入框问题，还有就是底部虚拟导航栏<br/>
 * 遮住了布局问题。<br/>
 * mainly due to the full screen display popup input box problem,<br/>
 * and the bottom of the virtual navigation bar<br/>
 * covers layout issues.<br/>
 */
public class NavigationBar {

    private View mChildOfContent;
    private int usableHeightPrevious;
    private ViewGroup.LayoutParams frameLayoutParams;

    /**
     * 底部虚拟按钮
     *
     * @param content 内容布局
     */
    private NavigationBar(View content) {
        mChildOfContent = content;
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                possiblyResizeChildOfContent();
            }
        });
        frameLayoutParams = mChildOfContent.getLayoutParams();
    }

    /**
     * 适配底部状态栏
     *
     * @param activity 页面
     */
    public static void adapter(Activity activity) {
        if (NavigationBar.checkDeviceHasNavigationBar(activity)) {
            new NavigationBar(activity.findViewById(android.R.id.content));
        }
    }

    /**
     * 底部栏是否显示
     *
     * @param activity 页面
     * @return 是否显示
     */
    public static boolean isShowNavigationBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y != size.y;
        } else {
            boolean menu = ViewConfiguration.get(activity).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            if (menu || back) {
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * 底部栏高度
     *
     * @param activity 页面
     * @return 状态栏高度（int）
     */
    public static int navigationBarHeight(Activity activity) {
        if (!isShowNavigationBar(activity)) {
            return 0;
        }
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        //获取NavigationBar的高度
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    /**
     * 调整内容的子内容。
     */
    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            frameLayoutParams.height = usableHeightNow;
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    /**
     * 计算可用的高度
     *
     * @return 可用高度
     */
    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom);
    }

    /**
     * 检查是否存在底部虚拟按键
     *
     * @param context 上下文内容
     * @return 设备是否有底部虚拟按键
     */
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;
    }

}