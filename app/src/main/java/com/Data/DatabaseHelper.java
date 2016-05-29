package com.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by xsw on 2016/5/18 0018.
 */
public class DatabaseHelper  extends SQLiteOpenHelper {

    static String name="User.db";
    static int dbVersion=1;
    public static final String CREATE_User = "CREATE TABLE User  ("
            + "UserId INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "UserName STRING, "
            + "UserPicture STRING)";

    public static final String CREATE_Chat = "create table Chat ("
            + "ChatID INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "ChatButtom STRING, "
            + "ChatData TIMESTAMP default (datetime('now', 'localtime')), "
            + "ChatWho INT)";

    private Context mContext;

    public DatabaseHelper(Context context, String name,
                          SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_User);
        db.execSQL(CREATE_Chat);

   }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      switch(oldVersion) {
          case 1:
              db.execSQL(CREATE_Chat);
          case 2:
              db.execSQL(CREATE_Chat);

          default:
      }

    }


}
