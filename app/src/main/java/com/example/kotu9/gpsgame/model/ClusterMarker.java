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


    protected ClusterMarker(Parcel in) {
        title = in.readString();
        snippet = in.readString();
        iconPicture = in.readInt();
        event = in.readParcelable(Event.class.getClassLoader());
        owner = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<ClusterMarker> CREATOR = new Creator<ClusterMarker>() {
        @Override
        public ClusterMarker createFromParcel(Parcel in) {
            return new ClusterMarker(in);
        }

        @Override
        public ClusterMarker[] newArray(int size) {
            return new ClusterMarker[size];
        }
    };

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.snippet);
        dest.writeInt(iconPicture);
        dest.writeParcelable(event, flags);
        dest.writeParcelable(owner, flags);
    }
}
