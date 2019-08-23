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

import java.text.SimpleDateFormat;
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
        View view = mInflater.inflate(R.layout.comment_rec_view, viewGroup, false);
        return new CommentsRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        SimpleDateFormat dt = new SimpleDateFormat("dd-mm-yyyy hh:mm:ss");
        viewHolder.cUsername.setText(commentList.get(position).username);
        viewHolder.cDate.setText(dt.format(commentList.get(position).commentDate));
        viewHolder.eBody.setText(commentList.get(position).body);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView cUsername, cDate, eBody;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cUsername = itemView.findViewById(R.id.comUsername);
            cDate = itemView.findViewById(R.id.comDate);
            eBody = itemView.findViewById(R.id.comBody);
        }
    }
}