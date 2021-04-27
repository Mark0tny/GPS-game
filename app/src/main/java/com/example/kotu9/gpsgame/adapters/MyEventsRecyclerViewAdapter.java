package com.example.kotu9.gpsgame.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.model.ClusterMarker;

import java.util.List;

public class MyEventsRecyclerViewAdapter extends RecyclerView.Adapter<MyEventsRecyclerViewAdapter.ViewHolder> {


    private List<ClusterMarker> markers;
    private LayoutInflater mInflater;

    public MyEventsRecyclerViewAdapter(Context context, List<ClusterMarker> data) {
        this.mInflater = LayoutInflater.from(context);
        this.markers = data;
    }

    @NonNull
    @Override
    public MyEventsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = mInflater.inflate(R.layout.myevents_rec_view, viewGroup, false);
        return new MyEventsRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyEventsRecyclerViewAdapter.ViewHolder viewHolder, int position) {


        viewHolder.eName.setText(markers.get(position).getEvent().name);
        viewHolder.eType.setText(markers.get(position).getEvent().eventType.eventType.name());
        viewHolder.eDifficulty.setText(markers.get(position).getEvent().getDifficulty().name());
        viewHolder.icon.setImageResource(markers.get(position).getIconPicture());
        viewHolder.eRatingValue.setText(String.valueOf(markers.get(position).getEvent().rating.globalRating));
        viewHolder.ratingEvent.setRating(markers.get(position).getEvent().rating.globalRating);

        if (markers.get(position).getEvent().active) {
            viewHolder.eStatus.setText("active");
        } else {
            viewHolder.eStatus.setText("inactive");
        }

    }

    @Override
    public int getItemCount() {
        return markers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        TextView eName, eType, eDifficulty, eStatus, eRatingValue;
        RatingBar ratingEvent;
        ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eName = itemView.findViewById(R.id.textEventNameE);
            eType = itemView.findViewById(R.id.textEventTypeE);
            eDifficulty = itemView.findViewById(R.id.textEventDifficultyE);
            eStatus = itemView.findViewById(R.id.textStatusValueE);
            ratingEvent = itemView.findViewById(R.id.ratingEventE);
            icon = itemView.findViewById(R.id.markerIconE);
            eRatingValue = itemView.findViewById(R.id.textRatingValueE);
        }
    }
}
