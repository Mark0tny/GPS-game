package com.example.kotu9.gpsgame.Model;

import com.example.kotu9.gpsgame.Utils.EventTypes;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NonNull
@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor
public class EventType {

	public EventTypes eventType;
	public double points;

	public EventType(EventTypes eventType) {
		this.eventType = eventType;
		switch (eventType.ordinal()) {
			case 0:
				this.points = 80;
				break;
			case 1:
				this.points = 100;
				break;
			case 2:
				this.points = 100;
				break;
			case 3:
				this.points = 60;
				break;
		}
	}

	;
}
