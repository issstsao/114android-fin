// TutorManager.java（完全修正版，已對應 SimulationView 最新命名）
package com.example.aibounce;

import java.util.Random;

public class TutorManager {
    private long lastSpeakTime = 0;
    private final Random random = new Random();

    public static class TutorResponse {
        public String message;
        public Mood mood;
        public TutorResponse(String m, Mood mood) {
            this.message = m;
            this.mood = mood;
        }
    }

    public enum Mood { NEUTRAL, EXCITED, THINKING, SAD }

    public TutorResponse evaluate(SimulationView view) {
        long now = System.currentTimeMillis();
        if (now - lastSpeakTime < 3500) return null; // 避免太吵

        float velocity = view.currentVelocity;           // 改用正確名稱
        float height = view.currentHeightMeters;         // 改用正確名稱
        boolean bounced = view.justBounced;

        // 碰撞瞬間
        if (bounced) {
            lastSpeakTime = now;
            return new TutorResponse("蹦！部分動能轉化為熱能與聲能了！", Mood.EXCITED);
        }

        // 最高點
        if (velocity < 0.15f && height > 20f) {
            lastSpeakTime = now;
            return new TutorResponse("看！在最高點，速度為零，位能達到最大值！", Mood.EXCITED);
        }

        // 完全靜止
        if (velocity < 0.03f && height < 1.0f) {
            lastSpeakTime = now;
            return new TutorResponse("能量因摩擦完全耗散…熵增定律勝利！", Mood.SAD);
        }

        // 隨機金句
        if (random.nextInt(25) == 0) {
            lastSpeakTime = now;
            return new TutorResponse("總機械能幾乎守恆，美極了～", Mood.THINKING);
        }

        return null;
    }
}