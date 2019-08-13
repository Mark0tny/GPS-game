package com.example.kotu9.gpsgame.fragment.eventCreation;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

public class CreateEventQRcode extends Fragment implements View.OnClickListener {
    private static final String TAG = Activity.class.getSimpleName();

    private Button btnSubmitQRcode, btnGenerateQRcode, btnSendEmail, btnDownloadQRcode;
    private EditText editTextMessageQRcode;
    private ImageView mQRcodeView;
    private QRcodeType event;
    private NavController navController;
    private Bitmap bitmap;
    private FirebaseAuth mAuth;
    private QRGEncoder qrgEncoder;
    private String savePath = Environment.getExternalStorageDirectory().getPath() + "/QRCode/";

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

    private String getMessageFormEditText() {
        checkMessage();
        return editTextMessageQRcode.getText().toString().trim();
    }

    private void downloadQR(){
        try {
            QRGSaver.save(savePath, event.name, bitmap, QRGContents.ImageType.IMAGE_JPEG);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    //TODO mail https://stackoverflow.com/questions/6078099/android-intent-for-sending-email-with-attachment
    private void sendEmailQR() {
    }

    private String generateMessage() {
        StringBuilder message = null;
        message.append("Message from ").append(event.name).append(" : ").append(getMessageFormEditText());
        return message.toString();
    }

    private void generateQRcode() {

        qrgEncoder = new QRGEncoder(generateMessage(), null, QRGContents.Type.TEXT, 300);
        try {
            bitmap = qrgEncoder.encodeAsBitmap();
            mQRcodeView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Log.v(TAG, e.toString());
        }
    }

    private void submitQRcode() {
        setQRcode();
        Bundle eventBundle = new Bundle();
        eventBundle.putSerializable(String.valueOf(R.string.eventBundle), event);
        navController.navigate(R.id.createEventMarker, eventBundle);
    }

    private void setQRcode(){

    }
}
