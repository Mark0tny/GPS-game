package com.example.kotu9.gpsgame.model;

import com.example.kotu9.gpsgame.utils.EventDifficulty;

import java.io.Serializable;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NonNull
@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor
public class Event implements Serializable {
    public String name;
    public String description;
    public Hint hintList;
    public EventDifficulty difficulty;
    public EventType eventType;
    public boolean active;
    public double distance;
    public double rating;
    public float geofanceRadius;
    public Long time;
    public List<User> userList;
}
