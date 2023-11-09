package com.sprtcoding.tourizal.UserMenuFragment;

import android.Manifest;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sprtcoding.tourizal.R;
public class EmergencyFragment extends Fragment {
    private LinearLayout mdrrmo;
    private TextView mdrrmo_number;
    private static final int PHONE_CALL_PERMISSION_REQUEST_CODE = 100;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_emergency, container, false);
        _init();

        String mdrrmo_phone = mdrrmo_number.getText().toString();
        String finalMdrrmo_phone = mdrrmo_phone.replaceAll("[^0-9]", "");

        mdrrmo.setOnClickListener(view -> checkPhoneCallPermission(finalMdrrmo_phone));

        return v;
    }

    private void checkPhoneCallPermission(String number) {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            callNumber(number);
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.CALL_PHONE},
                    PHONE_CALL_PERMISSION_REQUEST_CODE);
        }
    }

    private void callNumber(String number) {
        String my_number = "tel:" + number;
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(my_number));
        startActivity(intent);
    }

    private void _init() {
        mdrrmo = v.findViewById(R.id.mdrrmo);
        mdrrmo_number = v.findViewById(R.id.mdrrmo_number);
    }
}