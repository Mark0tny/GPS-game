package com.example.kotu9.gpsgame.fragment.eventGame;

import android.Manifest;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.kotu9.gpsgame.R;
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
        if (getArguments() != null) {

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
        mQRid = view.findViewById(R.id.startQReventID);
        mQRmessage = view.findViewById(R.id.startQReventMessage);
        mTimerValue = view.findViewById(R.id.gameTimerValue);
        btnSwitch = view.findViewById(R.id.btn_switch_QR);
        scanner = view.findViewById(R.id.cameraView);
        btnSwitch.setOnClickListener(this);
        mTimerValue.start();
    }

    @Override
    public void onPermissionGranted(PermissionGrantedResponse response) {
            //setupCamera();
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
                        //TODO check event id z data i obsługa bledu
                        // if()
                        mQRid.setText(data);
                    }
                });
            }
        }).facing(QREader.BACK_CAM)
                .enableAutofocus(true)
                .height(scanner.getHeight())
                .width(scanner.getWidth())
                .build();
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
