package com.example.aibounce;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SimulationView extends SurfaceView implements Runnable, SurfaceHolder.Callback {

    private Thread gameThread;
    private volatile boolean running = false;
    private SurfaceHolder holder;
    private Ball ball;
    private Paint paintGrid, paintBall, paintText;

    public float gravity = 9.8f;
    public float bounceFactor = 0.8f;
    public float airDamping = 0.995f;
    public float scaleFactor = 20f;
    private float worldHeightMeters = 50f;

    public float currentHeightMeters = 0f;
    public float currentVelocity = 0f;
    public float potentialEnergy = 0f;
    public float kineticEnergy = 0f;
    public float totalEnergy = 0f;
    public boolean justBounced = false;

    public SimulationView(Context context) {
        this(context, null);
    }

    public SimulationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = getHolder();
        holder.addCallback(this);

        paintGrid = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintGrid.setColor(Color.parseColor("#44FFFFFF"));
        paintGrid.setStrokeWidth(2);

        paintBall = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBall.setStyle(Paint.Style.FILL);

        paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(Color.WHITE);
        paintText.setTextSize(36);
        paintText.setTextAlign(Paint.Align.RIGHT);

        ball = new Ball();
    }

    public void resetBall(float heightMeters) {
        if (getHeight() == 0) return;
        scaleFactor = getHeight() / worldHeightMeters;
        ball.y = getHeight() - heightMeters * scaleFactor - ball.radius;
        ball.x = getWidth() / 2f;
        ball.velocityY = 0f;
        updateEnergy(heightMeters);
    }

    @Override
    public void run() {
        while (running) {
            if (!holder.getSurface().isValid()) continue;
            Canvas canvas = holder.lockCanvas();
            if (canvas != null) {
                synchronized (holder) {
                    updatePhysics();
                    drawScene(canvas);
                }
                holder.unlockCanvasAndPost(canvas);
            }
            try { Thread.sleep(16); } catch (InterruptedException e) { running = false; }
        }
    }

    private void updatePhysics() {
        float dt = 0.016f;
        ball.velocityY += (gravity / scaleFactor) * dt * 60f;
        ball.velocityY *= airDamping;
        ball.y += ball.velocityY * dt * 60f;

        justBounced = false;
        if (ball.y + ball.radius > getHeight()) {
            ball.y = getHeight() - ball.radius;
            ball.velocityY *= -bounceFactor;
            justBounced = true;
            if (Math.abs(ball.velocityY) < 2f) ball.velocityY = 0f;
        }

        currentHeightMeters = (getHeight() - ball.y - ball.radius) / scaleFactor;
        currentVelocity = Math.abs(ball.velocityY / scaleFactor);
        updateEnergy(currentHeightMeters);
    }

    private void updateEnergy(float h) {
        float m = 1f;
        potentialEnergy = m * gravity * h;
        kineticEnergy = 0.5f * m * currentVelocity * currentVelocity;
        totalEnergy = potentialEnergy + kineticEnergy;
    }

    private void drawScene(Canvas canvas) {
        canvas.drawColor(Color.parseColor("#001133"));
        for (int i = 0; i < canvas.getHeight(); i += 50) {
            canvas.drawLine(0, i, canvas.getWidth(), i, paintGrid);
            if (i % 100 == 0) {
                float m = (canvas.getHeight() - i) / scaleFactor;
                canvas.drawText(String.format("%.0fm", m), canvas.getWidth() - 20, i - 10, paintText);
            }
        }
        paintBall.setColor(ball.color);
        canvas.drawCircle(ball.x, ball.y, ball.radius, paintBall);
    }

    public void startSimulation() {
        if (!running) {
            running = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    public void pauseSimulation() { running = false; }
    public void resumeSimulation() { if (!running) startSimulation(); }
    public boolean isRunning() { return running; }

    @Override public void surfaceCreated(SurfaceHolder holder) {
        post(this::startSimulation);   // 關鍵修正在這一行！
    }

    @Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        scaleFactor = height / worldHeightMeters;
        resetBall(10f);
    }

    @Override public void surfaceDestroyed(SurfaceHolder holder) {
        pauseSimulation();
        if (gameThread != null) {
            try { gameThread.join(1000); } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    public class Ball {
        float x = 540f, y = 100f, velocityY = 0f, radius = 60f;
        int color = Color.parseColor("#FF5722");
    }
}