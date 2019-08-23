package com.example.kotu9.gpsgame.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.model.ClusterMarker;

import java.util.ArrayList;
import java.util.List;

public class MarkerRecyclerViewAdapter extends RecyclerView.Adapter<MarkerRecyclerViewAdapter.ViewHolder> {

    private List<ClusterMarker> markers = new ArrayList<>();
    private LayoutInflater mInflater;
    private OnMarkerClickListener mOnMarkerClickListener;

    public MarkerRecyclerViewAdapter(Context context, List<ClusterMarker> data, OnMarkerClickListener onMarkerClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.markers = data;
        this.mOnMarkerClickListener = onMarkerClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.event_rec_view, parent, false);
        return new ViewHolder(view, mOnMarkerClickListener);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        viewHolder.eName.setText(markers.get(position).getEvent().name);
        viewHolder.eDescriptionValue.setText(markers.get(position).getEvent().description);
        viewHolder.eType.setText(markers.get(position).getEvent().eventType.eventType.name());
        viewHolder.eDifficulty.setText(markers.get(position).getEvent().getDifficulty().name());
        viewHolder.eDistance.setText(String.format("%.2f", markers.get(position).getEvent().distance));
        viewHolder.icon.setImageResource(markers.get(position).getIconPicture());
        viewHolder.eRatingValue.setText(String.valueOf(markers.get(position).getEvent().rating));
        viewHolder.ratingEvent.setRating(markers.get(position).getEvent().rating);

    }

    @Override
    public int getItemCount() {
        return markers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView eName, eType, eDifficulty, eDistance, eRatingValue, eDescriptionValue;
        RatingBar ratingEvent;
        ImageView icon;
        OnMarkerClickListener mOnMarkerClickListener;

        public ViewHolder(@NonNull View itemView, OnMarkerClickListener onMarkerClickListener) {
            super(itemView);

            eName = itemView.findViewById(R.id.textEventName);
            eType = itemView.findViewById(R.id.textEventType);
            eDifficulty = itemView.findViewById(R.id.textEventDifficulty);
            eDistance = itemView.findViewById(R.id.textDistanceValue);
            ratingEvent = itemView.findViewById(R.id.ratingEvent);
            eDescriptionValue = itemView.findViewById(R.id.textDescriptionValueD);
            icon = itemView.findViewById(R.id.markerIcon);
            eRatingValue = itemView.findViewById(R.id.textRatingValue);
            itemView.setOnClickListener(this);
            this.mOnMarkerClickListener = onMarkerClickListener;

        }

        @Override
        public void onClick(View v) {
            mOnMarkerClickListener.onMarkerListClick(getAdapterPosition());
        }
    }

    public interface OnMarkerClickListener {
        void onMarkerListClick(int position);
    }


}


