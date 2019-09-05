package com.example.kotu9.gpsgame.fragment.eventGame;

import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.model.ClusterMarker;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import github.nisrulz.qreader.QRDataListener;
import github.nisrulz.qreader.QREader;

public class EventStartQRcode extends Fragment implements PermissionListener, View.OnClickListener {

    private TextView mQRid, mQRmessage;
    private Chronometer mTimerValue;
    private ToggleButton btnSwitch;
    private SurfaceView scanner;
    private QREader qrEader;
    private ClusterMarker clusterMarker;
    private NavController navController;
    private long timerValue = 0;


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

        setupViews(view);

        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(this)
                .check();

        setupCamera();
        return view;
    }

    private void setupViews(View view) {
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
                            qrEader.stop();
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
