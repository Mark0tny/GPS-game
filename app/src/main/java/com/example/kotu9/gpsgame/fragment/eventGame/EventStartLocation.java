package com.example.kotu9.gpsgame.fragment.eventGame;

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
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.model.ClusterMarker;
import com.example.kotu9.gpsgame.model.Event;
import com.example.kotu9.gpsgame.model.LocationType;
import com.example.kotu9.gpsgame.model.User;
import com.example.kotu9.gpsgame.services.LocationService;
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
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.GeoPoint;

import java.util.Objects;

import static android.content.Context.ACTIVITY_SERVICE;
import static com.example.kotu9.gpsgame.utils.Constants.ERROR_DIALOG_REQUEST;
import static com.example.kotu9.gpsgame.utils.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.example.kotu9.gpsgame.utils.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;
import static java.lang.Thread.sleep;

public class EventStartLocation extends Fragment implements OnMapReadyCallback {
    private static final String TAG = EventStartLocation.class.getSimpleName();
    private static final int DISTANCE_UPDATE_INTERVAL = 2000;

    // Map and markers
    private GoogleMap mMap;
    private MapView mapView;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    //Model
    public static LocationType locationType;
    private ClusterMarker mClusterMarker;
    public static User user;

    //Database
    private FirebaseFirestore mDb;
    private FirebaseAuth mAuth;
    private long timerValue;

    //Geofencing
    private DatabaseReference geoDB;
    private GeoFire geoFire;
    private GeoQuery geofenceFind, geofenceArea;
    private Circle geoFenceLimits;
    private boolean mLocationPermissionGranted = false;

    //View
    private Chronometer eventTimer;
    private NavController navController;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private double distanceFormFind = 0;


    public EventStartLocation() {

    }


    public static EventStartLocation newInstance() {
        EventStartLocation fragment = new EventStartLocation();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getActivity().setTitle(R.string.fr_start_location);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventTimer = new Chronometer(getContext());
            mClusterMarker = new ClusterMarker();
            mClusterMarker = (ClusterMarker) getArguments().get(String.valueOf(R.string.markerBundleGame));
            locationType = new LocationType((Event) mClusterMarker.getEvent());
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_start_location, container, false);
        setHasOptionsMenu(true);

        setupViews(view, savedInstanceState);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mDb = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = new User();
        getLocationType();
        settingGeoFire();
        eventTimer.setBase(SystemClock.elapsedRealtime());
        eventTimer.start();
        return view;
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
        drawGeofence(mClusterMarker);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.start_game_loc_hints, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_showHints:
                showHintDialog();
                return true;
            default:
                break;
        }
        return false;
    }

    private void showHintDialog() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("List of hints:");
        builder.setCancelable(true);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, mClusterMarker.getEvent().hintList.hints);

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkMapServices()) {
            if (mLocationPermissionGranted) {
                loadUserInformation();
                getLastKnownLocation();

            } else {
                getLocationPermission();
            }
        }
    }

    private void setCameraView() {
        if (user != null) {
            if (user.location != null) {
                double bottomBoundary = user.location.getLatitude() - 0.005;
                double leftBoundary = user.location.getLongitude() - 0.005;
                double topBoundary = user.location.getLatitude() + 0.005;
                double rightBoundary = user.location.getLongitude() + 0.005;

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

    private boolean checkMapServices() {
        if (isServicesOK()) {
            return isMapsEnabled();
        }
        return false;
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
                    public void onComplete(@androidx.annotation.NonNull Task<DocumentSnapshot> task) {
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

    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation: called.");
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    user.setLocation(geoPoint);
                    startLocationService();
                    Log.d(TAG, "getLastKnownLocation:" + geoPoint.toString());

                    geoFire.setLocation(mAuth.getCurrentUser().getUid(),
                            new GeoLocation(location.getLatitude(), location.getLongitude()), new
                            GeoFire.CompletionListener() {
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

    private void startLocationService() {
        if (!isLocationServiceRunning()) {
            Intent serviceIntent = new Intent(getActivity(), LocationService.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @lombok.NonNull String permissions[],
                                           @lombok.NonNull int[] grantResults) {
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

    private void getLocationType() {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setSslEnabled(true)
                .build();
        mDb.setFirestoreSettings(settings);
        DocumentReference newUserRef = mDb
                .collection(getString(R.string.collection_events)).document(locationType.eventType.eventType.toString()).collection(locationType.id).document(locationType.id);
        newUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    locationType = task.getResult().toObject(LocationType.class);
                    createGeofenceArea();
                    createGeofenceToFind();
                    startDistanceRunnable();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Get question list failure:" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createGeofenceArea() {
        geofenceArea = geoFire.queryAtLocation(new GeoLocation(
                        mClusterMarker.getPosition().latitude, mClusterMarker.getPosition().longitude)
                , (mClusterMarker.getEvent().geofanceRadius / 1000));
        geofenceArea.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                createNotification(getContext(), "GPSgame", "Welcome back in game area");
                Toast.makeText(getContext(), "Welcome back in game area", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onKeyExited(String key) {
                createNotification(getContext(), "GPSgame",
                        "You have exited the game area, please back");
                Toast.makeText(getContext(), "You have exited the game area", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                if (distanceFormFind < 50) {
                    createNotification(getContext(), "GPSgame", "You are very close");
                    Toast.makeText(getContext(), "You are very close", Toast.LENGTH_LONG).show();
                } else if (distanceFormFind > 51 && distanceFormFind < 100) {
                    createNotification(getContext(), "GPSgame", "You are moving away");
                    Toast.makeText(getContext(), "You are moving away", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }

    private void createGeofenceToFind() {
        geofenceFind = geoFire.queryAtLocation(new GeoLocation(locationType.pointLocation.getLatitude(), locationType.pointLocation.getLongitude()), 0.03);
        Log.e("GeoQuery_ToFind", geofenceFind.getCenter().toString() + geofenceFind.getRadius());

        LatLng latLng = new LatLng(geofenceFind.getCenter().latitude, geofenceFind.getCenter().longitude);
        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)
                .strokeColor(Color.argb(50, 128, 0, 0))
                .fillColor(Color.argb(100, 220, 20, 60))
                .radius(geofenceFind.getRadius());
        geoFenceLimits = mMap.addCircle(circleOptions);

        geofenceFind.addGeoQueryEventListener(new GeoQueryEventListener() {

            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                showDialog("Congratulations\nYou have find hidden pleace");
                eventTimer.stop();
                try {
                    sleep(1000);
                    finishLocGame();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onKeyExited(String key) {
                Toast.makeText(getContext(), "MALA EXITED", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
//                if (distanceFormFind < 25) {
//                    showDialog("Congratulations\nYou have find hidden pleace");
//                    eventTimer.stop();
//                    try {
//                        sleep(1000);
//                        finishLocGame();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                } else if (distanceFormFind < 50 && distanceFormFind > 25) {
//                    createNotification(getContext(), "GPSgame", "You are very close");
//                    Toast.makeText(getContext(), "CLOSE", Toast.LENGTH_LONG).show();
//
//                } else if (distanceFormFind > 51 && distanceFormFind < 100) {
//                    createNotification(getContext(), "GPSgame", "You are moving away");
//                    Toast.makeText(getContext(), "AWAY", Toast.LENGTH_LONG).show();
//                } else if (distanceFormFind > mClusterMarker.getEvent().geofanceRadius / 1000) {
//                    createNotification(getContext(), "GPSgame", "You are exited game arena");
//                    Toast.makeText(getContext(), "You are exited game arena", Toast.LENGTH_LONG).show();
//                }
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void finishLocGame() {
        timerValue = SystemClock.elapsedRealtime() - eventTimer.getBase();
        Bundle bundle = new Bundle();
        bundle.putParcelable(String.valueOf(R.string.markerBundleGame), mClusterMarker);
        bundle.putLong(String.valueOf(R.string.timerBundleGame), timerValue);
        navController.navigate(R.id.eventStartSummary, bundle);
    }


    private void showDialog(String Message) {
        final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getContext())
                .setTitle("Quiz")
                .setCancelable(true)
                .setMessage(Message);
        final android.app.AlertDialog alert = dialog.create();
        alert.show();

        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                alert.dismiss();
            }
        }.start();
    }

    private void settingGeoFire() {
        geoDB = FirebaseDatabase.getInstance().getReference("EventLocationGame");
        geoFire = new GeoFire(geoDB);
    }

    private void setupViews(View view, Bundle savedInstanceState) {
        eventTimer = view.findViewById(R.id.gameTimerValueLoc);
        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_start_game);

        mapView = view.findViewById(R.id.mapLocation);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

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

    private void startDistanceRunnable() {
        Log.d(TAG, "startUserLocationsRunnable: starting runnable for retrieving updated locations.");
        mHandler.postDelayed(mRunnable = new Runnable() {
            @Override
            public void run() {
                distanceFormFind = calculateDistanceGeofence();
                mHandler.postDelayed(mRunnable, DISTANCE_UPDATE_INTERVAL);
            }
        }, DISTANCE_UPDATE_INTERVAL);
    }

    private double calculateDistanceGeofence() {
        float[] distance = new float[10];
        Location.distanceBetween(user.getLocation().getLatitude(), user.getLocation().getLongitude(),
                geofenceFind.getCenter().latitude, geofenceFind.getCenter().longitude, distance);
        return distance[0];
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

}
