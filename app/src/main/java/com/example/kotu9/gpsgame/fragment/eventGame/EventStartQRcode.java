package com.example.kotu9.gpsgame.fragment.eventGame;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.model.ClusterMarker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CircleOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import github.nisrulz.qreader.QRDataListener;
import github.nisrulz.qreader.QREader;

public class EventStartQRcode extends Fragment implements PermissionListener, View.OnClickListener, OnMapReadyCallback {

    private static final String TAG = EventStartQRcode.class.getSimpleName();

    private TextView mQRid, mQRmessage;
    private Chronometer mTimerValue;
    private ToggleButton btnSwitch;
    private SurfaceView scanner;
    private QREader qrEader;
    private ClusterMarker clusterMarker;
    private NavController navController;
    private long timerValue = 0;

    private FrameLayout frameLayout;
    private GoogleMap mMap;
    private MapView mapView;


    public EventStartQRcode() {

    }

    public static EventStartQRcode newInstance() {
        EventStartQRcode fragment = new EventStartQRcode();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.fr_start_QR);
        setRetainInstance(true);
        if (getArguments() != null) {
            clusterMarker = new ClusterMarker();
            clusterMarker = (ClusterMarker) getArguments().get(String.valueOf(R.string.markerBundleGame));
            mTimerValue = new Chronometer(getContext());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_start_qrcode, container, false);

        setupViews(view,savedInstanceState);
        setHasOptionsMenu(true);
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(this)
                .check();

        setupCamera();
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
        drawGeofence(clusterMarker);
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

    private void drawGeofence(ClusterMarker clusterMarker) {
        Log.d(TAG, "drawGeofence()");

        CircleOptions circleOptions = new CircleOptions()
                .center(clusterMarker.getPosition())
                .strokeColor(Color.argb(50, 70, 70, 70))
                .fillColor(Color.argb(100, 150, 150, 150))
                .radius(clusterMarker.getEvent().geofanceRadius);
        mMap.addCircle(circleOptions);

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.start_game_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_showHints:
                showHintDialog();
                return true;
            case R.id.action_MapView:
                frameLayout.setVisibility(View.GONE);
                mapView.setVisibility(View.VISIBLE);
                return true;
            case R.id.action_eventView:
                mapView.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
                return true;

            default:
                break;
        }
        return false;
    }

    private void showHintDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("List of hints:");
        builder.setCancelable(true);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, clusterMarker.getEvent().hintList.hints);

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setupViews(View view, Bundle savedInstanceState) {
        frameLayout = view.findViewById(R.id.scanner);
        mapView = view.findViewById(R.id.mapQR);

        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_start_game);
        mQRid = view.findViewById(R.id.startQReventID);
        mQRmessage = view.findViewById(R.id.startQReventMessage);
        mTimerValue = view.findViewById(R.id.gameTimerValueQR);
        btnSwitch = view.findViewById(R.id.btn_switch_QR);
        scanner = view.findViewById(R.id.cameraView);
        btnSwitch.setOnClickListener(this);
        mTimerValue.setBase(SystemClock.elapsedRealtime());
        mTimerValue.start();
    }

    @Override
    public void onPermissionGranted(PermissionGrantedResponse response) {

    }

    @Override
    public void onPermissionDenied(PermissionDeniedResponse response) {
        Toast.makeText(getContext(), "you must enable this permission", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

    }

    @Override
    public void onClick(View v) {
        if (qrEader.isCameraRunning()) {
            btnSwitch.setChecked(false);
            qrEader.stop();
        } else {
            btnSwitch.setChecked(true);
            qrEader.start();
        }
    }

    private void setupCamera() {
        qrEader = new QREader.Builder(getContext(), scanner, new QRDataListener() {
            @Override
            public void onDetected(final String data) {
                mQRid.post(new Runnable() {
                    @Override
                    public void run() {
                        mQRid.setText(data);
                        Log.i("TAG", "NAME:" + clusterMarker.getEvent().id);
                        Log.i("TAG", "DATA:" + data);
                        Log.i("TAG", "BOLEAN:" + clusterMarker.getEvent().id.equalsIgnoreCase(data));

                        if (clusterMarker.getEvent().id.equalsIgnoreCase(data)) {
                            mQRid.setText(data);
                            mTimerValue.stop();
                            timerValue = SystemClock.elapsedRealtime() - mTimerValue.getBase();
                            Log.i("TAG", "TIMER:" + timerValue);
                            mQRmessage.setText("Correct Qrcode");
                            mQRmessage.setTextColor(Color.WHITE);
                            mQRmessage.setVisibility(View.VISIBLE);


                            Bundle bundle = new Bundle();
                            bundle.putParcelable(String.valueOf(R.string.markerBundleGame), clusterMarker);
                            bundle.putLong(String.valueOf(R.string.timerBundleGame), timerValue);
                            completedGame(bundle);

                            return;
                        } else {
                            mQRmessage.setText("Incorrect Qrcode");
                            mQRmessage.setTextColor(Color.RED);
                            mQRmessage.setVisibility(View.VISIBLE);
                            return;
                        }

                    }
                });
            }
        }).facing(QREader.BACK_CAM)
                .enableAutofocus(true)
                .height(scanner.getHeight())
                .width(scanner.getWidth())
                .build();
    }

    private void completedGame(Bundle bundle) {
        qrEader.stop();
        navController.navigate(R.id.eventStartSummary, bundle);
    }


    @Override
    public void onResume() {
        super.onResume();
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        if (qrEader != null) {
                            qrEader.initAndStart(scanner);
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                })
                .check();
    }

    @Override
    public void onPause() {
        super.onPause();
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        if (qrEader != null) {
                            qrEader.releaseAndCleanup();
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                })
                .check();
    }
}
