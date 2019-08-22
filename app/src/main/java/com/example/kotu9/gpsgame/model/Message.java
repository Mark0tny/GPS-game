package com.example.kotu9.gpsgame.model;

import java.io.Serializable;
import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NonNull
@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor
public class Message implements Serializable {

    public String topic;
    public String body;
    public Date commentDate;
    public String username;
    public String eventName;
}
