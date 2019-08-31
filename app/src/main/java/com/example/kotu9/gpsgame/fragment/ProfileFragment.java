package com.example.kotu9.gpsgame.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.activity.LoginActivity;
import com.example.kotu9.gpsgame.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;

import de.hdodenhof.circleimageview.CircleImageView;
import lombok.NonNull;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private static final int CHOOSE_IMAGE = 101;
    private CircleImageView circleImageView;
    private TextView regDate, score, latitudeText, longitude, username, email;
    private ProgressBar progressBar;
    private Uri uriProfileImage;
    FirebaseAuth mAuth;
    private FirebaseFirestore mDb;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.fr_profile);
        if (getArguments() != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();
        try {
            circleImageView = view.findViewById(R.id.circleProfileImageF);
            progressBar = view.findViewById(R.id.progressbarImage);
            regDate = view.findViewById(R.id.regDateDisplay);
            score = view.findViewById(R.id.scoreDisplay);
            latitudeText = view.findViewById(R.id.latitudeDisplay);
            longitude = view.findViewById(R.id.longitudeDisplay);
            username = view.findViewById(R.id.profUsername);
            email = view.findViewById(R.id.profEmail);
        } catch (Exception e) {
            Log.d("Exception ProfileFrag", e.getMessage());
        }

        loadUserInformation();
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageChooser();
            }
        });
        return view;
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
                            setFieldsValues(user);
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

    private void setFieldsValues(User user) {
        Picasso.get().load(user.getImageUrl())
                .into(circleImageView);
        regDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(user.getRegDate()));
        score.setText(Double.toString(user.getScore()));
        latitudeText.setText(Double.toString(user.getLocation().getLatitude()));
        longitude.setText(Double.toString(user.getLocation().getLongitude()));
        username.setText(user.getUsername());
        email.setText(user.getEmail());
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uriProfileImage);
                circleImageView.setImageBitmap(bitmap);
                uploadImageToFirebaseStorage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }

    private void uploadImageToFirebaseStorage() {
        final StorageReference profileImageURI =
                FirebaseStorage.getInstance().getReference("profileimages/" + mAuth.getCurrentUser().getUid() + ".jpg");
        if (uriProfileImage != null) {
            progressBar.setVisibility(View.VISIBLE);
            UploadTask uploadTask = profileImageURI.putFile(uriProfileImage);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return profileImageURI.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri taskResult = task.getResult();
                        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                                .build();
                        mDb.setFirestoreSettings(settings);
                        DocumentReference newUserRef = mDb
                                .collection(getString(R.string.collection_users))
                                .document(FirebaseAuth.getInstance().getUid());
                        newUserRef.update(
                                "imageUrl", taskResult.toString()
                        ).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@lombok.NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), "Image Successful uploaded", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                }
            });
        }
    }

    public void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE);
    }


}
