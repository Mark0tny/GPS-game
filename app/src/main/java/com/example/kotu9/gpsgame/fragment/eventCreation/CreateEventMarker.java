package com.example.kotu9.gpsgame.fragment.eventCreation;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
import com.example.kotu9.gpsgame.model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;


public class CreateEventMarker extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    private static final String TAG = Activity.class.getSimpleName();
    private Button btnSubmit;
    private ImageButton addLocation;
    private EditText latitude, longitude;
    private GeoPoint location;
    private GoogleMap mMap;
    private MapView mapView;

    public Event event;
    private FirebaseFirestore mDb;
    public static User user;
    private FirebaseAuth mAuth;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Marker myEventLoc;
    private NavController navController;

    private Long radius;

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
        }
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
        setupMapView();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (myEventLoc == null) {
                    myEventLoc = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title("Event position"));
                } else {
                    myEventLoc.setPosition(latLng);
                }
                latitude.setText(String.valueOf(latLng.latitude));
                longitude.setText(String.valueOf(latLng.longitude));
                location = new GeoPoint(latLng.latitude, latLng.longitude);
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
            case R.id.imageButtonEvent:
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
    }

    private void submitMarker() {
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
}