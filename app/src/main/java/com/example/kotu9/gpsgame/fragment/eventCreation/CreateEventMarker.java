package com.example.kotu9.gpsgame.fragment.eventCreation;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.model.Event;
import com.example.kotu9.gpsgame.model.LocationType;
import com.example.kotu9.gpsgame.model.User;
import com.example.kotu9.gpsgame.services.GeofenceTrasitionService;
import com.example.kotu9.gpsgame.utils.EventTypes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.GeoPoint;


public class CreateEventMarker extends Fragment implements OnMapReadyCallback, View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    private static final long GEO_DURATION = 60 * 60 * 1000;
    private final int GEOFENCE_REQ_CODE = 0;
    private static final String TAG = Activity.class.getSimpleName();
    private Button btnSubmit;
    private ImageButton addLocation;
    private EditText latitude, longitude;
    private GoogleMap mMap;
    private MapView mapView;

    public Event event;
    private FirebaseFirestore mDb;
    public static User user;
    private FirebaseAuth mAuth;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Marker myEventLoc;
    private NavController navController;
    private GoogleApiClient googleApiClient;
    private PendingIntent geoFencePendingIntent;
    private Circle geoFenceLimits;
    private float radius;

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
        createGoogleApi();
        if (getArguments() != null) {
            event = (Event) getArguments().get(String.valueOf(R.string.eventBundle));
            if (event != null) {
                setGeofanceRadius(event);

            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event_marker, container, false);

        Toast.makeText(getContext(), event.toString(), Toast.LENGTH_LONG).show();

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();
        user = new User();
        setupView(view, savedInstanceState);
        return view;
    }

    private void setupView(View view, Bundle savedInstanceState) {
        btnSubmit = view.findViewById(R.id.submitMarker);
        addLocation = view.findViewById(R.id.imageButtonMarker);
        latitude = view.findViewById(R.id.eventLocLatitudeMarker);
        longitude = view.findViewById(R.id.eventLocLongitudeMarker);
        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        mapView = view.findViewById(R.id.markerMap);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
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
                startGeofence();
            }
        });
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
            case R.id.submitLocation:
                checkLatLangFields();
                submitMarker();
                break;
            case R.id.imageButtonMarker:
                changeMarkerPosition(getPositionEditText());
                break;
        }
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
        startGeofence();
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

    private void setMarkerIcon(Marker marker) {
        switch (checkEventType(event)) {
            case Location:
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon36_location));
                break;
            case Quiz:
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icons36_question));
                break;
            case QRcode:
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon36_qr_code));
                break;
            case PhotoCompare:
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon36_camera));
                break;
        }
    }

    private void setEventNullValues() {
        event.setActive(true);
        event.geofanceRadius = radius;
        event.eventType.points += calculatePointByDifficulty();
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
                .collection(getString(R.string.collection_event)).document(event.eventType.eventType.toString());
        newUserRef.set(event).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    private boolean checkGeofaceLocation() {
        //TODO dodać event do firebase
        return true;
    }

    private void submitMarker() {
        if (checkGeofaceLocation()) {
            setEventNullValues();
            addEventFirebase();
        } else {
            Toast.makeText(getContext(), "Pleace to find must be inside radius", Toast.LENGTH_LONG).show();
        }
    }


    private void addLocationMarker() {
        if (checkEventType(event) == EventTypes.Location) {
            LocationType locationType = (LocationType) event;
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(locationType.getLocation().getLatitude(), locationType.getLocation().getLongitude()))
                    .title("Pleace to find"));
        }

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


    private void createGoogleApi() {
        Log.d(TAG, "createGoogleApi()");
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
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
            setMarkerIcon(myEventLoc);
        }
    }

    private Geofence createGeofence(LatLng latLng, float radius) {
        Log.d(TAG, "createGeofence");
        return new Geofence.Builder()
                .setRequestId(event.name)
                .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                .setExpirationDuration(GEO_DURATION)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
    }

    private GeofencingRequest createGeofenceRequest(Geofence geofence) {
        Log.d(TAG, "createGeofenceRequest");
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build();
    }

    private PendingIntent createGeofencePendingIntent() {
        Log.d(TAG, "createGeofencePendingIntent");
        if (geoFencePendingIntent != null)
            return geoFencePendingIntent;

        Intent intent = new Intent(getActivity(), GeofenceTrasitionService.class);
        return PendingIntent.getService(
                getContext(), GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void addGeofence(GeofencingRequest request) {
        Log.d(TAG, "addGeofence");
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.GeofencingApi.addGeofences(
                googleApiClient,
                request,
                createGeofencePendingIntent()
        ).setResultCallback(this);
    }

    @Override
    public void onResult(@NonNull Status status) {
        Log.i(TAG, "onResult: " + status);
        if (status.isSuccess()) {
            drawGeofence();
        } else {
            // inform about fail
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

    private void startGeofence() {
        Log.i(TAG, "startGeofence()");
        if (myEventLoc != null) {
            Geofence geofence = createGeofence(myEventLoc.getPosition(), radius);
            GeofencingRequest geofenceRequest = createGeofenceRequest(geofence);
            addGeofence(geofenceRequest);
        } else {
            Log.e(TAG, "Geofence marker is null");
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}