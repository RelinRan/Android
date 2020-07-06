package com.android.utils;

import android.content.Context;

import com.android.R;

/**
 * Created by Relin
 * on 2018/5/15.
 * 程序架构授权
 */

public class Authorization {

    public static void main(String[] args){
        System.out.print(""+ Encryptor.encode("com.rainwood.rescue"));
    }

    /**
     * 检查程序是否授权
     * @param context
     * @return
     */
    public static boolean isAuth(Context context) {
        String packageName = context.getApplicationContext().getPackageName();
        String encode = context.getResources().getString(R.string.package_name);
        String decode = Encryptor.decode(encode);
        if (packageName.equals(decode)) {
            return true;
        }
        new RuntimeException("This framework requires permission, you do not have permission to use.").printStackTrace();
        return false;
    }

}
