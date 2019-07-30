package com.example.kotu9.gpsgame.Fragment.EventCreation;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.kotu9.gpsgame.Model.Event;
import com.example.kotu9.gpsgame.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.firestore.GeoPoint;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;


public class CreateEventMarker extends Fragment implements OnMapReadyCallback {

	private static final String TAG = Activity.class.getSimpleName();
	private Button btnSubmit;
	private ImageButton addLocation;
	private EditText latitude, longitude;
	private GeoPoint location;
	private GoogleMap mMap;
	private MapView mapView;
	public Event event;
	private FusedLocationProviderClient mFusedLocationProviderClient;
	private Marker myEventLoc;
	private NavController navController;

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
		if (getArguments() != null) {
			event = (Event) getArguments().get(String.valueOf(R.string.eventBundle));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_create_event_marker, container, false);

		Toast.makeText(getContext(), event.toString(), Toast.LENGTH_LONG).show();
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
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
		if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			return;
		}
		mMap.setMyLocationEnabled(true);
		setupMapView();
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
}