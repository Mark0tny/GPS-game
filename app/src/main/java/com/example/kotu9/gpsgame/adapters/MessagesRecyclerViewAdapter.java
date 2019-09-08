package com.example.kotu9.gpsgame.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.model.Message;

import java.text.SimpleDateFormat;
import java.util.List;

public class MessagesRecyclerViewAdapter extends RecyclerView.Adapter<MessagesRecyclerViewAdapter.ViewHolder> {

    List<Message> messageList;
    private LayoutInflater mInflater;

    public MessagesRecyclerViewAdapter(Context context, List<Message> data) {
        this.mInflater = LayoutInflater.from(context);
        this.messageList = data;
    }

    @NonNull
    @Override
    public MessagesRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = mInflater.inflate(R.layout.message_rec_view, viewGroup, false);
        return new MessagesRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesRecyclerViewAdapter.ViewHolder viewHolder, int position) {

        SimpleDateFormat dt = new SimpleDateFormat("dd-mm-yyyy hh:mm:ss");
        viewHolder.mUsername.setText(messageList.get(position).username);
        viewHolder.mDate.setText(dt.format(messageList.get(position).messageDate));
        viewHolder.mBody.setText(messageList.get(position).body);
        viewHolder.mEventName.setText(messageList.get(position).eventName);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView mUsername, mDate, mBody , mEventName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mUsername = itemView.findViewById(R.id.mUsername);
            mDate = itemView.findViewById(R.id.mDate);
            mBody = itemView.findViewById(R.id.mBody);
            mEventName = itemView.findViewById(R.id.mEventname);
        }
    }
}
