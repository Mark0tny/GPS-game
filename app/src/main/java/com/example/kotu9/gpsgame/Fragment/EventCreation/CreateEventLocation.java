package com.example.kotu9.gpsgame.Fragment.EventCreation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.kotu9.gpsgame.R;


public class CreateEventLocation extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button buttonNext;
    private Button buttonPrev;


    public CreateEventLocation() {
        // Required empty public constructor
    }

    public static CreateEventLocation newInstance(String param1, String param2) {
        CreateEventLocation fragment = new CreateEventLocation();
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
        View view = inflater.inflate(R.layout.fragment_create_event_location, container, false);


        buttonNext = view.findViewById(R.id.buttonLocationNext);
        buttonNext.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_createEventLocation_to_createEventMarker));

        buttonPrev = view.findViewById(R.id.buttonLocationPrev);
        buttonPrev.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_createEventLocation_to_createEventDetails));

        return view;
    }


}
