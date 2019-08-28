package com.example.kotu9.gpsgame.utils;

import android.graphics.Color;

public enum EventDifficulty {
	Easy(Color.parseColor("#3C8A40")), Medium(Color.parseColor("#FFC107")), Hard(Color.parseColor("#C00F0F"));

	private int colorId;

	public int getColor() {
		return this.colorId;
	}

	EventDifficulty(int color) {
		this.colorId = color;
	}

}
