package com.sprtcoding.tourizal.Auth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sprtcoding.tourizal.R;
import com.sprtcoding.tourizal.Utility.NetworkChangeListener;

import java.util.HashMap;

public class Signup extends AppCompatActivity {
    private TextView _logInBtn;
    ProgressDialog loading;
    private MaterialButton _signUpBtn;
    private CheckBox _isResortOwner;
    private boolean isOwner = false;
    private TextInputEditText _firstNameET, _lastNameET, _emailET, _passET;
    FirebaseAuth mAuth;
    private FirebaseUser mUser;
    FirebaseDatabase mDB;
    private DatabaseReference userRef;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        _var();

        loading = new ProgressDialog(this);
        loading.setTitle("Loading");
        loading.setMessage("Please wait...");

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mDB = FirebaseDatabase.getInstance();
        userRef = mDB.getReference("Users");

        _isResortOwner.setOnCheckedChangeListener((compoundButton, b) -> {
            if(compoundButton.isChecked()) {
                isOwner = true;
            }
        });

        _signUpBtn.setOnClickListener(view -> {
            loading.show();
            String fName = _firstNameET.getText().toString();
            String lName = _lastNameET.getText().toString();
            String email = _emailET.getText().toString();
            String pass = _passET.getText().toString();
            String accountType = "User";

            if(TextUtils.isEmpty(fName)) {
                loading.dismiss();
                _firstNameET.setError("First Name cannot be empty!");
            }else if(TextUtils.isEmpty(lName)) {
                loading.dismiss();
                _lastNameET.setError("Last Name cannot be empty!");
            }else if(TextUtils.isEmpty(email)) {
                loading.dismiss();
                _emailET.setError("Email cannot be empty!");
            }else if(TextUtils.isEmpty(pass)) {
                loading.dismiss();
                _passET.setError("Password cannot be empty!");
            }else {
                CreateEmailPassword(fName, lName, email, pass, accountType);
            }
        });

        _logInBtn.setOnClickListener(view -> {
            loading.show();
            Handler handler = new Handler();
            Runnable runnable = () -> {
                loading.dismiss();
                Intent gotoLogin = new Intent(this, Login.class);
                startActivity(gotoLogin);
            };
            handler.postDelayed(runnable, 2000);
        });
    }

    private void CreateEmailPassword(String fName, String lName, String email, String pass, String accountType) {
        if(isOwner) {
            mAuth.createUserWithEmailAndPassword(email, pass).addOnSuccessListener(authResult -> {
                HashMap<String, Object> map = new HashMap<>();
                map.put("Fullname", fName + " " + lName);
                map.put("Email", email);
                map.put("AccountType", "Admin");
                map.put("UID", FirebaseAuth.getInstance().getCurrentUser().getUid());

                userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(map).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        loading.dismiss();
                        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                        Intent gotoLoginPage = new Intent(Signup.this, Login.class);
                        startActivity(gotoLoginPage);
                        finish();
                    }
                });

            }).addOnFailureListener(e -> {
                loading.dismiss();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }else {
            mAuth.createUserWithEmailAndPassword(email, pass).addOnSuccessListener(authResult -> {
                HashMap<String, Object> map = new HashMap<>();
                map.put("Fullname", fName + " " + lName);
                map.put("Email", email);
                map.put("AccountType", accountType);
                map.put("UID", FirebaseAuth.getInstance().getCurrentUser().getUid());

                userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(map).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        loading.dismiss();
                        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                        Intent gotoLoginPage = new Intent(Signup.this, Login.class);
                        startActivity(gotoLoginPage);
                        finish();
                    }
                });

            }).addOnFailureListener(e -> {
                loading.dismiss();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void _var() {
        _logInBtn = findViewById(R.id.logInBtn);
        _signUpBtn = findViewById(R.id.signUpBtn);
        _firstNameET = findViewById(R.id.firstNameET);
        _lastNameET = findViewById(R.id.lastNameET);
        _emailET = findViewById(R.id.emailET);
        _passET = findViewById(R.id.passET);
        _isResortOwner = findViewById(R.id.isResortOwner);
    }

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, intentFilter);
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}