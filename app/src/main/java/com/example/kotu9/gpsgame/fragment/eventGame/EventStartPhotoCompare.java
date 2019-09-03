package com.example.kotu9.gpsgame.fragment.eventGame;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.model.ClusterMarker;
import com.example.kotu9.gpsgame.model.Event;
import com.example.kotu9.gpsgame.model.PhotoCompareType;
import com.example.kotu9.gpsgame.model.QuizType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Collections;

import static android.app.Activity.RESULT_OK;


public class EventStartPhotoCompare extends Fragment implements View.OnClickListener {

    private static final String TAG = EventStartPhotoCompare.class.getSimpleName();
    private static final int CHOOSE_IMAGE = 12;
    private static final int TAKE_PHOTO = 13;
    private static final int MY_CAMERA_REQUEST_CODE = 111;
    private static final int REQUEST_PERMISSIONS_CODE_WRITE_STORAGE = 112;

    //Data
    private Uri uriOldImage;
    private FirebaseFirestore mDb;
    private ClusterMarker clusterMarker;
    private long timerValue;
    private PhotoCompareType photoCompare;

    //View
    private NavController navController;
    private Chronometer eventTimer;
    private Button btnComparePhoto, btnSwitch;
    private ImageButton btnPhotoGallery, btnTakePhoto;
    private ImageView mPhotoViewOld, mPhotoViewNew;
    private TextView compareMessage;

    private LinearLayout imageOld;
    private LinearLayout imageNew;
    private Bitmap bitmap;
    private boolean activeWindowOld = true;
    private boolean imagesMatch = false;


    public EventStartPhotoCompare() {

    }

    public static EventStartPhotoCompare newInstance() {
        EventStartPhotoCompare fragment = new EventStartPhotoCompare();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getActivity().setTitle(R.string.fr_start_photo);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            clusterMarker = new ClusterMarker();
            clusterMarker = (ClusterMarker) getArguments().get(String.valueOf(R.string.markerBundleGame));
            photoCompare = new PhotoCompareType((Event) clusterMarker.getEvent());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_start_photo_compare, container, false);

        mDb = FirebaseFirestore.getInstance();
        setupViews(view);
        setObjectToFind();
        eventTimer.start();
        return view;
    }

    private void setupViews(View view) {
        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_start_game);
        eventTimer = view.findViewById(R.id.gameTimerValuePC);
        compareMessage = view.findViewById(R.id.photoMessage);

        btnPhotoGallery = view.findViewById(R.id.imageGalleryGame);
        btnTakePhoto = view.findViewById(R.id.imagePhotoGame);

        btnComparePhoto = view.findViewById(R.id.photoCompareGame);
        btnSwitch = view.findViewById(R.id.photoSwitchGame);

        mPhotoViewOld = view.findViewById(R.id.imageOld);
        mPhotoViewNew = view.findViewById(R.id.imageNew);

        imageOld = view.findViewById(R.id.photosOld);
        imageNew = view.findViewById(R.id.photosNew);

        btnPhotoGallery.setOnClickListener(this);
        btnTakePhoto.setOnClickListener(this);

        btnComparePhoto.setOnClickListener(this);
        btnSwitch.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photoCompareGame:
                compareImages();
                break;
            case R.id.photoSwitchGame:
                changeLayouts();
                break;
            case R.id.imageGalleryGame:
                showImageChooser();
                break;
            case R.id.imagePhotoGame:
                takePhoto();
                break;

        }
    }

    private void compareImages() {
        //TODO jakis czech czy sa oba
        // No i lecimy OPEN CV ale to juz pozniej
        if(imagesMatch){

            eventTimer.stop();
        }else{
            Toast.makeText(getContext(),"Images do not match. Please try again",Toast.LENGTH_LONG).show();
        }
    }

    private void changeLayouts() {
        if(activeWindowOld){
            imageNew.setVisibility(View.VISIBLE);
            imageOld.setVisibility(View.GONE);
            activeWindowOld=false;
        }else{
            imageNew.setVisibility(View.GONE);
            imageOld.setVisibility(View.VISIBLE);
            activeWindowOld=true;
        }

    }

    private void setObjectToFind() {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setSslEnabled(true)
                .build();
        mDb.setFirestoreSettings(settings);
        DocumentReference newUserRef = mDb
                .collection(getString(R.string.collection_events)).document(photoCompare.eventType.eventType.toString()).collection(photoCompare.id).document(photoCompare.id);
        newUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    photoCompare = task.getResult().toObject(PhotoCompareType.class);
                    Picasso.get().load(photoCompare.getImageDirectoryPhone())
                            .into(mPhotoViewOld);
//                    Picasso.get().load(photoCompare.getImageDirectoryPhone())
//                            .into(mPhotoViewOld);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Get picture failure:" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }


    public void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), CHOOSE_IMAGE);
    }

    public void takePhoto() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.CAMERA},
                    MY_CAMERA_REQUEST_CODE
            );
        }
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSIONS_CODE_WRITE_STORAGE
            );
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, TAKE_PHOTO);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriOldImage = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uriOldImage);
                mPhotoViewNew.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                bitmap = (Bitmap) extras.get("data");
                mPhotoViewNew.setImageBitmap(bitmap);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
        if (requestCode == REQUEST_PERMISSIONS_CODE_WRITE_STORAGE) {
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            }
        }
    }

}
