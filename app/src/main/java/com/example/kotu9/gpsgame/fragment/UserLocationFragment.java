package com.example.kotu9.gpsgame.fragment;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.activity.EventGameActivity;
import com.example.kotu9.gpsgame.adapters.MarkerRecyclerViewAdapter;
import com.example.kotu9.gpsgame.model.ClusterMarker;
import com.example.kotu9.gpsgame.model.User;
import com.example.kotu9.gpsgame.services.LocationService;
import com.example.kotu9.gpsgame.utils.ClusterManagerRenderer;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.NonNull;

import static android.content.Context.ACTIVITY_SERVICE;
import static com.example.kotu9.gpsgame.utils.Constants.ERROR_DIALOG_REQUEST;
import static com.example.kotu9.gpsgame.utils.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.example.kotu9.gpsgame.utils.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class UserLocationFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, MarkerRecyclerViewAdapter.OnMarkerClickListener {

    private static final String TAG = "UserLocationActivity";
    private static final String KEY = "You";
    private FirebaseAuth mAuth;
    private GoogleMap mMap;
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    public static User user;
    private FirebaseFirestore mDb;

    private ClusterManager<ClusterMarker> mClusterManager;
    private ClusterManagerRenderer mClusterManagerRenderer;
    private ArrayList<ClusterMarker> mClusterMarkers;
    private ClusterMarker clickedClusterMarker;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private static final int DISTANCE_UPDATE_INTERVAL = 20000;

    private RecyclerView mMarkerListRecyclerView;
    private MarkerRecyclerViewAdapter markerRecyclerViewAdapter;
    private View fragmentMap;

    private DatabaseReference geoDB;
    private GeoFire geoFire;
    private LatLng globalPostition = new LatLng(50, 50);

    public UserLocationFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClusterMarkers = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_user_location, container, false);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        fragmentMap = rootView.findViewById(R.id.map);
        setHasOptionsMenu(true);
        mMarkerListRecyclerView = rootView.findViewById(R.id.markerRecyclerview);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();
        user = new User();
        resetMap();
        getMapMarkersListDB();
        settingGeoFire();

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        setupMapView();
        Context context = getActivity();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnInfoWindowClickListener(this);
    }

    private boolean checkMapServices() {
        if (isServicesOK()) {
            return isMapsEnabled();
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkMapServices()) {
            if (mLocationPermissionGranted) {
                getLastKnownLocation();
                loadUserInformation();
            } else {
                getLocationPermission();
            }
        }
        startDistanceRunnable();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.user_location, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_mapView) {
            mMarkerListRecyclerView.setVisibility(View.GONE);
            fragmentMap.setVisibility(View.VISIBLE);
            return true;
        }
        if (id == R.id.action_listView) {
            fragmentMap.setVisibility(View.GONE);
            mMarkerListRecyclerView.setVisibility(View.VISIBLE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void initMarkerListRecyclerView() {
        if (getContext() != null) {
            markerRecyclerViewAdapter = new MarkerRecyclerViewAdapter(getContext(), mClusterMarkers, this);
            mMarkerListRecyclerView.setAdapter(markerRecyclerViewAdapter);
            mMarkerListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mMarkerListRecyclerView.setVisibility(View.GONE);
        }
    }

    private List<ClusterMarker> updateMarkers(List<ClusterMarker> clusterMarkers) {
        //TODO update zrobic/ dodawac do adaptera i addMarka

        return clusterMarkers;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        stopDistanceUpdates();
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AppCompatActivity) {
            AppCompatActivity activity;
            activity = (AppCompatActivity) context;
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
    }

    public boolean isMapsEnabled() {
        LocationManager manager =
                (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!(manager != null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER))) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            getLastKnownLocation();
            loadUserInformation();
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());

        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(getActivity(), "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void loadUserInformation() {
        if (user != null) {
            FirebaseUser userAuth = mAuth.getCurrentUser();
            if (userAuth != null) {
                DocumentReference docRef = mDb.collection(getString(R.string.collection_users)).document(mAuth.getCurrentUser().getUid());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@android.support.annotation.NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                user = task.getResult().toObject(User.class);
                                setCameraView();
                                getLastKnownLocation();
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
            }
        } else {
            getLastKnownLocation();
        }
    }

    private void saveUserLocation() {
        if (user != null) {
            DocumentReference docRef = mDb.collection(getString(R.string.collection_users)).document(mAuth.getCurrentUser().getUid());
            docRef.update("location", user.getLocation()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@android.support.annotation.NonNull Task<Void> task) {
                    Log.d(TAG, "saveUserLocation: location saved in DB");
                }
            });
        }
    }

    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation: called.");
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@android.support.annotation.NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    user.setLocation(geoPoint);
                    saveUserLocation();
                    Log.d(TAG, "getLastKnownLocation:" + geoPoint.toString());
                    geoFire.setLocation(KEY, new GeoLocation(location.getLatitude(), location.getLongitude()),new
                            GeoFire.CompletionListener(){
                                @Override
                                public void onComplete(String key, DatabaseError error) {
                                    Log.e(TAG, "GeoFire Complete");
                                }
                            });
                    startLocationService();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        Log.d(TAG, "SERVICE CALLBACK");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (mLocationPermissionGranted) {
                    getLastKnownLocation();
                    loadUserInformation();
                } else {
                    getLocationPermission();
                }
            }
        }
    }

    private void setupMapView() {
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
        if (user != null) {
            if (user.location != null) {
                double bottomBoundary = user.location.getLatitude() - .1;
                double leftBoundary = user.location.getLongitude() - .1;
                double topBoundary = user.location.getLatitude() + .1;
                double rightBoundary = user.location.getLongitude() + .1;

                LatLngBounds mMapBoundary = new LatLngBounds(
                        new LatLng(bottomBoundary, leftBoundary),
                        new LatLng(topBoundary, rightBoundary)
                );
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 10));
            }
        } else {
            Log.d(TAG, "setCameraView user null");
            Toast.makeText(getContext(), "setCameraView user null", Toast.LENGTH_SHORT).show();
        }

    }

    private void startLocationService() {
        if (!isLocationServiceRunning()) {
            Intent serviceIntent = new Intent(getActivity(), LocationService.class);

            if (Build.VERSION.SDK_INT >= VERSION_CODES.O) {
                getActivity().startForegroundService(serviceIntent);
            } else {
                getActivity().startService(serviceIntent);
            }
        }
    }

    private boolean isLocationServiceRunning() {
        try {
            ActivityManager manager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (".Services.LocationService".equals(service.service.getClassName())) {
                    Log.d(TAG, "isLocationServiceRunning: location service is already running.");
                    return true;
                }
            }
            Log.d(TAG, "isLocationServiceRunning: location service is not running.");
        } catch (NullPointerException e) {
            Log.d(TAG, e.getMessage());
        }
        return false;
    }


    private void addMapMarkers(final List<ClusterMarker> mClusterMarkers1) {
        if (mMap != null) {
            if (mClusterManager == null) {
                mClusterManager = new ClusterManager<ClusterMarker>(getActivity().getApplicationContext(), mMap);
            }
            if (mClusterManagerRenderer == null) {
                mClusterManagerRenderer = new ClusterManagerRenderer(
                        getActivity(),
                        mMap,
                        mClusterManager
                );
                mClusterManager.setRenderer(mClusterManagerRenderer);
            }

            for (ClusterMarker clusterMarker : mClusterMarkers1) {
                clusterMarker.setIconPicture(clusterMarker.getIconPicture());
                if (clusterMarker.getEvent().active) {
                    mClusterManager.addItem(clusterMarker);
                    drawGeofence(clusterMarker);
                    addGeofenceToMarkers(clusterMarker);
                } else {
                    Log.i(TAG, "Event " + clusterMarker.getEvent().name + " inactive");
                }
            }

            mClusterManager.cluster();
            setCameraView();
        }
    }

    private void getMapMarkersListDB() {
        mDb.collection(getString(R.string.collection_markers)).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            Log.d(TAG, "onSuccess: LIST EMPTY");
                            return;
                        } else {
                            List<ClusterMarker> clusterMarkerList = documentSnapshots.toObjects(ClusterMarker.class);
                            mClusterMarkers.addAll(clusterMarkerList);
                            for (ClusterMarker clusterMarker : mClusterMarkers) {
                                clusterMarker.setPosition(new LatLng(clusterMarker.getPosition2().getLatitude(), clusterMarker.getPosition2().getLongitude()));
                            }
                            Log.d(TAG, "onSuccess: " + mClusterMarkers);
                            addMapMarkers(mClusterMarkers);
                            initMarkerListRecyclerView();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@android.support.annotation.NonNull Exception e) {
                Toast.makeText(getContext(), "Error getting data!!!", Toast.LENGTH_LONG).show();
                Log.i(TAG, e.getMessage());
            }
        });
    }

    private void resetMap() {
        if (mMap != null) {
            mMap.clear();
            if (mClusterManager != null) {
                mClusterManager.clearItems();
            }
            if (mClusterMarkers.size() > 0) {
                mClusterMarkers.clear();
                mClusterMarkers = new ArrayList<>();
            }
        }
    }

    private void startDistanceRunnable() {
        Log.d(TAG, "startUserLocationsRunnable: starting runnable for retrieving updated locations.");
        mHandler.postDelayed(mRunnable = new Runnable() {
            @Override
            public void run() {
                calulateDistanceMarkers();
                mHandler.postDelayed(mRunnable, DISTANCE_UPDATE_INTERVAL);
            }
        }, DISTANCE_UPDATE_INTERVAL);
    }

    private void stopDistanceUpdates() {
        mHandler.removeCallbacks(mRunnable);
    }

    private double calculateDistance(User user, ClusterMarker mEvent) {
        float[] distance = new float[10];
        Location.distanceBetween(user.getLocation().getLatitude(), user.getLocation().getLongitude(),
                mEvent.getPosition().latitude, mEvent.getPosition().longitude, distance);
        return distance[0];
    }

    private void calulateDistanceMarkers() {
        try {
            for (final ClusterMarker clusterMarker : mClusterMarkers) {
                clusterMarker.getEvent().distance = calculateDistance(user, clusterMarker);
                DocumentReference distanceRef = FirebaseFirestore.getInstance()
                        .collection(getString(R.string.collection_markers))
                        .document("marker_" + clusterMarker.getEvent().id);
                distanceRef.update("distance", clusterMarker.getEvent().distance).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@android.support.annotation.NonNull Task<Void> task) {
                        //Log.d(TAG, clusterMarker.getEvent().name + " distance: " + clusterMarker.getEvent().distance);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@android.support.annotation.NonNull Exception e) {
                        Log.d(TAG, e.getMessage());

                    }
                });
            }
        } catch (IllegalStateException e) {
            Log.e(TAG, "Fragment was destroyed during Firestore query. Ending query." + e.getMessage());
        }

    }

    @Override
    public void onInfoWindowClick(final Marker marker) {
        Bundle bundle = new Bundle();
        clickedClusterMarker = mClusterManagerRenderer.getClusterItem(marker);
        bundle.putSerializable(String.valueOf(R.string.markerBundle), mClusterManagerRenderer.getClusterItem(marker));
        bundle.putDouble("lat", mClusterManagerRenderer.getClusterItem(marker).getPosition().latitude);
        bundle.putDouble("lng", mClusterManagerRenderer.getClusterItem(marker).getPosition().longitude);
        generateEventClickDialog(bundle);
    }

    private void generateEventClickDialog(final Bundle bundle) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setMessage("What you want to do ?\nMove to Details or Start event")
                .setCancelable(true)
                .setPositiveButton("Start event game", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent intent = new Intent(getContext(), EventGameActivity.class);
                        intent.putExtra(String.valueOf(R.string.markerBundle), bundle);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                })
                .setNeutralButton("Show event details", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Fragment detailsFragment = new EventDetails();
                        detailsFragment.setArguments(bundle);
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame_container, detailsFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();

                        dialog.dismiss();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
        alert.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.WHITE);
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.GRAY);
        alert.getButton(AlertDialog.BUTTON_NEUTRAL).setBackgroundColor(Color.GRAY);
    }

    @Override
    public void onMarkerListClick(int position) {
        Bundle bundle = new Bundle();
        clickedClusterMarker = mClusterMarkers.get(position);

        bundle.putSerializable(String.valueOf(R.string.markerBundle), mClusterMarkers.get(position));
        generateEventClickDialog(bundle);
    }

    private void createNotification(Context context, String locTransitionType, String locationDetails) {
        String CHANNEL_ID = "GPSgame";
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.fui_ic_mail_white_24dp)
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                        .setContentTitle(locTransitionType)
                        .setContentText(locationDetails)
                        .setAutoCancel(true)
                        .setCategory("Message")
                        .setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(0, builder.build());
        Log.d("IntentServiceNoti", builder.build().toString());

    }

    private void drawGeofence(ClusterMarker clusterMarker) {
        Log.d(TAG, "drawGeofence()");

        CircleOptions circleOptions = new CircleOptions()
                .center(clusterMarker.getPosition())
                .strokeColor(Color.argb(50, 70, 70, 70))
                .fillColor(Color.argb(100, 150, 150, 150))
                .radius(clusterMarker.getEvent().geofanceRadius);
        mMap.addCircle(circleOptions);

    }

    private void settingGeoFire() {
        geoDB = FirebaseDatabase.getInstance().getReference("GeofenceMarkers");
        geoFire = new GeoFire(geoDB);
    }

    private void addGeofenceToMarkers(ClusterMarker clusterMarker) {
        GeoQuery geofence = geoFire.queryAtLocation(new GeoLocation(clusterMarker.getPosition().latitude, clusterMarker.getPosition().longitude), (clusterMarker.getEvent().geofanceRadius/1000));
        Log.i("GeoQuery", geofence.getCenter().toString() + geofence.getRadius() + "CLUSTER RADIUS" + clusterMarker.getEvent().geofanceRadius/1000);
        geofence.addGeoQueryEventListener(new GeoQueryEventListener() {

            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //TODO wykrywanie który marker i elo po lokacji ?

                // createNotification(getContext(), "GPSgame", key + "entered the " + clickedClusterMarker.getEvent().name + "area");
                Toast.makeText(getContext(), "GEOFENCE Entered" + location.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onKeyExited(String key) {
                // createNotification(getContext(), "GPSgame", key + "e the " + clickedClusterMarker.getEvent().name + "area");
                Toast.makeText(getContext(), "GEOFENCE Exited" + key, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                // createNotification(getContext(), "GPSgame", key + "are in " + clickedClusterMarker.getEvent().name + "area");
                Toast.makeText(getContext(), "GEOFENCE Moved" + location.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onGeoQueryReady() {
                Log.i(TAG, "GeoQueryReady");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}



