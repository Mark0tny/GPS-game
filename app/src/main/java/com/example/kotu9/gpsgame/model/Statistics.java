package com.example.kotu9.gpsgame.model;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NonNull
@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor
public class Statistics implements Serializable {

    public String eventID;
    public String eventName;
    public long time;
    public double points;
}
