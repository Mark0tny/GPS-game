package com.example.kotu9.gpsgame.model;

import android.os.Parcel;
import android.os.Parcelable;

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
public class Hint implements Serializable, Parcelable {

    public List<String> hints;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.hints);
    }

    protected Hint(Parcel in) {
        this.hints = in.createStringArrayList();
    }

    public static final Parcelable.Creator<Hint> CREATOR = new Parcelable.Creator<Hint>() {
        @Override
        public Hint createFromParcel(Parcel source) {
            return new Hint(source);
        }

        @Override
        public Hint[] newArray(int size) {
            return new Hint[size];
        }
    };
}
