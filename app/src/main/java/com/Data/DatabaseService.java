package com.Data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.android_qqfix.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/5/29 0029.
 */
public class DatabaseService {


    private Context mContext;
    private DatabaseHelper dbHelper;

    public DatabaseService(Context context) {
        mContext = context;
        dbHelper = new DatabaseHelper(mContext, "User.db", null, 2);
    }




    // 查询记录的总数
    public int getCount() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "select count(*) from Chat";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        int length = c.getInt(0);
        c.close();
        return length;
    }

    /**
     * 分页查询
     *
     * @param currentPage 当前页
     * @param pageSize 每页显示的记录
     * @return 当前页的记录
     */
    public ArrayList<String> getAllItems(int currentPage, int pageSize) {
        int firstResult = (currentPage - 1) * pageSize;
        int maxResult = currentPage * pageSize;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "select * from Chat order by ChatID desc limit ?,? ";
        Cursor mCursor = db.rawQuery(
                sql,
                new String[] { String.valueOf(firstResult),
                        String.valueOf(pageSize) });
       ArrayList<String> items = new ArrayList<String>();
        int columnCount  = mCursor.getColumnCount();
        while (mCursor.moveToNext()) {
            String ChatButtom = mCursor.getString(mCursor
                    .getColumnIndexOrThrow("ChatButtom"));

            String ChatData = mCursor.getString(mCursor
                    .getColumnIndexOrThrow("ChatData"));
            int  Chat = mCursor.getInt(mCursor
                    .getColumnIndexOrThrow("ChatWho"));
           String  ChatWho= Chat+"";
            items.add(ChatButtom);
            items.add(ChatData);
            items.add(ChatWho);

        }
        //不要关闭数据库
        return items;
    }



}
