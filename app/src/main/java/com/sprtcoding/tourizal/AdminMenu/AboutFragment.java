package com.sprtcoding.tourizal.AdminMenu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sprtcoding.tourizal.R;
public class AboutFragment extends Fragment {
    private FloatingActionButton facebookFab, gmailFab, instagramFab, phoneCallFab;
    private static final int PHONE_CALL_PERMISSION_REQUEST_CODE = 100;
    View aboutView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        aboutView = inflater.inflate(R.layout.fragment_about, container, false);

        _var();

        phoneCallFab.setOnClickListener(view -> {
            checkPhoneCallPermission();
        });

        facebookFab.setOnClickListener(view -> {
            gotoUrl("https://www.facebook.com/rodel.alipuso.29?mibextid=ZbWKwL");
        });

        gmailFab.setOnClickListener(view -> {
            String email = "rodel.alipuso01@gmail.com";
            String subject = "Feedback to Application";
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setData(Uri.parse("mailto:"));
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_EMAIL, new String[] {email});
            i.putExtra(Intent.EXTRA_SUBJECT, subject);
            i.putExtra(Intent.EXTRA_TEXT, "");

            try {
                startActivity(Intent.createChooser(i, "Choose an Email Client"));
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        instagramFab.setOnClickListener(view -> {
            gotoUrl("https://instagram.com/breathlessplays?utm_source=qr&igshid=MzNlNGNkZWQ4Mg%3D%3D");
        });

        return aboutView;
    }

    private void checkPhoneCallPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, start getting the location
            callNumber();
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.CALL_PHONE},
                    PHONE_CALL_PERMISSION_REQUEST_CODE);
        }
    }

    private void callNumber() {
        String number = "09073341838";
        String my_number = "tel:" + number;
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(my_number));
        startActivity(intent);
    }

    private void gotoUrl(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    private void _var() {
        facebookFab = aboutView.findViewById(R.id.facebookFab);
        gmailFab = aboutView.findViewById(R.id.gmailFab);
        instagramFab = aboutView.findViewById(R.id.instagramFab);
        phoneCallFab = aboutView.findViewById(R.id.phoneCallFab);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PHONE_CALL_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted
                callNumber();
            } else {
                // Location permission denied
                Toast.makeText(getContext(), "Phone Call permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}