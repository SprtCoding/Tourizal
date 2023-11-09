package com.sprtcoding.tourizal.Auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sprtcoding.tourizal.AdminDashboardPage;
import com.sprtcoding.tourizal.ForgotPassword.ForgotPassword;
import com.sprtcoding.tourizal.MainActivity;
import com.sprtcoding.tourizal.R;
import com.sprtcoding.tourizal.UserDashBoard;
import com.sprtcoding.tourizal.UserInformation.UserBasicInformation;
import com.sprtcoding.tourizal.Utility.NetworkChangeListener;

import java.util.HashMap;

public class Login extends AppCompatActivity {
    private TextView _signUpBtn, _forgotPasswordBtn;
    private TextInputEditText _emailET, _passET;
    private MaterialButton _loginBtn, _googleBtn;
    private String email, password;
    GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    FirebaseAuth mAuth;
    private FirebaseUser mUser;
    FirebaseDatabase mDB;
    private DatabaseReference userRef, userTokenRef;
    ProgressDialog loading;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        _var();

        loading = new ProgressDialog(this);
        loading.setTitle("Loading");
        loading.setMessage("Please wait...");

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mDB = FirebaseDatabase.getInstance();
        userRef = mDB.getReference("Users");
        userTokenRef = mDB.getReference("UserToken");

        _loginBtn.setOnClickListener(view -> {
            loading.show();
            Handler handler = new Handler();
            Runnable runnable = () -> {
                email = _emailET.getText().toString().trim();
                password = _passET.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    loading.dismiss();
                    Toast.makeText(this, "Email field can't empty!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(password)) {
                    loading.dismiss();
                    Toast.makeText(this, "Password field can't empty!", Toast.LENGTH_SHORT).show();
                } else {
                    LoginEmailPassword(email, password);
                }
            };
            handler.postDelayed(runnable, 2000);
        });

        _forgotPasswordBtn.setOnClickListener(view -> {
            loading.show();
            Handler handler = new Handler();
            Runnable runnable = () -> {
                loading.dismiss();
                Intent gotoSignUpPage = new Intent(this, ForgotPassword.class);
                startActivity(gotoSignUpPage);
            };
            handler.postDelayed(runnable, 2000);
        });

        //google sign in

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this, gso);

        _googleBtn.setOnClickListener(view -> {
            loading.show();
            SignInGoogle();
        });

        _signUpBtn.setOnClickListener(view -> {
            loading.show();
            Handler handler = new Handler();
            Runnable runnable = () -> {
                loading.dismiss();
                Intent gotoSignUp = new Intent(this, Signup.class);
                startActivity(gotoSignUp);
            };
            handler.postDelayed(runnable, 2000);
        });
    }

    private void SignInGoogle() {
        Intent googleIntent = gsc.getSignInIntent();
        startActivityForResult(googleIntent, 100);
    }

    private void LoginEmailPassword(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            userRef.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        if (snapshot.hasChild("AccountType")) {
                            String accountType = snapshot.child("AccountType").getValue(String.class);
                            if (accountType.equals("User")) {
                                FirebaseMessaging.getInstance().getToken()
                                        .addOnCompleteListener(task1 -> {
                                            if (!task1.isSuccessful()) {
                                                Toast.makeText(Login.this, "Fetching FCM registration token failed :" + task1.getException(), Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            // Get new FCM registration token
                                            String token = task1.getResult();
                                            userTokenRef.child(mAuth.getCurrentUser().getUid()).child("token").setValue(token);
                                        });

                                if(!snapshot.hasChild("Age") && !snapshot.hasChild("Gender") && !snapshot.hasChild("DateOfBirth")) {
                                    loading.dismiss();
                                    Intent gotoUserBasicInfo = new Intent(Login.this, UserBasicInformation.class);
                                    startActivity(gotoUserBasicInfo);
                                    finish();
                                }else {
                                    loading.dismiss();
                                    Intent gotoUserDashboard = new Intent(Login.this, UserDashBoard.class);
                                    startActivity(gotoUserDashboard);
                                    finish();
                                }
                            } else if (accountType.equals("Admin")) {
                                if(!snapshot.hasChild("Age") && !snapshot.hasChild("Gender") && !snapshot.hasChild("DateOfBirth")) {
                                    loading.dismiss();
                                    Intent gotoUserBasicInfo = new Intent(Login.this, UserBasicInformation.class);
                                    startActivity(gotoUserBasicInfo);
                                    finish();
                                }else {
                                    loading.dismiss();
                                    Intent gotoAdminDashboard = new Intent(Login.this, AdminDashboardPage.class);
                                    startActivity(gotoAdminDashboard);
                                    finish();
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    loading.dismiss();
                    Log.d("ERROR", error.getMessage());
                }
            });
        }).addOnFailureListener(e -> {
            loading.dismiss();
            Log.d("ERROR", e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void _var() {
        _signUpBtn = findViewById(R.id.signUpBtn);
        _emailET = findViewById(R.id.emailET);
        _passET = findViewById(R.id.passET);
        _loginBtn = findViewById(R.id.loginBtn);
        _googleBtn = findViewById(R.id.googleLoginBtn);
        _forgotPasswordBtn = findViewById(R.id.forgotPasswordBtn);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(task1 -> {
                            String picURL = null;
                            if(task1.isSuccessful()) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null) {
                                    for (UserInfo profile : user.getProviderData()) {
                                        picURL = String.valueOf(profile.getPhotoUrl());
                                    };
                                }
                                
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("AccountType", "User");
                                map.put("Fullname", account.getDisplayName());
                                map.put("Email", account.getEmail());
                                map.put("PhotoURL", picURL);
                                map.put("UID", FirebaseAuth.getInstance().getCurrentUser().getUid());

                                userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(map);

                                loading.dismiss();
                                Toast.makeText(Login.this, "Login successfully!", Toast.LENGTH_SHORT).show();
                                gotoUserDashboard();
                            } else {
                                loading.dismiss();
                                Toast.makeText(this, task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } catch (ApiException e) {
                loading.dismiss();
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void gotoUserDashboard() {
        userRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    if (snapshot.hasChild("AccountType")) {
                        String accountType = snapshot.child("AccountType").getValue(String.class);
                        if (accountType.equals("User")) {
                            if(!snapshot.hasChild("Age") && !snapshot.hasChild("Gender") && !snapshot.hasChild("DateOfBirth")) {
                                Intent gotoUserBasicInfo = new Intent(Login.this, UserBasicInformation.class);
                                startActivity(gotoUserBasicInfo);
                                finish();
                            }else {
                                Intent gotoUserDashboard = new Intent(Login.this, UserDashBoard.class);
                                startActivity(gotoUserDashboard);
                                finish();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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