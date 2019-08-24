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
public class User implements Serializable, Parcelable {

    public String username, email, password, imageUrl, id, role;
    public double score;
    public Date regDate;
    public GeoPoint location;

    public List<Statistics> completeEvents;
    public List<Event> createdEvents;
    public List<Message> messages;


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
        dest.writeDouble(this.score);
        dest.writeLong(this.regDate != null ? this.regDate.getTime() : -1);
        dest.writeTypedList(this.completeEvents);
        dest.writeTypedList(this.createdEvents);
        dest.writeTypedList(this.messages);
    }

    protected User(Parcel in) {
        this.username = in.readString();
        this.email = in.readString();
        this.password = in.readString();
        this.imageUrl = in.readString();
        this.id = in.readString();
        this.role = in.readString();
        this.score = in.readDouble();
        long tmpRegDate = in.readLong();
        this.regDate = tmpRegDate == -1 ? null : new Date(tmpRegDate);
        this.completeEvents = in.createTypedArrayList(Statistics.CREATOR);
        this.createdEvents = in.createTypedArrayList(Event.CREATOR);
        this.messages = in.createTypedArrayList(Message.CREATOR);
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
