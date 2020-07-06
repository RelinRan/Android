package com.android.app.manager;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.android.app.AppConstant;
import com.android.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Relin
 * on 2018-10-11.
 * 权限管理
 */
public class PermissionsManager {

    /**
     * 是否需要检查运行时权限
     *
     * @return
     */
    public static boolean isNeedCheckRunTimePermissions() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 检查运行时权限
     *
     * @param activity    Activity页面
     * @param permissions 权限
     * @param requestCode 请求代码
     * @param listener    请求监听
     */
    public static void checkRunTimePermissions(Activity activity, String[] permissions, int requestCode, OnRequestPermissionsListener listener) {
        checkRunTimePermission(activity, permissions, requestCode, listener);
    }

    /**
     * 检查运行时权限
     *
     * @param fragment
     * @param permissions 权限
     * @param requestCode 请求代码
     * @param listener    请求监听
     */
    public static void checkRunTimePermissions(Fragment fragment, String[] permissions, int requestCode, OnRequestPermissionsListener listener) {
        checkRunTimePermission(fragment, permissions, requestCode, listener);
    }

    /**
     * 检查运行时权限
     *
     * @param obj
     * @param permissions 权限
     * @param requestCode 请求代码
     * @param listener    请求监听
     */
    private static void checkRunTimePermission(Object obj, String[] permissions, int requestCode, OnRequestPermissionsListener listener) {
        if (!isNeedCheckRunTimePermissions()) {
            if (listener != null) {
                int grantResults[] = new int[permissions.length];
                for (int i = 0; i < permissions.length; i++) {
                    grantResults[i] = PackageManager.PERMISSION_DENIED;
                }
                listener.onRequestPermissionsSucceed(requestCode, permissions, grantResults);
            }
            return;
        }
        Activity activity = null;
        Fragment fragment = null;
        if (obj instanceof Activity) {
            activity = (Activity) obj;
        }
        if (obj instanceof Fragment) {
            fragment = (Fragment) obj;
        }
        if (!isNeedCheckRunTimePermissions()) {
            if (listener != null) {
                int grantResults[] = new int[permissions.length];
                for (int i = 0; i < permissions.length; i++) {
                    grantResults[i] = PackageManager.PERMISSION_DENIED;
                }
                listener.onRequestPermissionsSucceed(requestCode, permissions, grantResults);
            }
            return;
        }
        if (permissions == null) {
            return;
        }
        if (permissions.length == 0) {
            return;
        }
        List<String> list = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            Context context = activity == null ? fragment.getActivity() : activity;
            if (ContextCompat.checkSelfPermission(context, permissions[i]) == PackageManager.PERMISSION_DENIED) {
                list.add(permissions[i]);
            }
        }
        int size = ListUtils.getSize(list);
        if (size != 0) {
            String[] denied = new String[size];
            for (int i = 0; i < size; i++) {
                denied[i] = list.get(i);
            }
            if (denied.length != 0) {
                if (permissions == null) {
                    return;
                }
                if (permissions.length == 0) {
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (activity != null) {
                        ActivityCompat.requestPermissions(activity, permissions, requestCode);
                    }
                    if (fragment != null) {
                        fragment.requestPermissions(permissions, requestCode);
                    }
                }
            }
        } else {
            int grantResults[] = new int[permissions.length];
            for (int i = 0; i < permissions.length; i++) {
                grantResults[i] = PackageManager.PERMISSION_DENIED;
            }
            if (listener != null) {
                listener.onRequestPermissionsSucceed(requestCode, permissions, grantResults);
            }
        }
    }


    /**
     * 请求权限处理
     *
     * @param requestCode  请求代码
     * @param permissions  权限
     * @param grantResults 权限状态
     * @param listener     请求监听
     */
    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults, OnRequestPermissionsListener listener) {
        if (listener == null) {
            return;
        }
        switch (requestCode) {
            case AppConstant.REQUEST_CODE_PERMISSIONS: {
                boolean isGrant = true;
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                            isGrant = false;
                        }
                    }
                }
                if (grantResults.length > 0 && isGrant) {//成功的
                    listener.onRequestPermissionsSucceed(requestCode, permissions, grantResults);
                } else {//失败
                    listener.onRequestPermissionsFailed(requestCode, permissions, grantResults);
                }
                return;
            }
        }
    }

    /**
     * 权限请求监听
     */
    public interface OnRequestPermissionsListener {

        /**
         * 请求权限成功
         *
         * @param requestCode  请求码
         * @param permissions  权限集合
         * @param grantResults 获取权限结果
         */
        void onRequestPermissionsSucceed(int requestCode, String[] permissions, int[] grantResults);

        /**
         * 请求权限失败
         *
         * @param requestCode  请求码
         * @param permissions  权限集合
         * @param grantResults 获取权限结果
         */
        void onRequestPermissionsFailed(int requestCode, String[] permissions, int[] grantResults);
    }

}
