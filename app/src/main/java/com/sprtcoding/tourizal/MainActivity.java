package com.sprtcoding.tourizal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sprtcoding.tourizal.Auth.Login;
import com.sprtcoding.tourizal.UserInformation.UserBasicInformation;
import com.sprtcoding.tourizal.Utility.NetworkChangeListener;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDb;
    private DatabaseReference userRef, userTokenRef;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseDatabase.getInstance();
        userRef = mDb.getReference("Users");
        userTokenRef = mDb.getReference("UserToken");

    }

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, intentFilter);
        super.onStart();

        FirebaseUser _user = mAuth.getCurrentUser();

        if(_user != null) {
            userRef.child(_user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {

                        FirebaseMessaging.getInstance().getToken()
                                .addOnCompleteListener(task1 -> {
                                    if (!task1.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Fetching FCM registration token failed :" + task1.getException(), Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    // Get new FCM registration token
                                    String token = task1.getResult();
                                    userTokenRef.child(mAuth.getCurrentUser().getUid()).child("token").setValue(token);
                                });

                        String _accountType = snapshot.child("AccountType").getValue(String.class);
                        if (_accountType.equals("User")) {
                            if(!snapshot.hasChild("Age") && !snapshot.hasChild("Gender") && !snapshot.hasChild("DateOfBirth")) {
                                Intent gotoUserBasicInfo = new Intent(MainActivity.this, UserBasicInformation.class);
                                startActivity(gotoUserBasicInfo);
                                finish();
                            }else {
                                Intent gotoUserDashboard = new Intent(MainActivity.this, UserDashBoard.class);
                                startActivity(gotoUserDashboard);
                                finish();
                            }
                        } else if (_accountType.equals("Admin")) {
                            if(!snapshot.hasChild("Age") && !snapshot.hasChild("Gender") && !snapshot.hasChild("DateOfBirth")) {
                                Intent gotoUserBasicInfo = new Intent(MainActivity.this, UserBasicInformation.class);
                                startActivity(gotoUserBasicInfo);
                                finish();
                            }else {
                                Intent gotoAdminDashboard = new Intent(MainActivity.this, AdminDashboardPage.class);
                                startActivity(gotoAdminDashboard);
                                finish();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Intent gotoLogin = new Intent(MainActivity.this, Login.class);
            startActivity(gotoLogin);
            finish();
        }

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}