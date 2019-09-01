package com.example.kotu9.gpsgame.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.model.ClusterMarker;

import java.util.List;

public class MarkerRecyclerViewAdapter extends RecyclerView.Adapter<MarkerRecyclerViewAdapter.ViewHolder> {

    private List<ClusterMarker> markers;

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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        viewHolder.eName.setText(markers.get(position).getEvent().name);
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

        TextView eName, eType, eDifficulty, eDistance, eRatingValue;
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


