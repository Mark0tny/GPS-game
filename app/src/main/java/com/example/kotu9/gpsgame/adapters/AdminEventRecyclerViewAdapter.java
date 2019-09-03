package com.example.kotu9.gpsgame.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.model.ClusterMarker;

import java.text.SimpleDateFormat;
import java.util.List;

public class AdminEventRecyclerViewAdapter extends RecyclerView.Adapter<AdminEventRecyclerViewAdapter.ViewHolder> {

    List<ClusterMarker> markerList;
    private LayoutInflater mInflater;
    private OnEventClickListener onEventClickListener;


    public AdminEventRecyclerViewAdapter(Context context, List<ClusterMarker> data, OnEventClickListener onEventClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.markerList = data;
        this.onEventClickListener = onEventClickListener;
    }

    @NonNull
    @Override
    public AdminEventRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = mInflater.inflate(R.layout.admin_event_list_rec_view, viewGroup, false);
        return new AdminEventRecyclerViewAdapter.ViewHolder(view, onEventClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminEventRecyclerViewAdapter.ViewHolder viewHolder, int position) {

        SimpleDateFormat dt = new SimpleDateFormat("dd-mm-yyyy hh:mm:ss");

    }

    @Override
    public int getItemCount() {
        return markerList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView cUsername, cDate, eBody;
        OnEventClickListener mEventClickListener;

        public ViewHolder(@NonNull View itemView, OnEventClickListener onEventClickListener) {
            super(itemView);


            itemView.setOnClickListener(this);
            this.mEventClickListener = onEventClickListener;

        }

        @Override
        public void onClick(View v) {
            onEventClickListener.onMarkerListClick(getAdapterPosition());
        }
    }

    public interface OnEventClickListener {
        void onMarkerListClick(int position);
    }

}
