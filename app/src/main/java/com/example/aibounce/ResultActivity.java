package com.example.aibounce;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView tvResult = findViewById(R.id.tvResult);
        Button btnBack = findViewById(R.id.btnBack);

        // 接收 MainActivity 傳來的資料
        Bundle bundle = getIntent().getExtras();
        double heightCm = bundle.getDouble("height");
        double timeSec = bundle.getDouble("time");
        double g = bundle.getDouble("g");

        // 美化顯示
        String resultText = String.format(
                "彈跳高度：%.1f cm\n" +
                        "落下時間：%.3f 秒\n" +
                        "計算重力加速度\n" +
                        "g ≈ %.3f m/s²\n\n" +
                        "（理論值 9.806 m/s²）",
                heightCm, timeSec, g
        );
        tvResult.setText(resultText);

        btnBack.setOnClickListener(v -> finish());  // 直接關閉此頁回到主畫面
    }
}