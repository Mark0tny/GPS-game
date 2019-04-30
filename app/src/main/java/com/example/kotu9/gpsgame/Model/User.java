package com.example.kotu9.gpsgame.Model;

import com.google.firebase.firestore.GeoPoint;

import java.util.Date;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;


@NonNull
@Data
@AllArgsConstructor(access=AccessLevel.PUBLIC)
@NoArgsConstructor
@Getter
@Setter
public class User {

    public String username,email,password,imageUrl,id;
    public double score;
    public Date regDate;
    public GeoPoint location;
    public List<Event> completeEvents;
    public List<Event> createdEvents;

}
