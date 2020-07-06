package com.android.app.manager;

import android.app.Activity;
import android.content.Context;

import java.util.Iterator;
import java.util.Stack;

/**
 * Created by Relin on 2015/11/28.<br/>
 * Activity管理器<br/>
 * 对Activity页面进行管理，主要可以添加入栈，<br/>
 * 对栈里面的Activity进行管理，同时效果跟finish()<br/>
 * 方法一致，都是对页面进行销毁作用。<br/>
 */
public class ActivityManager {

    /**
     * 页面栈
     */
    private static Stack<Activity> mActivityStack;

    /**
     * 实例对象
     */
    private volatile static ActivityManager activityManager;

    /**
     * 构造函数
     */
    private ActivityManager() {

    }

    /**
     * 单一实例
     */
    public static ActivityManager getInstance() {
        if (activityManager == null) {
            synchronized (ActivityManager.class) {
                if (activityManager == null) {
                    activityManager = new ActivityManager();
                }
            }
        }
        return activityManager;
    }

    /**
     * 添加Activity到堆栈
     *
     * @param activity 页面
     */
    public void addActivity(Activity activity) {
        if (mActivityStack == null) {
            mActivityStack = new Stack<Activity>();
        }
        mActivityStack.add(activity);
    }

    /**
     * 获取栈顶Activity（堆栈中最后一个压入的）
     */
    public Activity getTopActivity() {
        Activity activity = mActivityStack.lastElement();
        return activity;
    }

    /**
     * 结束栈顶Activity（堆栈中最后一个压入的）
     */
    public void removeTopActivity() {
        Activity activity = mActivityStack.lastElement();
        removeActivity(activity);
    }

    /**
     * 结束指定的Activity
     *
     * @param activity 页面
     */
    public void removeActivity(Activity activity) {
        if (activity != null) {
            mActivityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     *
     * @param cls 类名的Activity
     */
    public void removeActivity(Class<?> cls) {
        Iterator<Activity> iterator = mActivityStack.iterator();
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            if (activity.getClass().equals(cls)) {
                iterator.remove();
                activity.finish();
                activity = null;
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void removeAllActivity() {
        for (int i = 0, size = mActivityStack.size(); i < size; i++) {
            if (null != mActivityStack.get(i)) {
                mActivityStack.get(i).finish();
            }
        }
        mActivityStack.clear();
    }

    /**
     * 退出应用程序
     *
     * @param context 上下文
     */
    public void AppExit(Context context) {
        try {
            removeAllActivity();
            android.app.ActivityManager activityMgr = (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.restartPackage(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {
        }
    }

}
