package com.example.kotu9.gpsgame.model;

import android.os.Parcel;
import android.os.Parcelable;

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
public class Statistics implements Serializable, Parcelable {

    public String eventID;
    public String eventName;
    public long time;
    public double points;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.eventID);
        dest.writeString(this.eventName);
        dest.writeLong(this.time);
        dest.writeDouble(this.points);
    }

    protected Statistics(Parcel in) {
        this.eventID = in.readString();
        this.eventName = in.readString();
        this.time = in.readLong();
        this.points = in.readDouble();
    }

    public static final Parcelable.Creator<Statistics> CREATOR = new Parcelable.Creator<Statistics>() {
        @Override
        public Statistics createFromParcel(Parcel source) {
            return new Statistics(source);
        }

        @Override
        public Statistics[] newArray(int size) {
            return new Statistics[size];
        }
    };
}
