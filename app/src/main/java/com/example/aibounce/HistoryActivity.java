// HistoryActivity.java（真正最終版，已對應新 DBHelper）
package com.example.aibounce;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
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

        loadData();
    }

    private void loadData() {
        Cursor cursor = dbHelper.getAllExperiments();

        if (cursor == null || cursor.getCount() == 0) {
            Toast.makeText(this, "還沒有實驗記錄哦～快回去做實驗吧！", Toast.LENGTH_LONG).show();
            return;
        }
        //把原先的東西寫入單獨分頁(HistoryAdapter)
        HistoryAdapter adapter = new HistoryAdapter(this, cursor);

        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(); // 每次回來都刷新
    }
}