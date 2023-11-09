package com.sprtcoding.tourizal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sprtcoding.tourizal.AdminMenu.AboutFragment;
import com.sprtcoding.tourizal.UserMenuFragment.EmergencyFragment;
import com.sprtcoding.tourizal.UserMenuFragment.UserHomeFragment;
import com.sprtcoding.tourizal.UserMenuFragment.UserLocatorFragment;
import com.sprtcoding.tourizal.Utility.NetworkChangeListener;

public class UserDashBoard extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dash_board);

        replaceFragment(new UserHomeFragment());

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottom_home:
                    replaceFragment(new UserHomeFragment());
                    break;
                case R.id.bottom_locator:
                    replaceFragment(new UserLocatorFragment());
                    break;
                case R.id.bottom_emergency:
                    replaceFragment(new EmergencyFragment());
                    break;
                case R.id.bottom_about:
                    replaceFragment(new AboutFragment());
                    break;
            }
            return true;
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
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
}