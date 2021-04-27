package com.example.kotu9.gpsgame.activity.administration;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.activity.UserLocationActivity;
import com.example.kotu9.gpsgame.adapters.CommentsRecyclerViewAdapter;
import com.example.kotu9.gpsgame.adapters.RankingRecyclerViewAdapter;
import com.example.kotu9.gpsgame.model.ClusterMarker;
import com.example.kotu9.gpsgame.model.Comment;
import com.example.kotu9.gpsgame.model.Event;
import com.example.kotu9.gpsgame.model.Hint;
import com.example.kotu9.gpsgame.model.Message;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;

import static java.lang.String.valueOf;

public class AdminEventAcceptFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMapClickListener {

    TextView eName, eType, eDifficulty,eDescription, eLatValue,
            eLngValue, eOwnerName, hintListExp;
    Button accept,reject;
    EditText message;

    ClusterMarker clusterMarker;

    private ArrayAdapter<String> mAdapterHints;
    private ListView listView;

    private Event event;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;
    private User user;

    private Hint hintList;

    private GoogleMap mMap;
    private MapView mapView;
    private Marker myEventLoc;

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
            clusterMarker = new ClusterMarker();
            event = new Event();
            user = new User();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_event_accept, container, false);
        setupViews(view, savedInstanceState);
        mDb = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        setEventInfoTextView(clusterMarker);
        return view;
    }

    private void setEventInfoTextView(ClusterMarker clusterMarker) {
        eName.setText(clusterMarker.getEvent().name);
        eType.setText(clusterMarker.getEvent().eventType.eventType.name());
        eDifficulty.setText(clusterMarker.getEvent().difficulty.name());
        eDescription.setText(clusterMarker.getEvent().description);
        eLatValue.setText(String.format("%.2f", clusterMarker.getPosition().latitude));
        eLngValue.setText(String.format("%.2f", clusterMarker.getPosition().longitude));
        eOwnerName.setText(clusterMarker.getOwner().username);


    }

    private void setupViews(View view, Bundle savedInstanceState) {
        eName = view.findViewById(R.id.textEventNameD);
        eType = view.findViewById(R.id.textEventTypeD);
        eDifficulty = view.findViewById(R.id.textEventDifficultyD);
        message = view.findViewById(R.id.addMessageValue);
        eLatValue = view.findViewById(R.id.textLatD);
        eLngValue = view.findViewById(R.id.textLngD);
        eOwnerName = view.findViewById(R.id.textOwnerValueD);
        eDescription = view.findViewById(R.id.textDescriptionValueD);
        listView = view.findViewById(R.id.hintListD);
        accept = view.findViewById(R.id.accept);
        reject = view.findViewById(R.id.reject);
        hintListExp = view.findViewById(R.id.hintListName);
        hintListExp.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hintListName:
                listView.setVisibility(listView.isShown() ? View.GONE : View.VISIBLE);
                break;
            case R.id.accept:
                acceptEvent();
                break;
            case R.id.reject:
                rejectEvent();
                break;
        }
    }

    private void rejectEvent() {
        if(message.getText().toString().isEmpty()){
            Toast.makeText(getContext(),"Add feedback",Toast.LENGTH_LONG).show();
        }else{
            user.messages.add(new Message(message.getText().toString().trim(), Calendar.getInstance().getTime(),"admin",clusterMarker.getEvent().name));
            DocumentReference newUserRef = mDb
                    .collection(getString(R.string.collection_users)).document(mAuth.getCurrentUser().getUid());
            newUserRef.update("messages", FieldValue.arrayUnion(user.messages)).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                    Toast.makeText(getContext(),"SCORE",Toast.LENGTH_LONG).show();
                    Log.i("score: ", "SUCCESS");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@androidx.annotation.NonNull Exception e) {
                    Log.i("update score", e.getMessage());
                }
            });
        }

    }

    private void acceptEvent() {
            DocumentReference newUserRef = mDb
                    .collection(getString(R.string.collection_events))
                    .document(clusterMarker.getEvent().getEventType().eventType.name())
                    .collection(clusterMarker.getEvent().getId())
                    .document(clusterMarker.getEvent().getId());
            newUserRef.update("active", true).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                    Toast.makeText(getContext(),"RATING()",Toast.LENGTH_LONG).show();
                    Log.i("rating: ", "SUCCESS");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@androidx.annotation.NonNull Exception e) {
                    Log.i("update status", e.getMessage());
                }
            });

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

    private EventTypes checkEventType(Event event) {
        return event.eventType.eventType;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Intent intent = new Intent(getContext(), UserLocationActivity.class);
        startActivity(intent);
    }
}
