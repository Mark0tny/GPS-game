package com.example.kotu9.gpsgame.fragment.eventGame;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.model.ClusterMarker;
import com.example.kotu9.gpsgame.model.Event;
import com.example.kotu9.gpsgame.utils.EventTypes;

public class StartGameFragment extends Fragment implements View.OnClickListener {

    private TextView username;
    private Button startEvent;


    public NavController navController;
    private Event event;
    private ClusterMarker clusterMarker;
    private Bundle clusterMarkerBundle;

    public StartGameFragment() {
    }

    public static StartGameFragment newInstance() {
        StartGameFragment fragment = new StartGameFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = new Event();
            clusterMarker = new ClusterMarker();
            clusterMarkerBundle = new Bundle();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_start_game, container, false);

        setupViews(view);
        setupNavigation();
        return view;
    }


    //TODO bundle z ClusterMarkiem + jakies widoki
    private void setupViews(View view) {
        username = view.findViewById(R.id.startWelcomeValue);
        startEvent = view.findViewById(R.id.startEvent);
        startEvent.setOnClickListener(this);
    }

    private void setupNavigation() {
        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_start_game);
    }

    private int navigateRightEventHandler(Bundle bundle) {
        switch (checkEventType(event)) {
            case Location:
                navController.navigate(R.id.eventStartLocation, bundle);
            case Quiz:
                navController.navigate(R.id.eventStartQuiz, bundle);
            case QRcode:
                navController.navigate(R.id.eventStartQRcode, bundle);
            case PhotoCompare:
                navController.navigate(R.id.eventStartPhotoCompare, bundle);
        }
        return 1;
    }


    private EventTypes checkEventType(Event event) {
        return event.eventType.eventType;
    }

    @Override
    public void onClick(View v) {
        navigateRightEventHandler(clusterMarkerBundle);
    }


}
