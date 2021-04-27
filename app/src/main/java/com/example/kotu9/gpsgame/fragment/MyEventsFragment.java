package com.example.kotu9.gpsgame.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.adapters.MessagesRecyclerViewAdapter;
import com.example.kotu9.gpsgame.adapters.MyEventsRecyclerViewAdapter;
import com.example.kotu9.gpsgame.model.ClusterMarker;
import com.example.kotu9.gpsgame.model.Message;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyEventsFragment extends Fragment {

    private static final String TAG = MyEventsFragment.class.getSimpleName();
    private RecyclerView mRecyclerViewMyEvents;
    private MyEventsRecyclerViewAdapter mAdapterEvents;
    private List<ClusterMarker> events = new ArrayList<>();

    FirebaseAuth mAuth;
    private FirebaseFirestore mDb;

    public MyEventsFragment() {
    }

    public static MyEventsFragment newInstance() {
        MyEventsFragment fragment = new MyEventsFragment();
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
        View view = inflater.inflate(R.layout.fragment_my_events, container, false);

        mRecyclerViewMyEvents = view.findViewById(R.id.recyclerMyEvents);
        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();
        getMapMarkersListDB();
        return view;
    }

    private void initListRecyclerView() {
        if (getContext() != null) {

            mAdapterEvents = new MyEventsRecyclerViewAdapter(getContext(), events);
            mRecyclerViewMyEvents.setAdapter(mAdapterEvents);
            mRecyclerViewMyEvents.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }

    private void getMapMarkersListDB() {
        mDb.collection(getString(R.string.collection_markers)).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            Log.d(TAG, "onSuccess: LIST EMPTY");
                            return;
                        } else {
                            List<ClusterMarker> clusterMarkerList = documentSnapshots.toObjects(ClusterMarker.class);
                            events.addAll(clusterMarkerList);
                            for (ClusterMarker clusterMarker : events) {
                                clusterMarker.setPosition(new LatLng(clusterMarker.getPosition2().getLatitude(), clusterMarker.getPosition2().getLongitude()));
                            }
                            Log.d(TAG, "onSuccess: " + events);
                            initListRecyclerView();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@androidx.annotation.NonNull Exception e) {
                Toast.makeText(getContext(), "Error getting data!!!", Toast.LENGTH_LONG).show();
                Log.i(TAG, e.getMessage());
            }
        });
    }

}
