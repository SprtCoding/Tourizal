package com.sprtcoding.tourizal.UserMenuFragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sprtcoding.tourizal.DirectionMapsAPI.ResortDirection;
import com.sprtcoding.tourizal.Model.ResortsModel;
import com.sprtcoding.tourizal.R;
import com.sprtcoding.tourizal.StreetView.KuulaStreetView;
import com.sprtcoding.tourizal.StreetView.ResortLocationStreetView;

import java.util.ArrayList;
import java.util.List;

public class UserLocatorFragment extends Fragment implements OnMapReadyCallback {
    GoogleMap gMap;
    FrameLayout map;
    SupportMapFragment mapFragment;
    Marker marker;
    MarkerOptions markerOptions;
    DatabaseReference resortRef;
    private FloatingActionButton mapTypeFab, navigate;
    private AutoCompleteTextView mapTypeCTV;
    private TextView closeMapTypeDialog;
    private MaterialButton okBtn;
    private FirebaseFirestore db;
    private CollectionReference ResortColRef;
    //private static String API_KEY = "AIzaSyANpn7LHul20RBsI-uvZzinLj5JtIiiY5g";
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_locator, container, false);

        map = view.findViewById(R.id.map);

        navigate = view.findViewById(R.id.navigate);

        mapTypeFab = view.findViewById(R.id.mapTypeFab);

        //fs
        db = FirebaseFirestore.getInstance();
        ResortColRef = db.collection("RESORTS");

        resortRef = FirebaseDatabase.getInstance().getReference("UsersResorts");

        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        viewMapTypeDialog();

        return view;
    }

    private BitmapDescriptor bitmapDescriptor(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0,0,vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void viewMapTypeDialog() {
        View mapTypeAlertDialog = LayoutInflater.from(getContext()).inflate(R.layout.map_style_dialog, null);
        AlertDialog.Builder mapTypeAlertDialogBuilder = new AlertDialog.Builder(getContext());

        mapTypeAlertDialogBuilder.setView(mapTypeAlertDialog);

        mapTypeCTV = mapTypeAlertDialog.findViewById(R.id.mapTypeCTV);
        closeMapTypeDialog = mapTypeAlertDialog.findViewById(R.id.closeMapTypeDialog);
        okBtn = mapTypeAlertDialog.findViewById(R.id.okBtn);

        final AlertDialog mapTypeDialog = mapTypeAlertDialogBuilder.create();

        mapTypeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mapTypeDialog.setCanceledOnTouchOutside(false);

        closeMapTypeDialog.setOnClickListener(view -> {
            mapTypeDialog.cancel();
        });

        String[] mapType = new String[] {
                "Normal Map",
                "Hybrid Map",
                "Satellite Map",
                "Terrain Map"
        };

        ArrayAdapter<String> mapTypeAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.gender_dropdown_item,
                mapType
        );

        mapTypeCTV.setAdapter(mapTypeAdapter);

        mapTypeFab.setOnClickListener(view -> {
            mapTypeDialog.show();
        });

        okBtn.setOnClickListener(view -> {
            if(!mapTypeCTV.getText().toString().equals("") || TextUtils.isEmpty(mapTypeCTV.getText().toString())) {
                if(mapTypeCTV.getText().toString().equals("Normal Map")) {
                    gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mapTypeDialog.dismiss();
                }else if(mapTypeCTV.getText().toString().equals("Hybrid Map")) {
                    gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    mapTypeDialog.dismiss();
                }else if(mapTypeCTV.getText().toString().equals("Satellite Map")) {
                    gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    mapTypeDialog.dismiss();
                }else if(mapTypeCTV.getText().toString().equals("Terrain Map")) {
                    gMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    mapTypeDialog.dismiss();
                }
            }else {
                Toast.makeText(getContext(), "Please select map type!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.gMap = googleMap;

        ResortColRef.addSnapshotListener((value, error) -> {
            if(error == null && value != null) {
                if(!value.isEmpty()) {
                    for(QueryDocumentSnapshot doc : value) {
                        String resortLocationName = doc.getString("LOCATION");
                        float zoomLevel = 11.0f;
                        LatLng latLng = new LatLng(doc.getDouble("LAT"), doc.getDouble("LNG"));
                        markerOptions = new MarkerOptions();
                        markerOptions.position(latLng).title(resortLocationName)
                                .icon(bitmapDescriptor(getContext(), R.drawable.marker));
                        marker = gMap.addMarker(markerOptions);
                        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng , zoomLevel));
                        gMap.setOnMarkerClickListener(marker -> {
                            LatLng clickedPosition = marker.getPosition();
                            double lat = clickedPosition.latitude;
                            double lng = clickedPosition.longitude;

                            navigate.setVisibility(View.VISIBLE);
                            navigate.setOnClickListener(view1 -> {
                                Intent i = new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("google.navigation:q="+lat + "," + lng+"&mode=d"));
                                i.setPackage("com.google.android.apps.maps");

                                startActivity(i);
                            });

                            return false;
                        });
                    }
                }
            }else {
                Toast.makeText(getContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}