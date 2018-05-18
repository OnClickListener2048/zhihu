package com.example.fx50j.zhihu.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.fx50j.zhihu.bean.TopStories;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 * 作 者 : XYJ
 * 版 本 ： 1.0
 * 创建日期 ： 2016/7/4 10:27
 * 描 述 ：
 * 修订历史 ：
 * ============================================================
 **/
public class Dao {

    private DBHelper helper;
    private Context mContext;

    public Dao(Context mContext) {
        helper = new DBHelper(mContext);
        this.mContext = mContext;
    }

    /**
     * 插入收藏信息
     *
     * @param id
     * @param image
     * @param title
     * @return
     */
    public boolean insert(int id, String image, String title) {
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("image", image);
        values.put("title", title);
        long result = database.insert("my_favorites", null, values);
        database.close();
        if (result > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查询数据库中的数据
     *
     * @return
     */
    public List<TopStories> query() {
        SQLiteDatabase database = helper.getReadableDatabase();
        List<TopStories> listDate = new ArrayList<>();
        Cursor cursor = database.query("my_favorites", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String image = cursor.getString(cursor.getColumnIndex("image"));
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            TopStories stories = new TopStories(image, id, -1, title, 1, null);
            listDate.add(stories);
        }

        database.close();
        cursor.close();
        return listDate;
    }

    /**
     * 是否收藏过了
     *
     * @param urlID
     * @return
     */
    public boolean queryOnly(int urlID) {
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query("my_favorites", new String[]{"id"}, "id = ?", new String[]{urlID + ""}, null, null, null);
        if (cursor.moveToNext()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除收藏
     *
     * @param urlID
     * @return
     */
    public boolean delete(int urlID) {
        SQLiteDatabase database = helper.getWritableDatabase();
        int result = database.delete("my_favorites", "id=?", new String[]{urlID + ""});
        if (result > 0) {
            mContext.getContentResolver().notifyChange(Uri.parse("delete"),null);
            return true;
        } else {
            return false;
        }

    }
}
