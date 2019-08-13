package com.example.kotu9.gpsgame.fragment.eventCreation;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.model.Event;
import com.example.kotu9.gpsgame.model.QRcodeType;

public class CreateEventQRcode extends Fragment implements View.OnClickListener {
    private static final String TAG = Activity.class.getSimpleName();

    private Button btnSubmitQRcode, btnGenerateQRcode, btnSendEmail, btnDownloadQRcode;
    private EditText editTextMessageQRcode;
    private ImageView mQRcodeView;
    private QRcodeType event;
    private NavController navController;

    public CreateEventQRcode() {

    }

    public static CreateEventQRcode newInstance() {
        CreateEventQRcode fragment = new CreateEventQRcode();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        event = new QRcodeType((Event) getArguments().get(String.valueOf(R.string.eventBundle)));
        Toast.makeText(getContext(), event.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event_qrcode, container, false);

        setupViews(view);
        return view;
    }

    private void setupViews(View view) {
        btnGenerateQRcode = view.findViewById(R.id.generateQRcode);
        btnSendEmail = view.findViewById(R.id.sendEmailQR);
        btnDownloadQRcode = view.findViewById(R.id.downloadQR);
        btnSubmitQRcode = view.findViewById(R.id.submitQR);
        mQRcodeView = view.findViewById(R.id.QRcodeView);
        editTextMessageQRcode = view.findViewById(R.id.QRcode_message);
        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        btnSubmitQRcode.setOnClickListener(this);
        btnDownloadQRcode.setOnClickListener(this);
        btnSendEmail.setOnClickListener(this);
        btnGenerateQRcode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submitQR:
                submitQRcode();
                break;
            case R.id.generateQRcode:
                generateQRcode();
                break;
            case R.id.sendEmailQR:
                sendEmailQR();
                break;
            case R.id.downloadQR:
                downloadQR();
                break;
        }
    }

    private void checkMessage() {
        if (TextUtils.isEmpty(editTextMessageQRcode.getText().toString())) {
            editTextMessageQRcode.setError("Please enter message");
            editTextMessageQRcode.requestFocus();
            return;
        }
    }

    //TODO pobieranie na telefon pdf
    private void downloadQR() {
    }

    //TODO mail pdf
    private void sendEmailQR() {
    }


    private void generateQRcode() {
    }

    private void submitQRcode() {
        setQRcode();
        Bundle eventBundle = new Bundle();
        eventBundle.putSerializable(String.valueOf(R.string.eventBundle), event);
        navController.navigate(R.id.createEventMarker, eventBundle);
    }

    //TODO mail pdf zapisac arraybate sciezke ?
    private void setQRcode() {

    }
}
