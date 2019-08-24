package com.example.kotu9.gpsgame.utils;

import android.graphics.Color;

public enum EventDifficulty {
    Easy(Color.GREEN), Medium(Color.YELLOW), Hard(Color.RED);

    private int colorId;

    public int getColor() {
        return this.colorId;
    }

    EventDifficulty(int color) {
        this.colorId = color;
    }

}
