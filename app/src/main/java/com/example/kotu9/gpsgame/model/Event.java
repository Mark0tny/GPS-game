package com.example.kotu9.gpsgame.model;

import android.os.Parcel;
import android.os.Parcelable;

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
public class Event implements Serializable, Parcelable {

    public String id;
    public String name;
    public String description;
    public Hint hintList;
    public EventDifficulty difficulty;
    public EventType eventType;
    public boolean active;
    public double distance;
    public float rating;
    public float geofanceRadius;
    public long time;
    public List<User> userList;


    protected Event(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        hintList = in.readParcelable(Hint.class.getClassLoader());
        active = in.readByte() != 0;
        distance = in.readDouble();
        rating = in.readFloat();
        geofanceRadius = in.readFloat();
        time = in.readLong();

    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeParcelable(hintList, flags);
        dest.writeString(difficulty.name());
        dest.writeString(String.valueOf(eventType));
        dest.writeDouble(this.distance);
        dest.writeFloat(this.rating);
        dest.writeFloat(this.geofanceRadius);
        dest.writeLong(this.time);
        dest.writeList(this.userList);
    }
}
