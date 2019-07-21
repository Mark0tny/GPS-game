package com.example.kotu9.gpsgame.Fragment.EventCreation;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.kotu9.gpsgame.Activity.CreateEventActivity;
import com.example.kotu9.gpsgame.R;



public class CreateEventInfo extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;
    public Spinner spinnerEvent;
    SpinnerListener spinnerListener;
    public CreateEventInfo() {
        // Required empty public constructor
    }

    public static CreateEventInfo newInstance(String param1, String param2) {
        CreateEventInfo fragment = new CreateEventInfo();
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
        View view = inflater.inflate(R.layout.fragment_create_event_info, container, false);

        spinnerEvent = view.findViewById(R.id.spinnerEvent);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.support_simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.Types));
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerEvent.setAdapter(adapter);

        spinnerEvent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerListener.onSpinnerEventSelected(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof CreateEventActivity)
        spinnerListener = ((CreateEventActivity)context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        spinnerListener = null;
    }

    public interface SpinnerListener {
         void onSpinnerEventSelected(Integer position);

    }
}





