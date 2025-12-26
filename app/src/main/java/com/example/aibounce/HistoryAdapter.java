package com.example.aibounce;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HistoryAdapter extends CursorAdapter {

    public HistoryAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_experiment, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // 1. 綁定文字欄位
        TextView tvId = view.findViewById(R.id.tvId);
        TextView tvEnv = view.findViewById(R.id.tvEnv);
        TextView tvMat = view.findViewById(R.id.tvMat);
        TextView tvHeight = view.findViewById(R.id.tvHeight);
        TextView tvG = view.findViewById(R.id.tvG);
        TextView tvMaxPE = view.findViewById(R.id.tvMaxPE);
        TextView tvFinalKE = view.findViewById(R.id.tvFinalKE);
        TextView tvTime = view.findViewById(R.id.tvTime);
        MiniGraphView graphView = view.findViewById(R.id.graphView); // 綁定圖表

        // 2. 讀取數據
        String id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
        String env = cursor.getString(cursor.getColumnIndexOrThrow("env"));
        String mat = cursor.getString(cursor.getColumnIndexOrThrow("material"));
        double h = cursor.getDouble(cursor.getColumnIndexOrThrow("height"));
        double g = cursor.getDouble(cursor.getColumnIndexOrThrow("gravity"));
        double pe = cursor.getDouble(cursor.getColumnIndexOrThrow("max_pe"));
        double ke = cursor.getDouble(cursor.getColumnIndexOrThrow("final_ke"));
        String ts = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"));
        String trajectory = cursor.getString(cursor.getColumnIndexOrThrow("trajectory"));

        // 3. 設定顯示文字
        tvId.setText("編號：" + id);
        tvEnv.setText("環境：" + env);
        tvMat.setText("材質：" + mat);
        tvHeight.setText(String.format("初始高度：%.1f m", h));
        tvG.setText(String.format("重力：%.1f m/s²", g));
        tvMaxPE.setText(String.format("最大位能：%.1f J", pe));
        tvFinalKE.setText(String.format("最終動能：%.1f J", ke));

        try {
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(new Date(Long.parseLong(ts)));
            tvTime.setText("時間：" + date);
        } catch (Exception e) {
            tvTime.setText("時間：未知");
        }

        // 4. 設定圖表數據 (關鍵步驟)
        graphView.setTrajectoryData(trajectory);
    }
}