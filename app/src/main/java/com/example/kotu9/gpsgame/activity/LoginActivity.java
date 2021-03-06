package com.example.kotu9.gpsgame.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.kotu9.gpsgame.activity.administration.AdminDashboardActivity;
import com.example.kotu9.gpsgame.model.User;
import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.utils.UserClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import permission.auron.com.marshmallowpermissionhelper.ActivityManagePermission;

import static com.example.kotu9.gpsgame.utils.Constants.USER_ROLE_ADMIN;

public class LoginActivity extends ActivityManagePermission implements View.OnClickListener {
    private EditText editTextEmail, editTextPassword;
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(R.string.ac_login);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupFirebaseAuth();
        findViewById(R.id.textViewSignup).setOnClickListener(this);
        findViewById(R.id.buttonLogin).setOnClickListener(this);

        editTextEmail = findViewById(R.id.editTextEmailL);
        editTextPassword = findViewById(R.id.editTextPasswordL);
        progressBar = findViewById(R.id.progressbar);

        hideSoftKeyboard();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textViewSignup:
                finish();
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.buttonLogin:
                userLogin();
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    private void setupFirebaseAuth() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser CurUser = firebaseAuth.getCurrentUser();
                if (CurUser != null) {
                    Toast.makeText(LoginActivity.this, "Authenticated with: " + CurUser.getEmail(), Toast.LENGTH_SHORT).show();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                            .build();
                    db.setFirestoreSettings(settings);

                    DocumentReference userRef = db.collection(getString(R.string.collection_users))
                            .document(CurUser.getUid());

                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                User user = task.getResult().toObject(User.class);
                                ((UserClient)(getApplicationContext())).setUser(user);
                                checkUserRole(user);
                            }
                        }
                    });

                }
            }
        };
    }

    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        loginFieldsCheck(email, password);
        
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            User user = ((UserClient)(getApplicationContext())).getUser();
                            if(user != null){
                                checkUserRole(user);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    private void checkUserRole(User user) {

        if(user.role.equalsIgnoreCase(USER_ROLE_ADMIN)){
            showAdminUI();
        }
        else{
            showUserUI();
        }
    }

    private  void showUserUI() {
        finish();
        Intent intent = new Intent(getApplicationContext(), UserLocationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private  void showAdminUI() {
        finish();
        Intent intent = new Intent(getApplicationContext(), AdminDashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void loginFieldsCheck(String email, String password) {

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
    }
}
