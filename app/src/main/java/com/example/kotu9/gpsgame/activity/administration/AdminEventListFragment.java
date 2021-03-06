package com.example.kotu9.gpsgame.activity.administration;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.adapters.AdminEventRecyclerViewAdapter;
import com.example.kotu9.gpsgame.model.ClusterMarker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminEventListFragment extends Fragment implements AdminEventRecyclerViewAdapter.OnEventClickListener {

    private static final String TAG = AdminEventListFragment.class.getSimpleName();
    private RecyclerView mRecyclerViewInEvents;
    private AdminEventRecyclerViewAdapter mAdapterEvents;
    private List<ClusterMarker> events = new ArrayList<>();

    FirebaseAuth mAuth;
    private FirebaseFirestore mDb;

    public AdminEventListFragment() {

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

        mRecyclerViewInEvents = view.findViewById(R.id.recyclerInactiveEvents);
        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();
        getMapMarkersListDB();
        return view;
    }

    private void initListRecyclerView() {
        if (getContext() != null) {

            mAdapterEvents = new AdminEventRecyclerViewAdapter(getContext(), events, this);
            mRecyclerViewInEvents.setAdapter(mAdapterEvents);
            mRecyclerViewInEvents.setLayoutManager(new LinearLayoutManager(getActivity()));
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

                            for (int i = 0; i < events.size(); i++) {
                                if (!events.get(i).getEvent().active)
                                    events.remove(events.get(i));
                                events.get(i).setPosition(new LatLng(events.get(i).getPosition2().getLatitude(), events.get(i).getPosition2().getLongitude()));
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

    @Override
    public void onMarkerListClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(String.valueOf(R.string.markerBundle), events.get(position));

        AdminEventAcceptFragment adminEventAcceptFragment = new AdminEventAcceptFragment();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, adminEventAcceptFragment);
        fragmentTransaction.commit();
    }
}
