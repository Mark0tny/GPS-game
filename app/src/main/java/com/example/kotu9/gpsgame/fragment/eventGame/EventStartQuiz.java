package com.example.kotu9.gpsgame.fragment.eventGame;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kotu9.gpsgame.R;

public class EventStartQuiz extends Fragment {

    public EventStartQuiz() {
        // Required empty public constructor
    }


    public static EventStartQuiz newInstance() {
        EventStartQuiz fragment = new EventStartQuiz();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getActivity().setTitle(R.string.fr_start_quiz);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_event_start_quiz, container, false);
        return view;
    }

}
