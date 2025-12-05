// MainActivity.java（Dr. Gravity 完整專業版）
package com.example.aibounce;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private SimulationView simulationView;
    private TutorManager tutorManager;
    private Handler uiHandler;
    private DecimalFormat df = new DecimalFormat("0.00");

    // UI 元件
    private Button btnStart, btnReset, btnSave;
    private EditText etHeight;
    private Spinner spinnerEnv, spinnerMat;
    private TextView tvTutor, tvData;
    private ImageView ivMood;

    // 環境與材質設定
    private final String[] environments = {"地球 (9.8 m/s²)", "月球 (1.6 m/s²)", "木星 (24.8 m/s²)"};
    private final float[] gravityValues = {9.8f, 1.6f, 24.8f};
    private final String[] materials = {"橡膠 (彈性0.8)", "鋼球 (彈性0.95)", "強力球 (彈性0.7)"};
    private final float[] bounceValues = {0.8f, 0.95f, 0.7f};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化元件
        simulationView = findViewById(R.id.simulationView);
        btnStart = findViewById(R.id.btnStart);
        btnReset = findViewById(R.id.btnReset);
        btnSave = findViewById(R.id.btnSave);
        etHeight = findViewById(R.id.etHeight);
        spinnerEnv = findViewById(R.id.spinnerEnv);
        spinnerMat = findViewById(R.id.spinnerMat);
        tvTutor = findViewById(R.id.tvTutor);
        tvData = findViewById(R.id.tvData);
        ivMood = findViewById(R.id.ivMood);

        tutorManager = new TutorManager();
        uiHandler = new Handler(Looper.getMainLooper());

        setupSpinners();
        setupButtons();
        startDataUpdateLoop();   // 開始即時更新數據
        startTutorLoop();        // 開始 AI 導師說話
    }

    private void setupSpinners() {
        ArrayAdapter<String> envAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, environments);
        envAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEnv.setAdapter(envAdapter);
        spinnerEnv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                simulationView.gravity = gravityValues[pos];
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        ArrayAdapter<String> matAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, materials);
        matAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMat.setAdapter(matAdapter);
        spinnerMat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                simulationView.bounceFactor = bounceValues[pos];
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupButtons() {
        btnStart.setOnClickListener(v -> {
            if (simulationView.isRunning()) {
                simulationView.pauseSimulation();
                btnStart.setText("繼續");
            } else {
                simulationView.resumeSimulation();
                btnStart.setText("暫停");
            }
        });

        btnReset.setOnClickListener(v -> {
            String hStr = etHeight.getText().toString();
            float height = hStr.isEmpty() ? 10f : Float.parseFloat(hStr);
            simulationView.resetBall(height);
            btnStart.setText("開始");
        });

        btnSave.setOnClickListener(v -> {
            // 之後會串 DBHelper
            Toast.makeText(this, "實驗已儲存！（功能開發中）", Toast.LENGTH_SHORT).show();
        });
    }

    // 每 100ms 更新一次畫面數據
    private void startDataUpdateLoop() {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                String data = String.format(
                        "高度: %.2f m\n速度: %.2f m/s\n位能: %.2f J\n動能: %.2f J\n總能量: %.2f J",
                        simulationView.currentHeightMeters,
                        simulationView.currentVelocity,
                        simulationView.potentialEnergy,
                        simulationView.kineticEnergy,
                        simulationView.totalEnergy
                );
                tvData.setText(data);
                uiHandler.postDelayed(this, 100);
            }
        });
    }

    // 每 800ms 讓 Dr. Gravity 說話
    private void startTutorLoop() {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                TutorManager.TutorResponse resp = tutorManager.evaluate(simulationView);
                if (resp != null) {
                    tvTutor.setText(resp.message);
                    // 之後可以換圖示
                }
                uiHandler.postDelayed(this, 800);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        simulationView.pauseSimulation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        simulationView.resumeSimulation();
    }
}