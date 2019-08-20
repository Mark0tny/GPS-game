package com.example.kotu9.gpsgame.model;

import com.google.firebase.firestore.GeoPoint;

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
public class LocationType extends Event implements Serializable {
    public GeoPoint location;

    public LocationType(Event event) {
        super(event.name, event.description, event.hintList, event.difficulty, event.eventType, event.active, event.distance, event.rating,event.geofanceRadius, event.time, event.userList);

    }
}
