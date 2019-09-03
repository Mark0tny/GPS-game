package com.example.kotu9.gpsgame.activity.administration;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kotu9.gpsgame.R;

public class AdminEventListFragment extends Fragment {

    public AdminEventListFragment() {
        // Required empty public constructor
    }


    public static AdminEventListFragment newInstance() {
        AdminEventListFragment fragment = new AdminEventListFragment();
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

        View view = inflater.inflate(R.layout.fragment_admin_event_list, container, false);
        return view;
    }

}
