package com.example.kotu9.gpsgame.Fragment.EventCreation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.Navigation;

import com.example.kotu9.gpsgame.R;


public class CreateEventDetails extends Fragment implements NavHost {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Button buttonNext;
    private Button buttonPrev;
    private String mParam1;
    private String mParam2;


    public CreateEventDetails() {
        // Required empty public constructor
    }

    public static CreateEventDetails newInstance(String param1, String param2) {
        CreateEventDetails fragment = new CreateEventDetails();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event_details, container, false);


        buttonNext = view.findViewById(R.id.buttonDetailsNext);
        buttonNext.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_createEventDetails_to_createEventLocation));

        buttonPrev = view.findViewById(R.id.buttonDetailsPrev);
        buttonPrev.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_createEventDetails_to_createEventInfo));

        return view;
    }


    @NonNull
    @Override
    public NavController getNavController() {
        return null;
    }
}
