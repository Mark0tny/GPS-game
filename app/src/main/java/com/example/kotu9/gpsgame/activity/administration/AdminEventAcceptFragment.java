package com.example.kotu9.gpsgame.activity.administration;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kotu9.gpsgame.R;

public class AdminEventAcceptFragment extends Fragment {

    public AdminEventAcceptFragment() {
        // Required empty public constructor
    }

    public static AdminEventAcceptFragment newInstance() {
        AdminEventAcceptFragment fragment = new AdminEventAcceptFragment();
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

        View view = inflater.inflate(R.layout.fragment_admin_event_accept, container, false);
        return view;
    }

}
