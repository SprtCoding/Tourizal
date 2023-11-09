package com.sprtcoding.tourizal.StreetView;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;
import com.google.android.gms.maps.model.StreetViewSource;
import com.sprtcoding.tourizal.R;

public class ResortLocationStreetView extends AppCompatActivity implements OnStreetViewPanoramaReadyCallback {
    StreetViewPanorama streetViewPanorama;
    private String lat = null, lng = null;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resort_location_street_view);

        StreetViewPanoramaFragment streetViewPanoramaFragment = (StreetViewPanoramaFragment) getFragmentManager()
                .findFragmentById(R.id.googleMapStreetView);
        assert streetViewPanoramaFragment != null;
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);

        i = getIntent();

        lat = i.getStringExtra("Lat");
        lng = i.getStringExtra("Lng");
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        this.streetViewPanorama = streetViewPanorama;

        Log.d("ResortPanorama", lat +","+ lng);

        streetViewPanorama.setPosition(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)), StreetViewSource.OUTDOOR);

        streetViewPanorama.setStreetNamesEnabled(true);
        streetViewPanorama.setPanningGesturesEnabled(true);
        streetViewPanorama.setZoomGesturesEnabled(true);
        streetViewPanorama.setUserNavigationEnabled(true);
        streetViewPanorama.animateTo(
                new StreetViewPanoramaCamera.Builder().orientation(new StreetViewPanoramaOrientation(20,20))
                        .zoom(streetViewPanorama.getPanoramaCamera().zoom)
                        .build(),2000
        );
        streetViewPanorama.setOnStreetViewPanoramaCameraChangeListener(streetViewPanoramaCamera ->
                Toast.makeText(ResortLocationStreetView.this, "Location updated!", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}