package com.example.kotu9.gpsgame.model;

import android.os.Parcel;
import android.os.Parcelable;

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
public class Hint implements Parcelable {

    public List<String> hints;


    protected Hint(Parcel in) {
        hints = in.createStringArrayList();
    }

    public static final Creator<Hint> CREATOR = new Creator<Hint>() {
        @Override
        public Hint createFromParcel(Parcel in) {
            return new Hint(in);
        }

        @Override
        public Hint[] newArray(int size) {
            return new Hint[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.hints);
    }
}
