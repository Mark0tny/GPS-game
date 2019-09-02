package com.example.kotu9.gpsgame.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.adapters.MessagesRecyclerViewAdapter;
import com.example.kotu9.gpsgame.model.Message;
import com.example.kotu9.gpsgame.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;

public class MessagesFragment extends Fragment {
    private final static String TAG = "MessagesFragment";

    private RecyclerView mRecyclerViewMessages;
    private MessagesRecyclerViewAdapter mAdapterMessages;
    private List<Message> messageList = new ArrayList<>();

    FirebaseAuth mAuth;
    private FirebaseFirestore mDb;

    public MessagesFragment() {

    }

    public static MessagesFragment newInstance() {
        MessagesFragment fragment = new MessagesFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.fr_messages);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();
        loadUserInformation();
        initCommentsListRecyclerView();
        return view;
    }

    private void initCommentsListRecyclerView() {
        if (getContext() != null) {

            mAdapterMessages = new MessagesRecyclerViewAdapter(getContext(), messageList);
            mRecyclerViewMessages.setAdapter(mAdapterMessages);
            mRecyclerViewMessages.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }

    private void loadUserInformation() {
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
                            messageList = user.messages;
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
