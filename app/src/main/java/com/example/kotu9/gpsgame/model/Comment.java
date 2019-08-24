package com.example.kotu9.gpsgame.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NonNull
@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor
public class Comment implements Serializable, Parcelable {
    public String body;
    public Date commentDate;
    public String username;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.body);
        dest.writeLong(this.commentDate != null ? this.commentDate.getTime() : -1);
        dest.writeString(this.username);
    }

    protected Comment(Parcel in) {
        this.body = in.readString();
        long tmpCommentDate = in.readLong();
        this.commentDate = tmpCommentDate == -1 ? null : new Date(tmpCommentDate);
        this.username = in.readString();
    }

    public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel source) {
            return new Comment(source);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
}
