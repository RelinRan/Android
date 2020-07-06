package com.android.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.android.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ice on 2016/3/26.
 * APP工具
 */
public class App {

    /**
     * 判断当前界面是否是桌面
     */
    public static boolean isHome(Context context) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
        List<String> strs = getHomes(context);
        if (strs != null && strs.size() > 0) {
            return strs.contains(rti.get(0).topActivity.getPackageName());
        } else {
            return false;
        }
    }

    /**
     * 获得属于桌面的应用的应用包名称
     *
     * @return 返回包含所有包名的字符串列表
     */
    private static List<String> getHomes(Context context) {
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = context.getPackageManager();
        //属性
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfo) {
            names.add(ri.activityInfo.packageName);
            Log.i(context.getResources().getString(R.string.frame_name), " getHomes packageName =" + ri.activityInfo.packageName);
        }
        return names;
    }


    /**
     * 打开App
     *
     * @param context
     * @param appPackageName app包名
     * @param bundle         给app栈定Actitivy传递的参数
     */
    public static void openBackgroundRunningApp(Context context, String appPackageName, Bundle bundle) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName appTopActivity = null;
        List<ActivityManager.RunningTaskInfo> infos = manager.getRunningTasks(100);//获取当前正在运行的应用列表
        for (ActivityManager.RunningTaskInfo info : infos) {
            //判断原app是否还在运行
            if (info.topActivity.getPackageName().equals(appPackageName) && info.baseActivity.getPackageName().equals(appPackageName)) {
                appTopActivity = info.topActivity;
            }
        }
        if (isRunningBackground(context, appPackageName)) {
            Intent intent = new Intent();
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //在receiver或者service里新建activity都要添加这个属性，
            intent.setComponent(appTopActivity);
            //使用addFlags,而不是setFlags
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //清除掉Task栈需要显示Activity之上的其他activity
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); //加上这个才不会新建立一个Activity，而是显示旧的
            context.startActivity(intent);
        }
    }

    /**
     * 打开新实例APP
     *
     * @param context
     * @param componentName 包名,AndroidKit.class
     * @param param         需要传递的参数
     */
    private void openNewInstanceApp(Context context, ComponentName componentName, Bundle param) {
        if (componentName != null) {
            PackageInfo packageInfo;
            try {
                packageInfo = context.getPackageManager().getPackageInfo(componentName.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                packageInfo = null;
                Toast.makeText(context, "您手机上还未安装该APP!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            try {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setComponent(componentName);
                if (param != null) {
                    intent.putExtras(param); // 把Bundle塞入Intent里面
                }
                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(context, "APP启动异常!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 判断App是否在前端或后台运行运行
     *
     * @param context
     * @param packageName 程序包名
     * @return
     */
    public static boolean isRunningForeground(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RecentTaskInfo> appTask = activityManager.getRecentTasks(Integer.MAX_VALUE, 1);
        if (appTask == null) {
            return false;
        }
        if (appTask.get(0).baseIntent.toString().contains(packageName)) {
            return true;
        }
        return false;
    }

    /**
     * 判断APP是否在后台运行
     *
     * @param context
     * @param appPackageName 应用的包名
     * @return
     */
    public static boolean isRunningBackground(Context context, String appPackageName) {
        boolean isInBackground = false;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName appTopActivity = null;
        //如果是Android 5.0以上
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = manager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                //前台程序
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {//如果是Android 5.0以下
            List<ActivityManager.RunningTaskInfo> infos = manager.getRunningTasks(100);//获取当前正在运行的应用列表
            for (ActivityManager.RunningTaskInfo info : infos) {
                //判断原app是否还在运行
                if (info.topActivity.getPackageName().equals(appPackageName) && info.baseActivity.getPackageName().equals(appPackageName)) {
                    appTopActivity = info.topActivity;
                }
            }
            if (appTopActivity != null) {
                isInBackground = true;
            } else {
                isInBackground = false;
            }
        }
        return isInBackground;

    }

    /**
     * 获取Assets中的文件
     *
     * @param fileName 文件名
     * @param folder   存放在手机内存中的文件夹名
     * @return
     */
    public static File openFileFromAssets(String fileName, String folder) {
        File dir;
        File file = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //文件夹
            dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + folder);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            file = new File(dir + File.separator + fileName);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            InputStream inputStream = App.class.getClassLoader().getResourceAsStream("assets/" + fileName);
            //Activity中this.getClass().getClassLoader().getResourceAsStream
            try {
                FileOutputStream outputStream = new FileOutputStream(file);
                int hasRead = 0;
                byte[] bytes = new byte[1024];
                while ((hasRead = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, hasRead);
                    outputStream.flush();
                }
                inputStream.close();
                outputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("Elven", "get file from assets during sdcard is not exist please check again");
        }
        return file;
    }

    /**
     * 获取版本号
     *
     * @param context 上下文对象
     * @return
     */
    public static String getVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    /**
     * 获取版本
     *
     * @param context 上下文对象
     * @return
     */
    public static int getVersionCode(Context context) {
        int versionCode = 1;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionCode = pi.versionCode;
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionCode;
    }

    /**
     * 安装APK
     *
     * @param context 上下文对象
     * @param file    apk文件
     */
    public static void installApk(Context context, File file) {
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 返回主页
     *
     * @param context 上下文对象
     */
    public static void backHome(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 获取包名
     *
     * @param context 上下文对象
     * @return
     */
    public static String getPackageName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取启动类名
     *
     * @param context 上下文对象
     * @return
     */
    public static String getLauncherClassName(Context context) {
        String launcherClassName = "";
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfoList) {
            if (context != null && context.getPackageName().equalsIgnoreCase(resolveInfo.activityInfo.applicationInfo.packageName)) {
                launcherClassName = resolveInfo.activityInfo.name;
                break;
            }
        }
        Log.i(Badge.class.getSimpleName(), "launcherClassName:" + launcherClassName);
        return launcherClassName;
    }

}
