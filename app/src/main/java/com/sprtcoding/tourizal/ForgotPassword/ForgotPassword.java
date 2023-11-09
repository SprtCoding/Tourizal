package com.sprtcoding.tourizal.ForgotPassword;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.sprtcoding.tourizal.R;
import com.sprtcoding.tourizal.Utility.NetworkChangeListener;

public class ForgotPassword extends AppCompatActivity {
    private MaterialButton _resetBtn, _backBtn;
    private TextInputEditText _emailET;
    FirebaseAuth mAuth;
    String email;
    ProgressDialog loading;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        _var();

        loading = new ProgressDialog(this);
        loading.setTitle("Loading");
        loading.setMessage("Please wait...");

        mAuth = FirebaseAuth.getInstance();

        _resetBtn.setOnClickListener(view -> {
            loading.show();
            email = _emailET.getText().toString().trim();
            if(!TextUtils.isEmpty(email)) {
                ResetPassword();
            } else {
                _emailET.setError("Email field can't empty!");
                loading.dismiss();
            }
        });

        _backBtn.setOnClickListener(view -> {
            loading.show();
            Handler handler = new Handler();
            Runnable runnable = () -> {
                loading.dismiss();
                finish();
            };
            handler.postDelayed(runnable, 1500);
        });
    }

    @SuppressLint("SetTextI18n")
    private void ResetPassword() {
        _resetBtn.setVisibility(View.INVISIBLE);

        mAuth.sendPasswordResetEmail(email).addOnSuccessListener(unused -> {
            loading.dismiss();
            Toast.makeText(this, "Reset Password link has been sent to your registered Email.", Toast.LENGTH_LONG).show();
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error: - " + e.getMessage(), Toast.LENGTH_SHORT).show();
            loading.dismiss();
            _resetBtn.setVisibility(View.VISIBLE);
        });
    }

    private void _var() {
        _resetBtn = findViewById(R.id.resetBtn);
        _backBtn = findViewById(R.id.backBtn);
        _emailET = findViewById(R.id.emailET);
    }

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, intentFilter);
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}