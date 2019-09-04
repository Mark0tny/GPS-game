package com.example.kotu9.gpsgame.fragment.eventGame;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.activity.UserLocationActivity;
import com.example.kotu9.gpsgame.model.ClusterMarker;
import com.example.kotu9.gpsgame.model.Comment;
import com.example.kotu9.gpsgame.model.QuizType;
import com.example.kotu9.gpsgame.model.Rating;
import com.example.kotu9.gpsgame.model.Statistics;
import com.example.kotu9.gpsgame.model.User;
import com.example.kotu9.gpsgame.utils.EventTypes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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
    private Statistics statistics;
    private Comment usersComment;
    private long timerValue;
    private int correctAnswers;

    private Rating eventRating;
    private boolean eventRated = false;
    private ProgressBar progressBar;


    public EventStartSummary() {
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
            statistics = new Statistics();
            usersComment = new Comment();
            clusterMarker = (ClusterMarker) getArguments().get(String.valueOf(R.string.markerBundleGame));
            eventRating = clusterMarker.getEvent().getRating();
            timerValue = getArguments().getLong(String.valueOf(R.string.timerBundleGame));
            if (clusterMarker.getEvent().getEventType().eventType == EventTypes.Quiz)
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
        progressBar = view.findViewById(R.id.uploadDataSummary);
        progressBar.setVisibility(View.INVISIBLE);


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
        long minutes = ((timerValue / (1000 * 60)) % 60);
        double points = 0;
        if (clusterMarker.getEvent().eventType.eventType.equals(EventTypes.Quiz)) {
            points = (clusterMarker.getEvent().getEventType().points) * (correctAnswers / ((QuizType) clusterMarker.getEvent()).getQuestionsList().size());
            points -= minutes;
            return points;
        } else {
            points = (clusterMarker.getEvent().getEventType().points) - minutes;
            return points;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.buttonFinish:
                setComment();
                setStatistics();
                caclucateUsersScore();
                setRating();
                if (checkAllfieldsFill()) {
                    uploadDatabase();
                } else {
                    Toast.makeText(getContext(), "Please add comment and rate event", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private boolean checkAllfieldsFill() {

        return (!TextUtils.isEmpty(mEditComment.getText().toString().trim()) && eventRated);
    }

    private void setRating() {
        eventRating.setGlobalRating(calculateGlobalRating());
    }

    private float calculateGlobalRating() {
        if (eventRating.getUsersRating() != null) {
            float sum = 0;
            for (float f : eventRating.getUsersRating()) {
                sum += f;
            }
            return sum / eventRating.getUsersRating().size();
        } else {
            return 0;
        }
    }

    private void uploadDatabase() {
        updateUserStatistics();
        updateUserScore();
        updateRating();

    }


    private void updateUserStatistics() {
        progressBar.setVisibility(View.VISIBLE);
        DocumentReference newUserRef = mDb
                .collection(getString(R.string.collection_users)).document(mAuth.getCurrentUser().getUid());
        newUserRef.update("completeEvents", FieldValue.arrayUnion(statistics)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                progressBar.setVisibility(View.INVISIBLE);
                Log.i("completeEvents: ", "SUCCESS");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@androidx.annotation.NonNull Exception e) {
                Log.i("update completeEvents", e.getMessage());
            }
        });
    }

    private void updateUserScore() {
        progressBar.setVisibility(View.VISIBLE);
        DocumentReference newUserRef = mDb
                .collection(getString(R.string.collection_users)).document(mAuth.getCurrentUser().getUid());
        newUserRef.update("score", user.score).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                progressBar.setVisibility(View.INVISIBLE);
                Log.i("score: ", "SUCCESS");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@androidx.annotation.NonNull Exception e) {
                Log.i("update score", e.getMessage());
            }
        });
    }

    private void updateRating() {
        progressBar.setVisibility(View.VISIBLE);
        DocumentReference newUserRef = mDb
                .collection(getString(R.string.collection_events))
                .document(clusterMarker.getEvent().getEventType().eventType.name())
                .collection(clusterMarker.getEvent().getId())
                .document(clusterMarker.getEvent().getId());
        newUserRef.update("rating", eventRating).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                progressBar.setVisibility(View.INVISIBLE);
                Log.i("rating: ", "SUCCESS");
                finishSummary();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@androidx.annotation.NonNull Exception e) {
                Log.i("update rating", e.getMessage());
            }
        });

    }


    private void setStatistics() {
        statistics.eventID = clusterMarker.getEvent().id;
        statistics.eventName = clusterMarker.getEvent().name;
        statistics.time = timerValue;
        statistics.points = calculatePointsByTime();
        user.completeEvents.add(statistics);
    }

    private void caclucateUsersScore() {
        user.score += statistics.points;
    }

    private void setComment() {
        if (TextUtils.isEmpty(mEditComment.getText().toString().trim())) {
            mEditComment.setError("Please leave any comment");
            mEditComment.requestFocus();
            return;
        } else {
            usersComment.username = user.username;
            usersComment.commentDate = Calendar.getInstance().getTime();
            usersComment.body = mEditComment.getText().toString().trim();
        }
    }

    private void finishSummary() {
        Intent intent = new Intent(getActivity(), UserLocationActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        eventRated = true;
        eventRating.getUsersRating().add(rating);
    }
}
