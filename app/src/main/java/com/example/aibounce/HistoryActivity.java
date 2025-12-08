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

        // 對應資料表欄位名稱（直接用字串，因為新版 DBHelper 沒有 static 常數）
        String[] from = {"_id", "env", "material", "height", "gravity", "max_pe", "final_ke", "timestamp"};
        int[] to = {R.id.tvId, R.id.tvEnv, R.id.tvMat, R.id.tvHeight, R.id.tvG, R.id.tvMaxPE, R.id.tvFinalKE, R.id.tvTime};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this, R.layout.item_experiment, cursor, from, to, 0);

        // 美化時間格式
        adapter.setViewBinder((view, cursor1, columnIndex) -> {
            if (view.getId() == R.id.tvTime) {
                String ts = cursor1.getString(columnIndex);
                String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .format(new java.util.Date(Long.parseLong(ts)));
                ((android.widget.TextView) view).setText("時間：" + date);
                return true;
            }
            return false;
        });

        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(); // 每次回來都刷新
    }
}