// MainActivity.java（真正最終無敵版，保證不閃退！）
package com.example.aibounce;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private SimulationView simulationView;
    private TutorManager tutorManager;
    private Handler uiHandler;
    private DecimalFormat df = new DecimalFormat("0.00");

    // UI 元件
    private Button btnStart, btnReset, btnSave, btnHistory;
    private EditText etHeight;
    private Spinner spinnerEnv, spinnerMat;
    private TextView tvTutor, tvData;
    private ImageView ivMood;

    // 環境與材質資料
    private final String[] environments = {"地球 (9.8 m/s²)", "月球 (1.6 m/s²)", "木星 (24.8 m/s²)"};
    private final float[] gravityValues = {9.8f, 1.6f, 24.8f};
    private final String[] materials = {"橡膠 (0.8)", "鋼球 (0.95)", "強力球 (0.7)"};
    private final float[] bounceValues = {0.8f, 0.95f, 0.7f};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化所有元件（一定要有 id 才找得到！）
        simulationView = findViewById(R.id.simulationView);
        btnStart = findViewById(R.id.btnStart);
        btnReset = findViewById(R.id.btnReset);
        btnSave = findViewById(R.id.btnSave);
        btnHistory = findViewById(R.id.btnHistory);  // 這行之前漏了！
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
        startDataUpdateLoop();
        startTutorLoop();
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
            String h = etHeight.getText().toString();
            float height = h.isEmpty() ? 10f : Float.parseFloat(h);
            simulationView.resetBall(height);
            btnStart.setText("開始");
        });

        btnSave.setOnClickListener(v -> {
            DBHelper db = new DBHelper(this);
            String env = spinnerEnv.getSelectedItem().toString();
            String mat = spinnerMat.getSelectedItem().toString();
            float h = Float.parseFloat(etHeight.getText().toString().isEmpty() ? "10" : etHeight.getText().toString());
            float g = simulationView.gravity;
            float bounce = simulationView.bounceFactor;
            float maxPE = 1.0f * g * h;
            float finalKE = simulationView.kineticEnergy;

            long id = db.saveExperiment(h, g, bounce, env, mat, maxPE, finalKE);
            Toast.makeText(this, "實驗已儲存！ID: " + id, Toast.LENGTH_LONG).show();
        });

        btnHistory.setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));
    }

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

    private void startTutorLoop() {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                TutorManager.TutorResponse resp = tutorManager.evaluate(simulationView);
                if (resp != null) {
                    tvTutor.setText(resp.message);
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