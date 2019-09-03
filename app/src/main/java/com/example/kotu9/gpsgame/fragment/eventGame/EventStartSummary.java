package com.example.kotu9.gpsgame.fragment.eventGame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.activity.UserLocationActivity;
import com.example.kotu9.gpsgame.model.ClusterMarker;
import com.example.kotu9.gpsgame.model.Rating;
import com.example.kotu9.gpsgame.model.User;
import com.example.kotu9.gpsgame.utils.EventTypes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;

import lombok.NonNull;

public class EventStartSummary extends Fragment implements View.OnClickListener, RatingBar.OnRatingBarChangeListener {

    private static final String TAG = EventStartSummary.class.getSimpleName();

    private EditText mEditComment;
    private Button btnFinish;
    private RatingBar ratingBarEvent;
    private TextView mUsername, mEventName, mTimeValue, mPointsValue;

    private ClusterMarker clusterMarker;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;
    private User user;
    private long timerValue;
    private int correctAnswers;

    private Rating eventRating;


    public EventStartSummary() {
        // Required empty public constructor
    }

    public static EventStartSummary newInstance() {
        EventStartSummary fragment = new EventStartSummary();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getActivity().setTitle(R.string.fr_start_summary);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventRating = new Rating();
            clusterMarker = new ClusterMarker();
            clusterMarker = (ClusterMarker) getArguments().get(String.valueOf(R.string.markerBundleGame));
            timerValue = getArguments().getLong(String.valueOf(R.string.timerBundleGame));
            if (clusterMarker.getEvent().getEventType().eventType == EventTypes.PhotoCompare)
                correctAnswers = getArguments().getInt(String.valueOf(R.string.answersBundleGame));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_event_start_summary, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();
        loadUserInformation();
        setupViews(view);
        return view;
    }

    private void setupViews(View view) {

        mEditComment = view.findViewById(R.id.editComment);
        btnFinish = view.findViewById(R.id.buttonFinish);
        ratingBarEvent = view.findViewById(R.id.ratingEventSummary);
        mUsername = view.findViewById(R.id.startWelcomeValue);
        mEventName = view.findViewById(R.id.eventNameValue);
        mTimeValue = view.findViewById(R.id.eventTimeValue);
        mPointsValue = view.findViewById(R.id.eventPointsValue);

        ratingBarEvent.setOnRatingBarChangeListener(this);
        btnFinish.setOnClickListener(this);
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
                            setTextViewValues();
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

    private void setTextViewValues() {

        SimpleDateFormat dt = new SimpleDateFormat("mm:ss");
        mUsername.setText(user.username);
        mEventName.setText(clusterMarker.getEvent().name);
        mTimeValue.setText(dt.format(timerValue));
        mPointsValue.setText(String.valueOf(calculatePointsByTime()));

    }

    private double calculatePointsByTime() {
        return 0;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.buttonFinish:
                //finishSummary();
                break;
        }
    }

    private void finishSummary() {
        Intent intent = new Intent(getActivity(), UserLocationActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        eventRating.getUsersRating().add(rating);
    }
}
