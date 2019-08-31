package com.example.kotu9.gpsgame.fragment.eventGame;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kotu9.gpsgame.R;

public class EventStartLocation extends Fragment {

    public EventStartLocation() {
        // Required empty public constructor
    }


    public static EventStartLocation newInstance() {
        EventStartLocation fragment = new EventStartLocation();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getActivity().setTitle(R.string.fr_start_location);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_start_location, container, false);
        return view;
    }

}
