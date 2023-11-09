package com.sprtcoding.tourizal.UserMenuFragment.UserOtherOptions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sprtcoding.tourizal.Auth.Login;
import com.sprtcoding.tourizal.R;
import com.sprtcoding.tourizal.Utility.NetworkChangeListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
    private CircleImageView userPic;
    private TextView name, email, bdate, age, gender, back_btn;
    private MaterialButton edit_btn, logout_btn;
    DatabaseReference userRef;
    FirebaseDatabase db;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    GoogleApiClient mGoogleApiClient;
    ProgressDialog loading;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        _init();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

        loading = new ProgressDialog(this);
        loading.setTitle("Sign Out");
        loading.setMessage("Please wait...");

        mUser = mAuth.getCurrentUser();
        userRef = db.getReference("Users");

        if(mUser != null) {
            userRef.child(mUser.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                String _name = snapshot.child("Fullname").getValue(String.class);
                                String _email = snapshot.child("Email").getValue(String.class);
                                String _bdate = snapshot.child("DateOfBirth").getValue(String.class);
                                String _age = snapshot.child("Age").getValue(String.class);
                                String _gender = snapshot.child("Gender").getValue(String.class);
                                String _picUrl = snapshot.child("PhotoURL").getValue(String.class);

                                Picasso.get().load(_picUrl).placeholder(R.drawable.default_profile).fit().into(userPic);
                                name.setText(_name);
                                email.setText(_email);
                                bdate.setText(_bdate);
                                age.setText(_age);
                                gender.setText(_gender);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(Profile.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        // Initialize GoogleSignInOptions
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with the options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(Profile.this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        logout_btn.setOnClickListener(view -> {
            loading.show();
            if (mGoogleApiClient.isConnected()) {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        status -> {
                            mAuth.signOut();
                            loading.dismiss();
                            Intent gotoLogin = new Intent(Profile.this, Login.class);
                            startActivity(gotoLogin);
                            finish();
                        });
            } else {
                // Handle the case when the GoogleApiClient is not yet connected
                // You may want to display a message or take other appropriate action
                Toast.makeText(Profile.this, "Something went wrong! Try Again.", Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }
        });

        back_btn.setOnClickListener(view -> finish());

    }

    private void _init() {
        back_btn = findViewById(R.id.back_btn);
        userPic = findViewById(R.id.user_pic);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        bdate = findViewById(R.id.bdate);
        age = findViewById(R.id.age);
        gender = findViewById(R.id.gender);
        edit_btn = findViewById(R.id.edit_btn);
        logout_btn = findViewById(R.id.logout_btn);
    }

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, intentFilter);
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

    // Connect the GoogleApiClient in onResume or another appropriate lifecycle method
    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    // Disconnect the GoogleApiClient in onPause or another appropriate lifecycle method
    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
}