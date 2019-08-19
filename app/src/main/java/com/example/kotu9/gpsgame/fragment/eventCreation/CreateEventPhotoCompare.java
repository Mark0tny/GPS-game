package com.example.kotu9.gpsgame.fragment.eventCreation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.model.Event;
import com.example.kotu9.gpsgame.model.PhotoCompareType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static android.app.Activity.RESULT_OK;


public class CreateEventPhotoCompare extends Fragment implements View.OnClickListener {
    private static final int CHOOSE_IMAGE = 12;
    private static final int TAKE_PHOTO = 13;
    private static final int MY_CAMERA_REQUEST_CODE = 111;
    private static final int REQUEST_PERMISSIONS_CODE_WRITE_STORAGE = 112;


    private Button btnSubmitPhoto;
    private ImageButton btnPhotoGallery, btnTakePhoto;
    private ImageView mPhotoView;
    private PhotoCompareType event;
    private NavController navController;
    private Bitmap bitmap;
    private FirebaseAuth mAuth;
    private Uri uriPhoto;
    private String savePath;

    public CreateEventPhotoCompare() {

    }

    public static CreateEventPhotoCompare newInstance(String param1, String param2) {
        CreateEventPhotoCompare fragment = new CreateEventPhotoCompare();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        savePath = "/" + getString(R.string.app_name) + "/PhotoCompare/";
        if (getArguments() != null) {
            event = new PhotoCompareType((Event) getArguments().get(String.valueOf(R.string.eventBundle)));
        } else {
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event_photo_compare, container, false);
        setupViews(view);
        return view;
    }

    private void setupViews(View view) {
        btnPhotoGallery = view.findViewById(R.id.imageGallery);
        btnTakePhoto = view.findViewById(R.id.imagePhoto);
        btnSubmitPhoto = view.findViewById(R.id.submitPhoto);
        mPhotoView = view.findViewById(R.id.imageViewphoto);
        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        btnPhotoGallery.setOnClickListener(this);
        btnTakePhoto.setOnClickListener(this);
        btnSubmitPhoto.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submitPhoto:
                submitPhoto();
                break;
            case R.id.imageGallery:
                showImageChooser();
                break;
            case R.id.imagePhoto:
                takePhoto();
                break;

        }
    }

    private void submitPhoto() {
        setPhoto();
        Bundle eventBundle = new Bundle();
        eventBundle.putSerializable(String.valueOf(R.string.eventBundle), event);
        navController.navigate(R.id.createEventHints, eventBundle);
    }

    private void setPhoto() {
        event.photoDirectory = createMediaFile();
        event.photoURL = null;
        event.status = false;
    }

    private String createMediaFile() {
        String folderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + savePath;
        File mediaStorageDir = new File(folderPath);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Toast.makeText(getContext(), folderPath, Toast.LENGTH_SHORT).show();
                return mediaStorageDir.getAbsolutePath();
            }
        }
        return folderPath;
    }

    private String getPhotoName() {
        String fileName;
        fileName = "PHOTO_" + event.name + "_" + event.hashCode();
        return fileName;
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
            uriPhoto = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uriPhoto);
                mPhotoView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                bitmap = (Bitmap) extras.get("data");
                createMediaFile();
                saveBitmap(bitmap);
                mPhotoView.setImageBitmap(bitmap);
            }

        }

    }

    private File saveBitmap(Bitmap bmp) {

        OutputStream outStream = null;
        File file = new File(createMediaFile(), getPhotoName() + ".jpeg");
        if (file.exists()) {
            file.delete();
            file = new File(createMediaFile(), getPhotoName() + ".jpeg");
        }
        try {
            outStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return file;
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
