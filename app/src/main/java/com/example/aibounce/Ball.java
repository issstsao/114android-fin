package com.example.aibounce;

public class Ball {
    public float x;           // 中心點 x
    public float y;           // 中心點 y
    public float velocityY;   // 垂直速度（向下為正）
    public float radius = 30f;
    public int color = 0xFFFF5722; // 橘紅色

    // 能量（供外部讀取）
    public double potentialEnergy = 0.0;  // 位能 J
    public double kineticEnergy = 0.0;    // 動能 J

    public Ball(float x, float y) {
        this.x = x;
        this.y = y;
        this.velocityY = 0f;
    }
}