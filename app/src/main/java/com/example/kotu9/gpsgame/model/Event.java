package com.example.kotu9.gpsgame.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.kotu9.gpsgame.utils.EventDifficulty;
import com.example.kotu9.gpsgame.utils.EventTypes;

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
    public List<User> ranking;
    public List<Comment> comments;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeParcelable(this.hintList, flags);
        dest.writeInt(this.difficulty == null ? -1 : this.difficulty.ordinal());
        dest.writeParcelable(this.eventType, flags);
        dest.writeByte(this.active ? (byte) 1 : (byte) 0);
        dest.writeDouble(this.distance);
        dest.writeFloat(this.rating);
        dest.writeFloat(this.geofanceRadius);
        dest.writeTypedList(this.ranking);
        dest.writeTypedList(this.comments);
    }

    protected Event(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.hintList = in.readParcelable(Hint.class.getClassLoader());
        int tmpDifficulty = in.readInt();
        this.difficulty = tmpDifficulty == -1 ? null : EventDifficulty.values()[tmpDifficulty];
        this.eventType = in.readParcelable(EventType.class.getClassLoader());
        this.active = in.readByte() != 0;
        this.distance = in.readDouble();
        this.rating = in.readFloat();
        this.geofanceRadius = in.readFloat();
        this.ranking = in.createTypedArrayList(User.CREATOR);
        this.comments = in.createTypedArrayList(Comment.CREATOR);
    }

    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}
