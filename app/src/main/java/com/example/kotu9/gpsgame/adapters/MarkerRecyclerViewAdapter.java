package com.example.kotu9.gpsgame.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.model.Event;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

public class MarkerRecyclerViewAdapter extends RecyclerView.Adapter<MarkerRecyclerViewAdapter.ViewHolder> {

    List<Event> eventList = new ArrayList<>();
    List<Marker> markers = new ArrayList<>();
    private LayoutInflater mInflater;
    private QuizRecyclerViewAdapter.ItemClickListener mClickListener;


    public MarkerRecyclerViewAdapter(Context context, List<Event> data) {
        this.mInflater = LayoutInflater.from(context);
        this.eventList = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewTypei) {

        //TODO inicjalizacja list pomocniczych

        View view = mInflater.inflate(R.layout.event_rec_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {


        //TODO DAWANIE DANYCH DO LISTY
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //TODO elementy na vidoku

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //TODO findbyid
        }

        @Override
        public void onClick(View v) {

        }
    }

    void setClickListener(QuizRecyclerViewAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }


    //TODO przenosi do activity detailts i wysla dane pobrane event
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }




}


