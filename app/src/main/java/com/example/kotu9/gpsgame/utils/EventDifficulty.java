package com.example.kotu9.gpsgame.utils;

import android.graphics.Color;

import com.example.kotu9.gpsgame.R;

public enum EventDifficulty {
    Easy(Color.parseColor(String.valueOf(R.color.diffGreen))), Medium(Color.parseColor(String.valueOf(R.color.diffYellow))), Hard(Color.parseColor(String.valueOf(R.color.diffRed)));

    private int colorId;

    public int getColor() {
        return this.colorId;
    }

    EventDifficulty(int color) {
        this.colorId = color;
    }

}
