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
                "height REAL," +
                "gravity REAL," +
                "bounce REAL," +
                "env TEXT," +
                "material TEXT," +
                "max_pe REAL," +
                "final_ke REAL," +
                "timestamp TEXT," +
                "trajectory TEXT"+
                ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    // 改名成 insert（標準名稱）
    public long insert(float height, float g, float bounce, String env, String mat, float maxPE, float finalKE,String trajectory) {
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
        cv.put("trajectory", trajectory);
        return db.insert(TABLE, null, cv);
    }

    public Cursor getAllExperiments() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE, null, null, null, null, null, "_id DESC");
    }
}