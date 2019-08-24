package com.example.kotu9.gpsgame.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.kotu9.gpsgame.utils.EventTypes;

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
public class EventType implements Serializable, Parcelable {

    public EventTypes eventType;
    public double points;

    public EventType(EventTypes eventType) {
        this.eventType = eventType;
        switch (eventType.ordinal()) {
            case 0:
                this.points = 80;
                break;
            case 1:
                this.points = 100;
                break;
            case 2:
                this.points = 100;
                break;
            case 3:
                this.points = 60;
                break;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.eventType == null ? -1 : this.eventType.ordinal());
        dest.writeDouble(this.points);
    }

    protected EventType(Parcel in) {
        int tmpEventType = in.readInt();
        this.eventType = tmpEventType == -1 ? null : EventTypes.values()[tmpEventType];
        this.points = in.readDouble();
    }

    public static final Parcelable.Creator<EventType> CREATOR = new Parcelable.Creator<EventType>() {
        @Override
        public EventType createFromParcel(Parcel source) {
            return new EventType(source);
        }

        @Override
        public EventType[] newArray(int size) {
            return new EventType[size];
        }
    };
}
