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

import com.example.kotu9.gpsgame.model.Event;
import com.example.kotu9.gpsgame.model.LocationType;
import com.example.kotu9.gpsgame.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.GeoPoint;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;


public class CreateEventLocation extends Fragment implements OnMapReadyCallback, View.OnClickListener {

	private static final String TAG = Activity.class.getSimpleName();
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
				}
				latitude.setText(String.valueOf(latLng.latitude));
				longitude.setText(String.valueOf(latLng.longitude));
				location = new GeoPoint(latLng.latitude, latLng.longitude);
				event.setLocation(location);
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
			event.setLocation(location);

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
}
