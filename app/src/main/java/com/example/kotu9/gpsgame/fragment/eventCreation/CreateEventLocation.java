package com.example.kotu9.gpsgame.fragment.eventCreation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.model.Event;
import com.example.kotu9.gpsgame.model.LocationType;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.GeoPoint;


public class CreateEventLocation extends Fragment implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMarkerDragListener {

    private static final String TAG = AppCompatActivity.class.getSimpleName();
    private Button btnSubmit;
    private ImageButton addLocation;
    private EditText latitude, longitude;
    private GeoPoint location;
    private GoogleMap mMap;
    private MapView mapView;
    public LocationType event;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Marker myEventLoc;
    private NavController navController;


    public CreateEventLocation() {
    }

    public static CreateEventLocation newInstance() {
        CreateEventLocation fragment = new CreateEventLocation();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.fr_add_pleace);
        setRetainInstance(true);
        if (getArguments() != null) {
            event = new LocationType((Event) getArguments().get(String.valueOf(R.string.eventBundle)));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event_location, container, false);

        Toast.makeText(getContext(), event.toString(), Toast.LENGTH_LONG).show();
        setupView(view, savedInstanceState);
        addLocation.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        return view;
    }

    private void changeMarkerPosition(LatLng latLng) {
        if (latLng != null) {
            if (myEventLoc == null) {
                myEventLoc = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Pleace to find"));
            } else {
                myEventLoc.setPosition(latLng);
            }
        }
    }

    private void setupView(View view, Bundle savedInstanceState) {
        btnSubmit = view.findViewById(R.id.submitLocation);
        addLocation = view.findViewById(R.id.imageButtonEvent);
        latitude = view.findViewById(R.id.eventLocLatitude);
        longitude = view.findViewById(R.id.eventLocLongitude);
        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        mapView = view.findViewById(R.id.eventMap);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

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
                            .title("Pleace to find"));

                } else {
                    myEventLoc.setPosition(latLng);
                    myEventLoc.setDraggable(true);
                }
                latitude.setText(String.format("%.4f", latLng.latitude));
                longitude.setText(String.format("%.4f", latLng.longitude));
                location = new GeoPoint(latLng.latitude, latLng.longitude);
                event.setPointLocation(location);

            }
        });

        mMap.setOnMarkerDragListener(this);
        setCameraView();
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

    private void setCameraView() {
        LocationManager mng = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = mng.getLastKnownLocation(mng.getBestProvider(new Criteria(), false));
        if (location != null) {
            double lat = location.getLatitude();
            double lon = location.getLongitude();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 15);
            mMap.animateCamera(cameraUpdate);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submitLocation:
                checkLatLangFields();
                submitEventLocation();
                break;
            case R.id.imageButtonEvent:
                changeMarkerPosition(getPositionEditText());
                break;
        }
    }

    private void submitEventLocation() {
        setEventLocation();
        Bundle eventBundle = new Bundle();
        eventBundle.putSerializable(String.valueOf(R.string.eventBundle), event);
        navController.navigate(R.id.createEventHints, eventBundle);
    }

    private void setEventLocation() {
        if (location != null)
            event.setPointLocation(location);
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

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {
        latitude.setText(String.format("%.4f", marker.getPosition().latitude));
        longitude.setText(String.format("%.4f", marker.getPosition().longitude));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

        latitude.setText(String.format("%.4f", marker.getPosition().latitude));
        longitude.setText(String.format("%.4f", marker.getPosition().longitude));
        location = new GeoPoint(marker.getPosition().latitude, marker.getPosition().longitude);
        event.setPointLocation(location);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
    }
}
