package com.example.kotu9.gpsgame.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.activity.UserLocationActivity;
import com.example.kotu9.gpsgame.adapters.CommentsRecyclerViewAdapter;
import com.example.kotu9.gpsgame.adapters.RankingRecyclerViewAdapter;
import com.example.kotu9.gpsgame.fragment.eventGame.EventStartSummary;
import com.example.kotu9.gpsgame.model.ClusterMarker;
import com.example.kotu9.gpsgame.model.Comment;
import com.example.kotu9.gpsgame.model.Event;
import com.example.kotu9.gpsgame.model.Hint;
import com.example.kotu9.gpsgame.model.User;
import com.example.kotu9.gpsgame.utils.EventTypes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import lombok.NonNull;

import static java.lang.String.valueOf;

public class EventDetails extends Fragment implements View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private static final String TAG = EventStartSummary.class.getSimpleName();

    TextView eName, eType, eDifficulty, eDistance, eDescription, eRatingValue, eLatValue,
            eLngValue, eOwnerName, hintListExp, commentsListExp, rankingListExp;
    RatingBar ratingEvent;

    ClusterMarker clusterMarker;

    private ArrayAdapter<String> mAdapterHints;
    private ListView listView;

    private RecyclerView mRecyclerViewComments;
    private CommentsRecyclerViewAdapter mAdapterComments;
    private RecyclerView mRecyclerViewRanking;
    private RankingRecyclerViewAdapter mAdapterRanking;

    private Event event;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;
    private User user;

    private ArrayList<Comment> comments;
    private Hint hintList;


    private GoogleMap mMap;
    private MapView mapView;
    private Marker myEventLoc;

    public EventDetails() {

    }

    public static EventDetails newInstance() {
        EventDetails fragment = new EventDetails();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getActivity().setTitle(R.string.fr_event_details);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            clusterMarker = new ClusterMarker();
            event = new Event();
            user = new User();
            comments = new ArrayList<>();
            hintList = new Hint();
            hintList.hints = new ArrayList<>();
            clusterMarker = (ClusterMarker) getArguments().get(valueOf(R.string.markerBundle));
            if (clusterMarker != null) {
                Log.i("clusterMarkerBundle", clusterMarker.toString());
                event = clusterMarker.getEvent();
                hintList.hints = clusterMarker.getEvent().getHintList().getHints();
            }
        }
    }

    private void loadUserInformation() {
        if (user != null) {
            DocumentReference docRef = mDb.collection(getString(R.string.collection_users)).document(mAuth.getCurrentUser().getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            user = task.getResult().toObject(User.class);
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);
        setupViews(view, savedInstanceState);
        mDb = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        loadUserInformation();
        getEvent();
        setEventInfoTextView(clusterMarker);
        initCommentsListRecyclerView();
        initRankingListRecyclerView();
        return view;
    }

    private void getEvent() {
        DocumentReference newUserRef = mDb
                .collection(getString(R.string.collection_events))
                .document(clusterMarker.getEvent().getEventType().eventType.name())
                .collection(clusterMarker.getEvent().getId())
                .document(clusterMarker.getEvent().getId());
        newUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        event = task.getResult().toObject(Event.class);
                        comments = (ArrayList<Comment>) event.comments;

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    private void setEventInfoTextView(ClusterMarker clusterMarker) {
        eName.setText(clusterMarker.getEvent().name);
        eType.setText(clusterMarker.getEvent().eventType.eventType.name());
        eDifficulty.setText(clusterMarker.getEvent().difficulty.name());
        eDistance.setText(String.format("%.2f", clusterMarker.getEvent().distance));
        eDescription.setText(clusterMarker.getEvent().description);
        eLatValue.setText(String.format("%.2f", clusterMarker.getPosition().latitude));
        eLngValue.setText(String.format("%.2f", clusterMarker.getPosition().longitude));
        eOwnerName.setText(clusterMarker.getOwner().username);

        ratingEvent.setRating(event.rating.globalRating);
        eRatingValue.setText(valueOf(event.rating.globalRating));

    }

    private void setupViews(View view, Bundle savedInstanceState) {
        eName = view.findViewById(R.id.textEventNameD);
        eType = view.findViewById(R.id.textEventTypeD);
        eDifficulty = view.findViewById(R.id.textEventDifficultyD);
        eDistance = view.findViewById(R.id.textDistanceValueD);
        ratingEvent = view.findViewById(R.id.ratingEventD);
        eRatingValue = view.findViewById(R.id.textRatingValueD);
        eLatValue = view.findViewById(R.id.textLatD);
        eLngValue = view.findViewById(R.id.textLngD);
        eOwnerName = view.findViewById(R.id.textOwnerValueD);
        eDescription = view.findViewById(R.id.textDescriptionValueD);
        listView = view.findViewById(R.id.hintListD);
        mRecyclerViewComments = view.findViewById(R.id.recyclerComments);
        mRecyclerViewRanking = view.findViewById(R.id.recyclerRanking);

        hintListExp = view.findViewById(R.id.hintListName);
        hintListExp.setOnClickListener(this);
        commentsListExp = view.findViewById(R.id.commentListName);
        commentsListExp.setOnClickListener(this);
        rankingListExp = view.findViewById(R.id.rankingListName);
        rankingListExp.setOnClickListener(this);

        mapView = view.findViewById(R.id.detailMap);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        if (hintList.hints != null) {
            if (hintList.hints.isEmpty()) {
                hintListExp.setEnabled(false);
            } else hintListExp.setEnabled(true);

            mAdapterHints = new ArrayAdapter<String>(getContext(),
                    android.R.layout.simple_list_item_1, hintList.hints);
            listView.setAdapter(mAdapterHints);
        }

    }

    private void initCommentsListRecyclerView() {
        if (getContext() != null) {
            if (comments != null) {
                commentsListExp.setEnabled(false);
            } else commentsListExp.setEnabled(true);

            mAdapterComments = new CommentsRecyclerViewAdapter(getContext(), comments);
            mRecyclerViewComments.setAdapter(mAdapterComments);
            mRecyclerViewComments.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }

    private void initRankingListRecyclerView() {
        if (getContext() != null && event != null) {
            if (event.getRanking() != null) {
                rankingListExp.setEnabled(false);
            } else rankingListExp.setEnabled(true);

            mAdapterRanking = new RankingRecyclerViewAdapter(getContext(), event);
            mRecyclerViewRanking.setAdapter(mAdapterRanking);
            mRecyclerViewRanking.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hintListName:
                listView.setVisibility(listView.isShown() ? View.GONE : View.VISIBLE);
                break;
            case R.id.commentListName:
                mRecyclerViewComments.setVisibility(mRecyclerViewComments.isShown() ? View.GONE : View.VISIBLE);
                break;
            case R.id.rankingListName:
                mRecyclerViewRanking.setVisibility(mRecyclerViewRanking.isShown() ? View.GONE : View.VISIBLE);
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.setOnMapClickListener(this);
        addLocationMarker(clusterMarker.getPosition());
    }

    private EventTypes checkEventType(Event event) {
        return event.eventType.eventType;
    }

    private int setMarkerIcon(Marker marker) {
        switch (checkEventType(clusterMarker.getEvent())) {
            case Location:
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon36_location));
                return R.drawable.icon36_location;
            case Quiz:
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icons36_question));
                return R.drawable.icons36_question;
            case QRcode:
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon36_qr_code));
                return R.drawable.icon36_qr_code;
            case PhotoCompare:
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon36_camera));
                return R.drawable.icon36_camera;
        }
        return 1;
    }

    private void addLocationMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng);
        if (mMap != null) {
            if (myEventLoc != null) {
                myEventLoc.remove();
            }
            myEventLoc = mMap.addMarker(markerOptions);
            setMarkerIcon(myEventLoc);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(myEventLoc.getPosition().latitude, myEventLoc.getPosition().longitude), 15);
            mMap.moveCamera(cameraUpdate);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Intent intent = new Intent(getContext(), UserLocationActivity.class);
        startActivity(intent);
    }


}
