package com.example.aibounce;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etHeight;
    private Button btnStartTimer, btnCalculate, btnHistory;
    private TextView tvTimer;

    private long startMillis = 0;
    private boolean isTiming = false;
    private double fallTimeSeconds = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 綁定元件
        etHeight       = findViewById(R.id.etHeight);
        btnStartTimer  = findViewById(R.id.btnStartTimer);
        tvTimer        = findViewById(R.id.tvTimer);
        btnCalculate  = findViewById(R.id.btnCalculate);
        btnHistory     = findViewById(R.id.btnHistory);

        // 計時按鈕
        btnStartTimer.setOnClickListener(v -> {
            if (!isTiming) {
                // 第一次按：開始計時
                startMillis = SystemClock.uptimeMillis();
                isTiming = true;
                btnStartTimer.setText("落地了！快按我停止");
            } else {
                // 第二次按：停止計時
                fallTimeSeconds = (SystemClock.uptimeMillis() - startMillis) / 1000.0;
                tvTimer.setText(String.format("%.3f 秒", fallTimeSeconds));
                btnCalculate.setEnabled(true);
                isTiming = false;
                btnStartTimer.setText("重新計時");
            }
        });

        // 計算按鈕
        btnCalculate.setOnClickListener(v -> {
            String hStr = etHeight.getText().toString().trim();
            if (hStr.isEmpty()) {
                Toast.makeText(this, "請輸入高度", Toast.LENGTH_SHORT).show();
                return;
            }

            double heightCm = Double.parseDouble(hStr);
            double heightM = heightCm / 100.0;

            if (fallTimeSeconds <= 0) {
                Toast.makeText(this, "請先完成計時", Toast.LENGTH_SHORT).show();
                return;
            }

            // 公式：g = 2h / t²
            double g = (2 * heightM) / (fallTimeSeconds * fallTimeSeconds);

            // 存進資料庫
            DBHelper db = new DBHelper(this);
            db.insert(heightCm, fallTimeSeconds, g);

            // 跳到結果頁
            Intent intent = new Intent(MainActivity.this, ResultActivity.class);
            intent.putExtra("height", heightCm);
            intent.putExtra("time", fallTimeSeconds);
            intent.putExtra("g", g);
            startActivity(intent);

            // 重置畫面
            fallTimeSeconds = 0;
            tvTimer.setText("0.000 秒");
            btnCalculate.setEnabled(false);
            btnStartTimer.setText("開始計時（放手時按）");
        });

        // 歷史記錄按鈕
        btnHistory.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, HistoryActivity.class)));
    }
}