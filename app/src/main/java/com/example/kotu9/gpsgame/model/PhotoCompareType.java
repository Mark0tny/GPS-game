package com.example.kotu9.gpsgame.model;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@EqualsAndHashCode(callSuper = false)
@NonNull
@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor
public class PhotoCompareType extends Event implements Serializable {

	public String fileName;
	public String imageDirectoryPhone;
	public String imageURLfirebase;


	public PhotoCompareType(Event event) {
		super(event.id,event.name, event.description, event.hintList, event.difficulty, event.eventType, event.active, event.distance, event.rating,event.geofanceRadius,event.ranking,event.comments);
	}
}
