package com.example.kotu9.gpsgame.fragment.eventCreation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.activity.UserLocationActivity;
import com.example.kotu9.gpsgame.model.ClusterMarker;
import com.example.kotu9.gpsgame.model.Event;
import com.example.kotu9.gpsgame.model.LocationType;
import com.example.kotu9.gpsgame.model.PhotoCompareType;
import com.example.kotu9.gpsgame.model.QRcodeType;
import com.example.kotu9.gpsgame.model.User;
import com.example.kotu9.gpsgame.utils.EventTypes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;

import lombok.NonNull;


public class CreateEventMarker extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    private static final String TAG = Activity.class.getSimpleName();
    private Button btnSubmit;
    private ImageButton addLocation;
    private EditText latitude, longitude;
    private GoogleMap mMap;
    private MapView mapView;
    public Event event;
    private FirebaseFirestore mDb;
    private User eventCreator;
    private Marker myEventLoc;
    private Circle geoFenceLimits;
    private float radius;

    private FirebaseAuth mAuth;
    private ClusterMarker eventMarker = new ClusterMarker();

    public CreateEventMarker() {
    }

    public static CreateEventMarker newInstance() {
        CreateEventMarker fragment = new CreateEventMarker();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            event = (Event) getArguments().get(String.valueOf(R.string.eventBundle));
            if (event != null) {
                setGeofanceRadius(event);

            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event_marker, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();
        eventCreator = new User();
        eventCreator.createdEvents = new ArrayList<>();
        setupView(view, savedInstanceState);
        loadUserInformation();
        return view;
    }

    private void setupView(View view, Bundle savedInstanceState) {
        btnSubmit = view.findViewById(R.id.submitMarker);
        addLocation = view.findViewById(R.id.imageButtonMarker);
        latitude = view.findViewById(R.id.eventLocLatitudeMarker);
        longitude = view.findViewById(R.id.eventLocLongitudeMarker);
        mapView = view.findViewById(R.id.markerMap);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
        btnSubmit.setOnClickListener(this);
        addLocation.setOnClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        setupMapView();
        setCameraView();
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        addLocationMarker();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (myEventLoc == null) {
                    markerForGeofence(latLng);

                } else {
                    myEventLoc.setPosition(latLng);
                }
                latitude.setText(String.valueOf(latLng.latitude));
                longitude.setText(String.valueOf(latLng.longitude));
                drawGeofence();
            }
        });
    }


    private void setCameraView() {
        LocationManager mng = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = mng.getLastKnownLocation(mng.getBestProvider(new Criteria(), false));
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 15);
        mMap.animateCamera(cameraUpdate);
    }

    public void setupMapView() {
        UiSettings settings = mMap.getUiSettings();
        settings.setAllGesturesEnabled(true);
        settings.setCompassEnabled(true);
        settings.setMyLocationButtonEnabled(true);
        settings.setRotateGesturesEnabled(true);
        settings.setScrollGesturesEnabled(true);
        settings.setTiltGesturesEnabled(true);
        settings.setZoomControlsEnabled(true);
        settings.setZoomGesturesEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submitMarker:
                checkLatLangFields();
                submitMarker();
                break;
            case R.id.imageButtonMarker:
                changeMarkerPosition(getPositionEditText());
                break;
        }
    }

    private void moveToUserLocationActivity() {
        Intent intent = new Intent(getContext(), UserLocationActivity.class);
        startActivity(intent);
    }

    private LatLng getPositionEditText() {
        try {
            checkLatLangFields();
            double lat = Double.valueOf(latitude.getText().toString().trim());
            double lang = Double.valueOf(longitude.getText().toString().trim());
            return new LatLng(lat, lang);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void changeMarkerPosition(LatLng latLng) {
        if (latLng != null) {
            if (myEventLoc == null) {
                myEventLoc = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Event position"));
            } else {
                myEventLoc.setPosition(latLng);
            }
        }
        drawGeofence();
    }


    private void checkLatLangFields() {
        if (TextUtils.isEmpty(latitude.getText().toString())) {
            latitude.setError("Please enter latitude");
            latitude.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(longitude.getText().toString())) {
            longitude.setError("Please enter longitude");
            longitude.requestFocus();
            return;
        }
        return;
    }


    private EventTypes checkEventType(Event event) {
        return event.eventType.eventType;
    }

    private int setMarkerIcon(Marker marker) {
        switch (checkEventType(event)) {
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

    private void setEventNullValues() {
        event.setActive(true);
        event.geofanceRadius = radius;
        event.eventType.points += calculatePointByDifficulty();
        eventCreator.createdEvents = new ArrayList<>();
        eventCreator.createdEvents.add(event);
    }

    private double calculatePointByDifficulty() {
        switch (event.getDifficulty().ordinal()) {
            case 0:
                return 30;
            case 1:
                return 50;
            case 2:
                return 70;
            default:
                return 30;
        }
    }

    private void addEventFirebase() {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setSslEnabled(true)
                .build();
        mDb.setFirestoreSettings(settings);
        DocumentReference newUserRef = mDb
                .collection(getString(R.string.collection_events)).document(event.eventType.eventType.toString()).collection(event.name + event.hashCode()).document(event.name + event.hashCode());
        newUserRef.set(event).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@lombok.NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Toast.makeText(getContext(), "Saving event in database successfuly", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Could not save event in database", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addPictureFirebase() {
        Uri imageUri = null;

        if (checkEventType(event) == EventTypes.PhotoCompare) {
            PhotoCompareType photoCompareType = (PhotoCompareType) event;
            imageUri = Uri.fromFile(new File(photoCompareType.imageDirectoryPhone + event.name + event.hashCode() + ".jpg"));
            if (imageUri != null) {
                uploadImageToFirebaseStorage(imageUri, EventTypes.PhotoCompare);
            }
        }
        if (checkEventType(event) == EventTypes.QRcode) {
            QRcodeType mQRcodeType = (QRcodeType) event;
            imageUri = Uri.fromFile(new File(mQRcodeType.imageDirectoryPhone + mQRcodeType.fileName + ".png"));
            if (imageUri != null) {
                uploadImageToFirebaseStorage(imageUri, EventTypes.QRcode);
            }
        }
    }

    private void uploadImageToFirebaseStorage(Uri imageUri, EventTypes eventType) {
        final StorageReference ImageURI =
                FirebaseStorage.getInstance().getReference();
        final StorageReference referenceChild = ImageURI.child(eventType.toString() + "/" + event.name + event.hashCode());
        if (imageUri != null) {
            UploadTask uploadTask = referenceChild.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return referenceChild.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                         @Override
                                         public void onComplete(@NonNull Task<Uri> task) {
                                             if (task.isSuccessful()) {
                                                 Uri taskResult = task.getResult();
                                                 DocumentReference newUserRef = mDb
                                                         .collection(getString(R.string.collection_events)).document(event.eventType.eventType.toString()).collection(event.name + event.hashCode()).document(event.name + event.hashCode());
                                                 newUserRef.update(
                                                         "imageURLfirebase", taskResult.toString()
                                                 ).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                     @Override
                                                     public void onComplete(@lombok.NonNull Task<Void> task) {
                                                         if (task.isSuccessful()) {
                                                             Toast.makeText(getActivity(), "Image Successful uploaded", Toast.LENGTH_SHORT).show();
                                                         }
                                                     }
                                                 }).addOnFailureListener(new OnFailureListener() {
                                                     @Override
                                                     public void onFailure(@android.support.annotation.NonNull Exception e) {
                                                         Toast.makeText(getActivity(), "Image save Failure", Toast.LENGTH_SHORT).show();
                                                         Log.i("Image upload: ", e.getMessage());
                                                     }
                                                 });

                                             }
                                         }
                                     }
            ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@android.support.annotation.NonNull Exception e) {
                    Toast.makeText(getActivity(), "Image save Failure", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    private void setEventMarker() {
        //eventMarker.setPosition(myEventLoc.getPosition());
        eventMarker.setPosition(null);
        eventMarker.setPosition2(new GeoPoint(myEventLoc.getPosition().latitude, myEventLoc.getPosition().longitude));
        eventMarker.setTitle(event.name);
        eventMarker.setSnippet(event.eventType.eventType.toString());
        eventMarker.setIconPicture(setMarkerIcon(myEventLoc));
        eventMarker.setEvent(event);
        eventMarker.setOwner(eventCreator);
    }

    private void addMarkerFirebase() {
        DocumentReference newUserRef = mDb
                .collection(getString(R.string.collection_markers)).document("marker_" + event.name + event.hashCode());
        newUserRef.set(eventMarker).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@lombok.NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Saving event in database successfuly", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Could not save event in database", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                            eventCreator = task.getResult().toObject(User.class);
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

    private boolean checkFindPleaceMarkerLocation() {
        if (checkEventType(event) == EventTypes.Location) {
            return calculateDistance(addLocationMarker(), myEventLoc);
        } else return true;
    }

    private boolean calculateDistance(Marker mLocation, Marker mEvent) {
        float[] distance = new float[10];
        Location.distanceBetween(mLocation.getPosition().latitude, mLocation.getPosition().longitude,
                mEvent.getPosition().latitude, mEvent.getPosition().longitude, distance);
        if (distance[0] > radius) {
            return false;
        } else return true;
    }

    private void submitMarker() {
        if (checkFindPleaceMarkerLocation()) {
            setEventNullValues();
            addEventFirebase();
            addPictureFirebase();
            setEventMarker();
            addMarkerFirebase();
            moveToUserLocationActivity();
        } else {
            Toast.makeText(getContext(), "Pleace to find must be inside event radius", Toast.LENGTH_LONG).show();
        }
    }


    private Marker addLocationMarker() {
        if (checkEventType(event) == EventTypes.Location) {
            LocationType locationType = (LocationType) event;
            Marker pleaceToFind = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(locationType.getPointLocation().getLatitude(), locationType.getPointLocation().getLongitude()))
                    .title("Pleace to find"));
            return pleaceToFind;
        }
        return null;
    }

    private float setGeofanceRadius(Event event) {
        switch (event.getDifficulty().ordinal()) {
            case 0:
                return radius = 300f;
            case 1:
                return radius = 500f;
            case 2:
                return radius = 700f;
            default:
                return radius = 300f;

        }
    }

    private void markerForGeofence(LatLng latLng) {
        Log.i(TAG, "markerForGeofence(" + latLng + ")");
        String title = latLng.latitude + ", " + latLng.longitude;
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(title);

        if (mMap != null) {
            if (myEventLoc != null) {
                myEventLoc.remove();
            }
            myEventLoc = mMap.addMarker(markerOptions);
            drawGeofence();
            setMarkerIcon(myEventLoc);
        }
    }

    private void drawGeofence() {
        Log.d(TAG, "drawGeofence()");
        if (geoFenceLimits != null)
            geoFenceLimits.remove();

        CircleOptions circleOptions = new CircleOptions()
                .center(myEventLoc.getPosition())
                .strokeColor(Color.argb(50, 70, 70, 70))
                .fillColor(Color.argb(100, 150, 150, 150))
                .radius(radius);
        geoFenceLimits = mMap.addCircle(circleOptions);
    }

}