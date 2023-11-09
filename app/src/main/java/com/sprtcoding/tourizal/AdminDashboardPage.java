package com.sprtcoding.tourizal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sprtcoding.tourizal.AdminMenu.AboutFragment;
import com.sprtcoding.tourizal.AdminMenu.HomeFragment;
import com.sprtcoding.tourizal.AdminMenu.ProfileFragment;
import com.sprtcoding.tourizal.AdminMenu.ReservationFragment;
import com.sprtcoding.tourizal.AdminMenu.ResortFragment;
import com.sprtcoding.tourizal.AdminMenu.ReviewsFragment;
import com.sprtcoding.tourizal.Auth.Login;
import com.sprtcoding.tourizal.Utility.NetworkChangeListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminDashboardPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef, userTokenRef;
    private TextView _ownerName, _ownerEmail;
    private CircleImageView _profilePic;
    public static int count = 0;
    public static TextView dots;
    private FirebaseFirestore DB;
    private CollectionReference reservationColRef;
    ProgressDialog _loading;
    NavigationView navigationView;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard_page);

        mAuth = FirebaseAuth.getInstance();

        DB = FirebaseFirestore.getInstance();
        reservationColRef = DB.collection("RESERVATION");

        FirebaseDatabase mDb = FirebaseDatabase.getInstance();
        userRef = mDb.getReference("Users");
        userTokenRef = mDb.getReference("UserToken");

        _loading = new ProgressDialog(this);
        _loading.setTitle("Signing Out");
        _loading.setMessage("Please wait...");

        Toolbar toolbar = findViewById(R.id.toolbar);
        getSupportActionBar();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        LayoutInflater li = LayoutInflater.from(this);
        dots = (TextView) li.inflate(R.layout.notification_dot, null);
        navigationView.getMenu().findItem(R.id.nav_reservation).setActionView(dots);

        reservationColRef.whereEqualTo("OWNER_UID", mAuth.getCurrentUser().getUid())
                        .whereEqualTo("READ", false).addSnapshotListener((value, error) -> {
                            if(error == null && value != null) {
                                if(!value.isEmpty()) {
                                    count = value.size();
                                    showNotificationDot(true, count);
                                } else {
                                    showNotificationDot(false, count);
                                }
                            } else {
                                Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                });

        updateHeader();

        checkNotificationPermission();
    }

    private void checkNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, start getting the location
            getTokenID();
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                    1);
        }
    }

    private void getTokenID() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task1 -> {
                    if (!task1.isSuccessful()) {
                        Toast.makeText(AdminDashboardPage.this, "Fetching FCM registration token failed :" + task1.getException(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Get new FCM registration token
                    String token = task1.getResult();
                    userTokenRef.child(mAuth.getCurrentUser().getUid()).child("token").setValue(token);
                });
    }

    private void updateHeader() {
        NavigationView navigationViewUpdate = findViewById(R.id.nav_view);
        View headerView = navigationViewUpdate.getHeaderView(0);
        _ownerName = headerView.findViewById(R.id.ownerName);
        _ownerEmail = headerView.findViewById(R.id.ownerEmail);
        _profilePic = headerView.findViewById(R.id.profilePic);

        userRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String name = snapshot.child("Fullname").getValue(String.class);
                    String email = snapshot.child("Email").getValue(String.class);
                    String photoURL = snapshot.child("PhotoURL").getValue(String.class);

                    _ownerName.setText(name);
                    _ownerEmail.setText(email);
                    try {
                        Picasso.get().load(photoURL).into(_profilePic);
                    }catch (Exception e) {
                        Picasso.get().load(R.drawable.default_profile).into(_profilePic);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminDashboardPage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                break;
            case R.id.nav_reservation:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ReservationFragment()).commit();
                break;
            case R.id.nav_reviews:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ReviewsFragment()).commit();
                break;
            case R.id.nav_about:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutFragment()).commit();
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                break;
            case R.id.nav_logout:
                _loading.show();
                Handler handler = new Handler();
                Runnable runnable = () -> {
                    _loading.dismiss();
                    mAuth.signOut();
                    Intent gotoLogin = new Intent(getApplicationContext(), Login.class);
                    startActivity(gotoLogin);
                    finish();
                    Toast.makeText(this, "Sign Out successfully!", Toast.LENGTH_SHORT).show();
                };
                handler.postDelayed(runnable, 2000);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
            finish();
        }
    }

    public static void showNotificationDot(boolean isNotified, int count) {
        if(isNotified) {
            dots.setText(String.valueOf(count));
        } else {
            dots.setVisibility(View.GONE);
        }
    }
}