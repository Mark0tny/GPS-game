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
public class Rating implements Serializable, Parcelable {

    private float globalRating;
    private float[] usersRating;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.globalRating);
        dest.writeFloatArray(this.usersRating);
    }

    protected Rating(Parcel in) {
        this.globalRating = in.readFloat();
        this.usersRating = in.createFloatArray();
    }

    public static final Creator<Rating> CREATOR = new Creator<Rating>() {
        @Override
        public Rating createFromParcel(Parcel source) {
            return new Rating(source);
        }

        @Override
        public Rating[] newArray(int size) {
            return new Rating[size];
        }
    };
}
