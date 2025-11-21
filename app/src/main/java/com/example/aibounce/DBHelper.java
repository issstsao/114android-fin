package com.example.aibounce;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    // 資料庫名稱與版本
    private static final String DB_NAME = "bounce.db";
    private static final int DB_VERSION = 1;

    // 表格與欄位名稱
    public static final String TABLE_NAME = "records";
    public static final String COL_ID      = "_id";          // 自動遞增主鍵
    public static final String COL_HEIGHT  = "height";       // 高度 (cm)
    public static final String COL_TIME    = "time_sec";     // 時間 (秒)
    public static final String COL_G       = "gravity";      // 計算出的 g
    public static final String COL_DATE    = "date";         // 時間戳記

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID     + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_HEIGHT + " REAL, " +
                COL_TIME   + " REAL, " +
                COL_G      + " REAL, " +
                COL_DATE   + " TEXT" +
                ");";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // 新增一筆實驗記錄
    public long insert(double heightCm, double timeSec, double g) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_HEIGHT, heightCm);
        cv.put(COL_TIME, timeSec);
        cv.put(COL_G, g);
        cv.put(COL_DATE, System.currentTimeMillis() + ""); // 存毫秒
        long id = db.insert(TABLE_NAME, null, cv);
        db.close();
        return id;
    }

    // 取得全部資料（最新在最上面）
    public Cursor getAllRecords() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME, null, null, null, null, null, COL_ID + " DESC");
    }
}