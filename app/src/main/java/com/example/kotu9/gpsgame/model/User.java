package com.example.kotu9.gpsgame.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
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
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor
@Getter
@Setter
public class User implements Serializable,Parcelable {

    public String username, email, password, imageUrl, id, role;
    public double score;
    public Date regDate;
    public GeoPoint location;

    public List<Statistics> completeEvents;
    public List<Event> createdEvents;
    public List<Message> messages;


    protected User(Parcel in) {
        username = in.readString();
        email = in.readString();
        password = in.readString();
        imageUrl = in.readString();
        id = in.readString();
        role = in.readString();
        score = in.readDouble();
        createdEvents = in.createTypedArrayList(Event.CREATOR);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.username);
        dest.writeString(this.email);
        dest.writeString(this.password);
        dest.writeString(this.imageUrl);
        dest.writeString(this.id);
        dest.writeString(this.role);
        dest.writeList(this.completeEvents);
        dest.writeList(this.createdEvents);
        dest.writeList(this.messages);
    }
}
