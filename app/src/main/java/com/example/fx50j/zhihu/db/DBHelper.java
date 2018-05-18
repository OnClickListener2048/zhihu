package com.example.fx50j.zhihu.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * ============================================================
 * 作 者 : XYJ
 * 版 本 ： 1.0
 * 创建日期 ： 2016/7/4 9:38
 * 描 述 ：
 * 修订历史 ：
 * ============================================================
 **/
public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "favorites.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("db create");
        db.execSQL("create table my_favorites(_id Integer primary key autoincrement,title char(20),image char(10),id Integer(5))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
