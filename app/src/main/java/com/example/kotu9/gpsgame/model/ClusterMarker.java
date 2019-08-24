package com.example.kotu9.gpsgame.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.android.clustering.ClusterItem;

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
public class ClusterMarker implements ClusterItem, Serializable, Parcelable {

    private LatLng position;
    private String title;
    private String snippet;
    private int iconPicture;
    private Event event;
    private User owner;
    private GeoPoint position2;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.snippet);
        dest.writeInt(this.iconPicture);
        dest.writeParcelable(this.event, flags);
        dest.writeParcelable(this.owner, flags);
    }

    protected ClusterMarker(Parcel in) {
        this.title = in.readString();
        this.snippet = in.readString();
        this.iconPicture = in.readInt();
        this.event = in.readParcelable(Event.class.getClassLoader());
        this.owner = in.readParcelable(User.class.getClassLoader());
    }

    public static final Parcelable.Creator<ClusterMarker> CREATOR = new Parcelable.Creator<ClusterMarker>() {
        @Override
        public ClusterMarker createFromParcel(Parcel source) {
            return new ClusterMarker(source);
        }

        @Override
        public ClusterMarker[] newArray(int size) {
            return new ClusterMarker[size];
        }
    };
}
