package com.example.kotu9.gpsgame.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.model.Event;
import com.example.kotu9.gpsgame.model.Statistics;
import com.example.kotu9.gpsgame.model.User;

import java.text.SimpleDateFormat;
import java.util.List;

public class RankingRecyclerViewAdapter extends RecyclerView.Adapter<RankingRecyclerViewAdapter.ViewHolder> {

    private Event event;
    private List<Statistics> statistics;
    private List<User> ranking;
    private LayoutInflater mInflater;

    public RankingRecyclerViewAdapter(Context context, Event data) {
        this.mInflater = LayoutInflater.from(context);
        this.event = data;
    }

    @NonNull
    @Override
    public RankingRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = mInflater.inflate(R.layout.ranking_rec_view, viewGroup, false);
        return new RankingRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        SimpleDateFormat dt = new SimpleDateFormat("mm:ss");
        ranking = event.getRanking();

        for (User user : ranking) {
            statistics = user.getCompleteEvents();

            for (Statistics statistic : statistics) {
                if (statistic.eventID.equals(event.id)) {
                    viewHolder.rUsername.setText(user.username);
                    viewHolder.rPoints.setText(statistic.eventID);
                    viewHolder.rTime.setText(dt.format(statistic.getTime()));
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView rUsername, rPoints, rTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rUsername = itemView.findViewById(R.id.rUsername);
            rPoints = itemView.findViewById(R.id.rPoints);
            rTime = itemView.findViewById(R.id.rTime);

        }

    }
}
