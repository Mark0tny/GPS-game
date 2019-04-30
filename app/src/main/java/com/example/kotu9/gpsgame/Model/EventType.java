package com.example.kotu9.gpsgame.Model;

import com.example.kotu9.gpsgame.Utils.EnumEventTypes;

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

    public EnumEventTypes eventType;
    public double points;
}
