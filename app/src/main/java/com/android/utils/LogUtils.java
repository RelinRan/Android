package com.android.utils;

import android.util.Log;

/**
 * Created by Relin
 * on 2018-09-18.
 */
public class LogUtils {

    public static void e(String tag, String msg) {
        if (msg.length() > 4000) {
            for (int i = 0; i < msg.length(); i += 4000) {
                if (i + 4000 < msg.length())
                    Log.e(tag + i, msg.substring(i, i + 4000));
                else
                    Log.e(tag + i, msg.substring(i, msg.length()));
            }
        } else
            Log.e(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (msg.length() > 4000) {
            for (int i = 0; i < msg.length(); i += 4000) {
                if (i + 4000 < msg.length())
                    Log.i(tag + i, msg.substring(i, i + 4000));
                else
                    Log.i(tag + i, msg.substring(i, msg.length()));
            }
        } else
            Log.i(tag, msg);
    }

}
