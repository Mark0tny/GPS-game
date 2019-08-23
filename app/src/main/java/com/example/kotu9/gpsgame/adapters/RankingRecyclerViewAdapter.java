package com.example.kotu9.gpsgame.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.model.User;

import java.util.List;

public class RankingRecyclerViewAdapter extends RecyclerView.Adapter<RankingRecyclerViewAdapter.ViewHolder> {

    List<User> userList;
    private LayoutInflater mInflater;

    public RankingRecyclerViewAdapter(Context context, List<User> data) {
        this.mInflater = LayoutInflater.from(context);
        this.userList = data;
    }

    @NonNull
    @Override
    public RankingRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = mInflater.inflate(R.layout.ranking_rec_view, viewGroup, false);
        return new RankingRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.eName.setText("");

    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView eName, eType, eDifficulty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            eName = itemView.findViewById(R.id.textEventName);
            eType = itemView.findViewById(R.id.textEventType);
            eDifficulty = itemView.findViewById(R.id.textEventDifficulty);

        }

    }
}
