package com.example.kotu9.gpsgame.fragment.eventGame;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
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
import android.widget.ProgressBar;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.squareup.picasso.Picasso;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class EventStartPhotoCompare extends Fragment implements View.OnClickListener {
    static {
        OpenCVLoader.initDebug();
    }

    private static final String TAG = EventStartPhotoCompare.class.getSimpleName();
    private static final int CHOOSE_IMAGE = 12;
    private static final int TAKE_PHOTO = 13;
    private static final int MY_CAMERA_REQUEST_CODE = 111;
    private static final int REQUEST_PERMISSIONS_CODE_WRITE_STORAGE = 112;

    //Data
    private Uri uriUsersPhoto;
    private Uri uriToFind;
    private String savePath;
    private double compareResult;

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
    private ProgressBar progressBar;

    private LinearLayout imageOld;
    private LinearLayout imageNew;
    private Bitmap bitmap;
    private boolean activeWindowOld = true;
    private boolean imagesMatch = false;

    Mat imageMatFind = new Mat();
    Mat imageMatUser = new Mat();

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
        OpenCVLoader.initDebug();

        if (getArguments() != null) {
            clusterMarker = new ClusterMarker();
            clusterMarker = (ClusterMarker) getArguments().get(String.valueOf(R.string.markerBundleGame));
            photoCompare = new PhotoCompareType((Event) clusterMarker.getEvent());
            savePath = "/" + getString(R.string.app_name) + "/OpenCV/";
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

        progressBar = view.findViewById(R.id.uploadPhoto);

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
        if (checkIfbothImagesLoaded()) {

            openCVcomparasion(uriToFind.getPath(), uriUsersPhoto.getPath());
            if (imagesMatch) {
                eventTimer.stop();
                timerValue = eventTimer.getBase();
                showDialog("Congratulations event finished" + "\nImages match in " + String.format("%.2f", compareResult) + " %");
            } else {
                Toast.makeText(getContext(), "Images do not match. Please try again", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getContext(), "Pleace add picture to compare", Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkIfbothImagesLoaded() {
        if (uriUsersPhoto != null) {
            return true;
        }
        return false;
    }

    private void showDialog(String Message) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
                .setTitle("Quiz")
                .setCancelable(true)
                .setMessage(Message);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                completedGame();
            }
        });
        final AlertDialog alert = dialog.create();
        alert.show();

        new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                alert.dismiss();

            }
        }.start();
    }


    private void completedGame() {
        deleteTempFile();

        Bundle bundle = new Bundle();
        bundle.putParcelable(String.valueOf(R.string.markerBundleGame), clusterMarker);
        bundle.putLong(String.valueOf(R.string.timerBundleGame), timerValue);
        navController.navigate(R.id.eventStartSummary, bundle);
    }

    private void deleteTempFile() {
        File file = new File(uriUsersPhoto.getPath());
        boolean deleted = file.delete();
        if (deleted) Log.i(TAG, "TempFileDeleted");
    }


    private void openCVcomparasion(String toFind, String usersPhoto) {
        imageMatFind = Imgcodecs.imread(toFind, 1);
        imageMatUser = Imgcodecs.imread(usersPhoto, 1);

        Mat hsvBase = new Mat(), hsvTest1 = new Mat();
        Imgproc.cvtColor(imageMatFind, hsvBase, Imgproc.COLOR_RGB2HSV);
        Imgproc.cvtColor(imageMatUser, hsvTest1, Imgproc.COLOR_RGB2HSV);

        int hBins = 50, sBins = 60;
        int[] histSize = {hBins, sBins};
        // hue varies from 0 to 179, saturation from 0 to 255
        float[] ranges = {0, 180, 0, 256};
        // Use the 0-th and 1-st channels
        int[] channels = {0, 1};
        Mat histBase = new Mat(), histTest1 = new Mat();
        List<Mat> hsvBaseList = Arrays.asList(hsvBase);
        Imgproc.calcHist(hsvBaseList, new MatOfInt(channels), new Mat(), histBase, new MatOfInt(histSize), new MatOfFloat(ranges), false);
        Core.normalize(histBase, histBase, 0, 1, Core.NORM_MINMAX);
        List<Mat> hsvTest1List = Arrays.asList(hsvTest1);
        Imgproc.calcHist(hsvTest1List, new MatOfInt(channels), new Mat(), histTest1, new MatOfInt(histSize), new MatOfFloat(ranges), false);
        Core.normalize(histTest1, histTest1, 0, 1, Core.NORM_MINMAX);


        double baseBase = Imgproc.compareHist(histBase, histBase, 0);
        double baseTest1 = Imgproc.compareHist(histBase, histTest1, 0);
        compareResult = (baseTest1 / baseBase) * 100;
        if (compareResult > 40) {
            imagesMatch = true;
        } else {
            imagesMatch = false;
        }
    }

    //Method comparasion
//        for (int compareMethod = 0; compareMethod < 4; compareMethod++) {
//            double baseBase = Imgproc.compareHist(histBase, histBase, compareMethod);
//            double baseTest1 = Imgproc.compareHist(histBase, histTest1, compareMethod);
//            Toast.makeText(getContext(), "Method " + compareMethod + " Perfect, Base-Test(1)" + baseBase + " / " +
//                    baseTest1, Toast.LENGTH_LONG).show();
//        }


    private void changeLayouts() {
        if (activeWindowOld) {
            imageNew.setVisibility(View.VISIBLE);
            imageOld.setVisibility(View.GONE);
            activeWindowOld = false;
        } else {
            imageNew.setVisibility(View.GONE);
            imageOld.setVisibility(View.VISIBLE);
            activeWindowOld = true;
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
                    Picasso.get().load(photoCompare.getImageURLfirebase())
                            .into(mPhotoViewOld);
                    uriToFind = Uri.parse(photoCompare.imageDirectoryPhone);
                    progressBar.setVisibility(View.INVISIBLE);
//                    Picasso.get().load(photoCompare.getImageDirectoryPhone())
//                            .into(mPhotoViewOld);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
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
            uriUsersPhoto = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uriUsersPhoto);
                createMediaFile();
                uriUsersPhoto = Uri.fromFile(saveBitmap(bitmap));
                mPhotoViewNew.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                bitmap = (Bitmap) extras.get("data");
                createMediaFile();
                uriUsersPhoto = Uri.fromFile(saveBitmap(bitmap));
                mPhotoViewNew.setImageBitmap(bitmap);
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
        fileName = "TEMP_" + photoCompare.name;
        return fileName;
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
