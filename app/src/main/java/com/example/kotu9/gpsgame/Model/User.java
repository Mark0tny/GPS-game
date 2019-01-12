package com.example.kotu9.gpsgame.Model;

import java.util.Date;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;


@NonNull
@Data
@AllArgsConstructor(access=AccessLevel.PUBLIC)
@NoArgsConstructor
public class User {

    public String username,email,password,imageUrl,id;
    public double score;
    public Date regDate;
    public Location location;
    public List<Event> completeEvents;
    public List<Event> createdEvents;


}
