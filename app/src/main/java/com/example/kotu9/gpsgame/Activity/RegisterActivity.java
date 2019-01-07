package com.example.kotu9.gpsgame.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.kotu9.gpsgame.Model.User;
import com.example.kotu9.gpsgame.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.NonNull;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editTextEmail, editTextUsername, editTextPassword;
    ProgressBar progressBar;
    DatabaseReference reference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findViewById(R.id.buttonSignUp).setOnClickListener(this);
        findViewById(R.id.textViewLogin).setOnClickListener(this);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        progressBar = findViewById(R.id.progressbar);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("users");

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textViewLogin:
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.buttonSignUp:
                registerUser();
                break;
        }
    }
    private void registerUser() {
        final String email = editTextEmail.getText().toString().trim();
        final String username = editTextUsername.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Please enter email");
            editTextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter valid email");
            editTextEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(username)) {
            editTextUsername.setError("Please enter username");
            editTextUsername.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Please enter password");
            editTextPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            editTextPassword.setError("Minimum lenght of password should be 6");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            User user = new User(username,email,password,null,mAuth.getCurrentUser().getUid(),0,getCurrentDate(),0.0,0.0,null);
                            reference.child(mAuth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {

                                @Override
                                public void onComplete(@android.support.annotation.NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progressBar.setVisibility(View.GONE);
                                        finish();
                                        startActivity(new Intent(getApplicationContext(),UserProfileActivity.class));
                                    }else{
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(getApplicationContext(), "Could not register user",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                progressBar.setVisibility(View.VISIBLE);
                                Toast.makeText(getApplicationContext(), "User already exist",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                progressBar.setVisibility(View.VISIBLE);
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });
    }

    public Date getCurrentDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date currDate = new Date();
        return currDate;
    }
}
