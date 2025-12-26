package com.example.aibounce;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class MiniGraphView extends View {

    private Paint paintLine, paintBg;
    private Path path;
    private List<Float> times = new ArrayList<>();
    private List<Float> heights = new ArrayList<>();

    public MiniGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintLine.setColor(Color.parseColor("#FF5722")); // 橘色線條
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setStrokeWidth(3f);

        paintBg = new Paint();
        paintBg.setColor(Color.parseColor("#EEEEEE")); // 淺灰背景

        path = new Path();
    }

    // 接收資料庫取出的字串： "0.05,10.0|0.10,9.8|..."
    public void setTrajectoryData(String data) {
        times.clear();
        heights.clear();
        if (data == null || data.isEmpty()) {
            invalidate();
            return;
        }

        try {
            String[] points = data.split("\\|");
            for (String p : points) {
                String[] val = p.split(",");
                if (val.length == 2) {
                    times.add(Float.parseFloat(val[0]));
                    heights.add(Float.parseFloat(val[1]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        invalidate(); // 通知重畫
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();

        // 畫背景
        canvas.drawRect(0, 0, w, h, paintBg);

        if (times.size() < 2) return;

        // 找出最大值以便縮放
        float maxTime = times.get(times.size() - 1);
        float maxHeight = 0f;
        for (float f : heights) if (f > maxHeight) maxHeight = f;
        if (maxHeight == 0) maxHeight = 10f; // 避免除以 0

        path.reset();

        // 繪製路徑
        for (int i = 0; i < times.size(); i++) {
            float t = times.get(i);
            float yVal = heights.get(i);

            // 座標轉換：
            // X: 時間對應到寬度
            float x = (t / maxTime) * w;
            // Y: 高度對應到高度 (注意 Canvas Y軸是向下的，所以要反過來)
            float y = h - (yVal / maxHeight) * h;

            if (i == 0) path.moveTo(x, y);
            else path.lineTo(x, y);
        }

        canvas.drawPath(path, paintLine);
    }
}