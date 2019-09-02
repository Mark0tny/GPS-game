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
public class Message implements Serializable, Parcelable {

    public String body;
    public Date messageDate;
    public String username;
    public String eventName;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.body);
        dest.writeLong(this.messageDate != null ? this.messageDate.getTime() : -1);
        dest.writeString(this.username);
        dest.writeString(this.eventName);
    }

    protected Message(Parcel in) {
        this.body = in.readString();
        long tmpCommentDate = in.readLong();
        this.messageDate = tmpCommentDate == -1 ? null : new Date(tmpCommentDate);
        this.username = in.readString();
        this.eventName = in.readString();
    }

    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel source) {
            return new Message(source);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
}
