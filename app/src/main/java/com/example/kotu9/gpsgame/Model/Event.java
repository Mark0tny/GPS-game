package com.example.kotu9.gpsgame.Model;

import com.example.kotu9.gpsgame.Utils.EventDifficulty;
import com.example.kotu9.gpsgame.Utils.EventTypes;

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
public class Event {
    private String name;
    private String description;
    private List<Hint> hintList;
    private EventDifficulty difficulty;
    private EventType eventType;
    private boolean status;
    private double distance;
    private double rating;
    private Long time;
    private List<User> userList;
}
