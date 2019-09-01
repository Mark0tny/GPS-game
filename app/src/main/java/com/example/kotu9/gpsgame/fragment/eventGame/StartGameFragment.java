package com.example.kotu9.gpsgame.fragment.eventGame;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.activity.UserLocationActivity;
import com.example.kotu9.gpsgame.model.ClusterMarker;
import com.example.kotu9.gpsgame.model.Event;
import com.example.kotu9.gpsgame.model.User;
import com.example.kotu9.gpsgame.utils.EventTypes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static java.lang.String.valueOf;

public class StartGameFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "StartGameFragment";

    private TextView username, timerText,eventName;
    private Button startEvent, cancelEvent;

    Chronometer chronometer;
    public NavController navController;
    private Event event;
    private ClusterMarker clusterMarker;
    private Bundle clusterBundleGame;
    private CountDownTimer timer;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;

    public StartGameFragment() {
    }

    public static StartGameFragment newInstance() {
        StartGameFragment fragment = new StartGameFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getActivity().setTitle(R.string.ac_start_game);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = new Event();
            clusterMarker = new ClusterMarker();
            clusterBundleGame = new Bundle();
            Bundle clusterMarkerBundle = getArguments();

            Log.i("FragmentBUNDLEMarker", String.valueOf(clusterMarkerBundle.getBundle(valueOf(R.string.markerBundleGame)).getBundle(String.valueOf(R.string.markerBundle)).get(String.valueOf(R.string.markerBundle))));
            Log.i("FragmentBUNDLELat", String.valueOf(clusterMarkerBundle.getBundle(valueOf(R.string.markerBundleGame)).getBundle(String.valueOf(R.string.markerBundle)).getDouble("lat")));
            Log.i("FragmentBUNDLELng", String.valueOf(clusterMarkerBundle.getBundle(valueOf(R.string.markerBundleGame)).getBundle(String.valueOf(R.string.markerBundle)).getDouble("lnt")));

            clusterMarker = (ClusterMarker) clusterMarkerBundle.getBundle(valueOf(R.string.markerBundleGame)).getBundle(String.valueOf(R.string.markerBundle)).get(String.valueOf(R.string.markerBundle));
            double lat = clusterMarkerBundle.getBundle(valueOf(R.string.markerBundleGame)).getBundle(String.valueOf(R.string.markerBundle)).getDouble("lat");
            double lng = clusterMarkerBundle.getBundle(valueOf(R.string.markerBundleGame)).getBundle(String.valueOf(R.string.markerBundle)).getDouble("lng");

            LatLng latLng = new LatLng(lat, lng);
            clusterMarker.setPosition(latLng);
            event = clusterMarker.getEvent();

            mAuth = FirebaseAuth.getInstance();
            mDb = FirebaseFirestore.getInstance();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_start_game, container, false);

        setupViews(view);
        setupNavigation();
        setData();
        getCurrentUser();
        startTimer(5000, 1000);
        return view;
    }

    private void setData() {
        clusterBundleGame.putParcelable(String.valueOf(R.string.markerBundleGame), clusterMarker);
    }


    public void startTimer(final long finish, long tick) {
        timer = new CountDownTimer(finish, tick) {
            public void onTick(long millisUntilFinished) {
                long remainedSecs = millisUntilFinished / 1000;
                timerText.setText("" + (remainedSecs / 60) + ":" + (remainedSecs % 60) + " seconds");
            }

            public void onFinish() {
                timerText.setText("00:00:00");
                navigateRightEventHandler(clusterBundleGame);
                cancel();
            }
        }.start();
    }

    private void setupViews(View view) {
        timerText = view.findViewById(R.id.timer);
        username = view.findViewById(R.id.startWelcomeValue);
        startEvent = view.findViewById(R.id.startEvent);
        cancelEvent = view.findViewById(R.id.cancelEvent);
        eventName =  view.findViewById(R.id.startWelcomeEventName);
        eventName.setText(event.name);
        startEvent.setOnClickListener(this);
        cancelEvent.setOnClickListener(this);
    }

    private void setupNavigation() {
        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_start_game);
    }

    private int navigateRightEventHandler(Bundle bundle) {
        switch (checkEventType(event)) {
            case Location:
                navController.navigate(R.id.eventStartLocation, bundle);
                break;
            case Quiz:
                navController.navigate(R.id.eventStartQuiz, bundle);
                break;
            case QRcode:
                navController.navigate(R.id.eventStartQRcode, bundle);
                break;
            case PhotoCompare:
                navController.navigate(R.id.eventStartPhotoCompare, bundle);
                break;
        }
        return 1;
    }

    private EventTypes checkEventType(Event event) {
        return event.eventType.eventType;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startEvent:
                navigateRightEventHandler(clusterBundleGame);
                break;
            case R.id.cancelEvent:
                timer.cancel();
                Intent intent = new Intent(getContext(), UserLocationActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void getCurrentUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            DocumentReference docRef = mDb.collection(getString(R.string.collection_users)).document(mAuth.getCurrentUser().getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            User user = task.getResult().toObject(User.class);
                            username.setText(user.getUsername());
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


}
