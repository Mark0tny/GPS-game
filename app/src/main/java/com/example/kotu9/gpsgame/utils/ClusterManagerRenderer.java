package com.example.kotu9.gpsgame.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.model.ClusterMarker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

public class ClusterManagerRenderer extends DefaultClusterRenderer<ClusterMarker> implements ClusterManager.OnClusterClickListener<ClusterMarker> {

    private final IconGenerator iconGenerator;
    private ImageView imageView;
    private int markerWidth;
    private int markerHeight;
    private GoogleMap mMap;

    public ClusterManagerRenderer(Context context, GoogleMap map, ClusterManager<ClusterMarker> clusterManager) {
        super(context, map, clusterManager);

        this.mMap = map;
        iconGenerator = new IconGenerator(context.getApplicationContext());
        imageView = new ImageView(context.getApplicationContext());
        markerWidth = (int) context.getResources().getDimension(R.dimen.marker_icon_size);
        markerHeight = (int) context.getResources().getDimension(R.dimen.marker_icon_size);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(markerWidth, markerHeight));
        int padding = (int) context.getResources().getDimension(R.dimen.marker_padding);
        imageView.setPadding(padding, padding, padding, padding);
        iconGenerator.setContentView(imageView);

        clusterManager.setOnClusterClickListener(this);
        mMap.setInfoWindowAdapter(clusterManager.getMarkerManager());
        clusterManager.getMarkerCollection().setOnInfoWindowAdapter(new CustomInfoWindowAdapter(context));
        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<ClusterMarker> cluster) {
        return cluster.getSize() > 5;
    }

    @Override
    protected void onBeforeClusterItemRendered(ClusterMarker item, MarkerOptions markerOptions) {
        imageView.setImageResource(item.getIconPicture());
        Bitmap icon = iconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getTitle());
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<ClusterMarker> cluster, MarkerOptions markerOptions) {
        super.onBeforeClusterRendered(cluster, markerOptions);
    }

    @Override
    protected void onClusterRendered(Cluster<ClusterMarker> cluster, Marker marker) {
        super.onClusterRendered(cluster, marker);
    }


    @Override
    protected void onClusterItemRendered(ClusterMarker clusterItem, Marker marker) {
        marker.setTag(clusterItem);
    }


    @Override
    public boolean onClusterClick(Cluster<ClusterMarker> cluster) {
        if (cluster == null) return false;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (ClusterMarker clusterMarker : cluster.getItems())
            builder.include(clusterMarker.getPosition());
        LatLngBounds bounds = builder.build();
        try {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 15));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View mWindow;
        private Context mContext;
        ClusterMarker clusterMarkerInfo;

        public CustomInfoWindowAdapter(Context context) {
            mContext = context;
            mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
        }

        private void rendowWindowText(Marker marker, View view) {

            TextView eName = view.findViewById(R.id.textEventNameW);
            TextView eType = view.findViewById(R.id.textEventTypeW);
            TextView eDifficulty = view.findViewById(R.id.textEventDifficultyW);
            TextView eDistance = view.findViewById(R.id.textDistanceValueW);
            RatingBar ratingEvent = view.findViewById(R.id.ratingEventW);
            TextView eRatingValue = view.findViewById(R.id.textRatingValueW);


            clusterMarkerInfo = (ClusterMarker) marker.getTag();

            eName.setText(clusterMarkerInfo.getEvent().name);
            eType.setText(clusterMarkerInfo.getEvent().eventType.eventType.name());
            eDifficulty.setText(clusterMarkerInfo.getEvent().difficulty.name());
            eDifficulty.setTextColor(clusterMarkerInfo.getEvent().difficulty.getColor());
            eDistance.setText(String.format("%.2f", clusterMarkerInfo.getEvent().distance));
            eRatingValue.setText(String.valueOf(clusterMarkerInfo.getEvent().rating));
            ratingEvent.setRating(clusterMarkerInfo.getEvent().rating);
        }


        @Override
        public View getInfoWindow(Marker marker) {
            rendowWindowText(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            rendowWindowText(marker, mWindow);
            return mWindow;
        }
    }
}
