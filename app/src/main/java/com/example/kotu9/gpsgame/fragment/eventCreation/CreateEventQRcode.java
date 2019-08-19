package com.example.kotu9.gpsgame.fragment.eventCreation;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

public class CreateEventQRcode extends Fragment implements View.OnClickListener {
    private static final String TAG = Activity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_CODE_WRITE_STORAGE = 112;


    private Button btnSubmitQRcode, btnGenerateQRcode, btnSendEmail, btnDownloadQRcode;
    private EditText editTextMessageQRcode;
    private ImageView mQRcodeView;
    private QRcodeType event;
    private NavController navController;
    private Bitmap bitmap;
    private FirebaseAuth mAuth;
    private QRGEncoder qrgEncoder;
    private String savePath;


    public CreateEventQRcode() {

    }

    public static CreateEventQRcode newInstance() {
        CreateEventQRcode fragment = new CreateEventQRcode();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    //TODO jak wraca zeby zostawal wygenerowany kod w imageView to samo w photo jeden task
    // sprawdzac czy istnieje plik i wczytywć znowu ?
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mAuth = FirebaseAuth.getInstance();
        savePath = "/" + getString(R.string.app_name) + "/QRCode/";
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
                checkMessage();
                generateQRcode();
                break;
            case R.id.sendEmailQR:
                sendEmailQR();
                break;
            case R.id.downloadQR:
                createMediaFile();
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
        return editTextMessageQRcode.getText().toString().trim();
    }

    private void downloadQR() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSIONS_CODE_WRITE_STORAGE
            );
        } else {
            boolean save;
            String result;
            try {
                save = QRGSaver.save(createMediaFile(), getQRcodeName(), bitmap, QRGContents.ImageType.IMAGE_PNG);
                result = save ? "QRcode Saved" : "QRcode Not Saved";
                Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }

    }

//    TODO zrobic tylko dla gmail jeśli email to nie gmail to zakryty i elo java mail api
//    https://stackoverflow.com/questions/16117365/sending-mail-attachment-using-java
    private void sendEmailQR() {
        Uri uri = null;
        uri = Uri.fromFile(saveBitmap(bitmap));
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("image/*");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{mAuth.getCurrentUser().getEmail()});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "QRcode From : " + getString(R.string.app_name) + " App");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello,\n" + "QRcode From : " + getString(R.string.app_name) + " App");
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(emailIntent, "Send mail..."));

    }


    private String generateMessage() {
        StringBuilder message = new StringBuilder();
        message.append("Message from ").append(event.name).append(" : ").append(getMessageFormEditText());
        return message.toString();
    }

    private void generateQRcode() {
        if (!editTextMessageQRcode.getText().toString().isEmpty()) {
            qrgEncoder = new QRGEncoder(generateMessage(), null, QRGContents.Type.TEXT, 400);
            try {
                bitmap = qrgEncoder.encodeAsBitmap();
                mQRcodeView.setImageBitmap(bitmap);
            } catch (WriterException e) {
                Log.v(TAG, e.toString());
            }
        }
    }

    private void submitQRcode() {
        setQRcode();
        Bundle eventBundle = new Bundle();
        eventBundle.putSerializable(String.valueOf(R.string.eventBundle), event);
        navController.navigate(R.id.createEventHints, eventBundle);
    }

    private void setQRcode() {
        event.fileName = getQRcodeName();
        event.imageDirectoryPhone = createMediaFile();
        event.imageURLfirebase = null;
    }

    private String createMediaFile() {
        String folderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + savePath;
        File mediaStorageDir = new File(folderPath);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return mediaStorageDir.getAbsolutePath();
            }
        }
        return folderPath;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_CODE_WRITE_STORAGE) {
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadQR();
            }
        }
    }

    private String getQRcodeName() {
        String fileName;
        fileName = "QR_" + event.name + "_" + event.hashCode();
        return fileName;
    }

    private File saveBitmap(Bitmap bmp) {

        OutputStream outStream = null;
        File file = new File(createMediaFile(), getQRcodeName() + ".png");
        if (file.exists()) {
            file.delete();
            file = new File(createMediaFile(), getQRcodeName() + ".png");
        }
        try {
            outStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }
}
