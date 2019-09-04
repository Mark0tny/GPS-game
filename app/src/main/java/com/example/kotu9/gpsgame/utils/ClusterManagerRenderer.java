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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

public class ClusterManagerRenderer extends DefaultClusterRenderer<ClusterMarker> {

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

        mMap.setInfoWindowAdapter(clusterManager.getMarkerManager());
        clusterManager.getMarkerCollection().setOnInfoWindowAdapter(new CustomInfoWindowAdapter(context));
        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<ClusterMarker> cluster) {
        return cluster.getSize() > 3;
    }

    @Override
    protected void onBeforeClusterItemRendered(ClusterMarker item, MarkerOptions markerOptions) {
        imageView.setImageResource(item.getIconPicture());
        Bitmap icon = iconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getTitle());
    }


    @Override
    protected void onBeforeClusterRendered(Cluster<ClusterMarker> cluster, MarkerOptions markerOptions) {
        if (cluster.getSize() > 4) {
            Bitmap icon = iconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        } else if (cluster.getSize() > 8) {
            Bitmap icon = iconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        } else {
            Bitmap icon = iconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

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


    public static class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View mWindow;
        private Context mContext;
        ClusterMarker clusterMarkerInfo;

        public CustomInfoWindowAdapter(Context context) {
            this.mContext = context;
            this.mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
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
            eRatingValue.setText(String.valueOf(clusterMarkerInfo.getEvent().rating.globalRating));
            ratingEvent.setRating(clusterMarkerInfo.getEvent().rating.globalRating);
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
