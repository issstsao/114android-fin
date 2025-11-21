package com.example.aibounce;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class HistoryActivity extends AppCompatActivity {

    private ListView listView;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listView = findViewById(R.id.listView);
        dbHelper = new DBHelper(this);
        Button btnExport = findViewById(R.id.btnExport);

        btnExport.setOnClickListener(v -> {
            Cursor cursor = dbHelper.getAllRecords();
            ExportXml.export(this, cursor);
            cursor.close();
        });

        loadData();
    }

    private void loadData() {
        Cursor cursor = dbHelper.getAllRecords();

        String[] from = {DBHelper.COL_ID, DBHelper.COL_HEIGHT, DBHelper.COL_TIME,
                DBHelper.COL_G, DBHelper.COL_DATE};
        int[] to = {R.id.tvId, R.id.tvHeight, R.id.tvTime, R.id.tvG, R.id.tvDate};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this, R.layout.item_record, cursor, from, to, 0);

        // 讓日期顯示得漂亮一點
        adapter.setViewBinder((view, cursor1, columnIndex) -> {
            if (view.getId() == R.id.tvDate) {
                long timestamp = Long.parseLong(cursor1.getString(columnIndex));
                String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .format(new java.util.Date(timestamp));
                ((TextView) view).setText(date);
                return true;
            }
            return false;
        });

        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}