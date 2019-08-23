package com.example.kotu9.gpsgame.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.model.Comment;

import java.util.List;

public class CommentsRecyclerViewAdapter extends RecyclerView.Adapter<CommentsRecyclerViewAdapter.ViewHolder> {

    List<Comment> commentList;
    private LayoutInflater mInflater;

    public CommentsRecyclerViewAdapter(Context context, List<Comment> data) {
        this.mInflater = LayoutInflater.from(context);
        this.commentList = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = mInflater.inflate(R.layout.event_rec_view, viewGroup, false);
        return new CommentsRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

//        viewHolder.eName.setText();
//        viewHolder.eType.setText();
//        viewHolder.eDifficulty.setText();


    }

    @Override
    public int getItemCount() {
        return commentList.size();
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