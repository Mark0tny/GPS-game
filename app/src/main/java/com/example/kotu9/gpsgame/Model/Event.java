package com.example.kotu9.gpsgame.Model;

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
class Event {

    private EventType eventType;
    private String name;
    private String description;
    private List<Hint> hintList;
    private double rating;
    private double distance;
    private String size;
    private boolean status;
    private Long time;
    private List<User> userList;

}
