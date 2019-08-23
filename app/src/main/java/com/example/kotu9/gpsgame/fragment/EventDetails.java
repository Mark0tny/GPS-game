package com.example.kotu9.gpsgame.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.kotu9.gpsgame.R;

public class EventDetails extends Fragment implements View.OnClickListener {


    TextView eName, eType, eDifficulty, eDistance, eRatingValue, eLatValue, eLngValue, eOwnerName;
    RatingBar ratingEvent;

    private ArrayAdapter<String> madapterhints;

    private RecyclerView recyclerViewComments;
    private RecyclerView.Adapter mAdapterComments;
    private RecyclerView.LayoutManager layoutManagerComments;

    private RecyclerView recyclerViewRanking;
    private RecyclerView.Adapter mAdapterRanking;
    private RecyclerView.LayoutManager layoutManagerRanking;

    public EventDetails() {

    }

    public static EventDetails newInstance() {
        EventDetails fragment = new EventDetails();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_event_details, container, false);

        setupViews(view);
        initCommentsListRecyclerView();
        initRankingListRecyclerView();
        return view;
    }


    private void setupViews(View view) {
        eName = view.findViewById(R.id.textEventNameD);
        eType = view.findViewById(R.id.textEventTypeD);
        eDifficulty = view.findViewById(R.id.textEventDifficultyD);
        eDistance = view.findViewById(R.id.textDistanceValueD);
        ratingEvent = view.findViewById(R.id.ratingEventD);
        eLatValue = view.findViewById(R.id.textLatD);
        eLngValue = view.findViewById(R.id.textLngD);
        eOwnerName = view.findViewById(R.id.textOwnerD);
    }

    private void initCommentsListRecyclerView() {
        if (getContext() != null) {

        }
    }

    private void initRankingListRecyclerView() {
        if (getContext() != null) {

        }
    }


    @Override
    public void onClick(View v) {

    }
}
