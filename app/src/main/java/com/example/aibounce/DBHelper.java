// DBHelper.java
package com.example.aibounce;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "dr_gravity.db";
    private static final int DB_VERSION = 1;
    public static final String TABLE = "experiments";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "height REAL," +           // 初始高度 (m)
                "gravity REAL," +          // 重力加速度
                "bounce REAL," +           // 彈性係數
                "env TEXT," +              // 環境名稱
                "material TEXT," +         // 材質名稱
                "max_pe REAL," +           // 最大位能
                "final_ke REAL," +         // 最後動能（耗散後）
                "timestamp TEXT" +         // 時間
                ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public long saveExperiment(float height, float g, float bounce, String env, String mat,
                               float maxPE, float finalKE) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("height", height);
        cv.put("gravity", g);
        cv.put("bounce", bounce);
        cv.put("env", env);
        cv.put("material", mat);
        cv.put("max_pe", maxPE);
        cv.put("final_ke", finalKE);
        cv.put("timestamp", System.currentTimeMillis() + "");
        return db.insert(TABLE, null, cv);
    }

    public Cursor getAllExperiments() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE, null, null, null, null, null, "_id DESC");
    }
}