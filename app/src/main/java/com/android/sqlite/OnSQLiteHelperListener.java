package com.android.sqlite;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Relin
 * on 2018-11-21.
 */
public interface OnSQLiteHelperListener {

    /**
     * 数据库第一次创建的时候调用
     * The first time the database is created
     *
     * @param db 数据库操作对象
     */
    void onCreate(SQLiteDatabase db);

    /**
     * 数据库修改需要升级的时候调用
     * Calls when database changes need to be upgraded
     *
     * @param db         数据库操作对象
     * @param oldVersion 数据库旧版本
     * @param newVersion 数据库升级版本
     */
    void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

}
