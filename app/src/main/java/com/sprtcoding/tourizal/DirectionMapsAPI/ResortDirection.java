package com.sprtcoding.tourizal.DirectionMapsAPI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.sprtcoding.tourizal.AdminPost.AddFeaturedPost;
import com.sprtcoding.tourizal.R;
import com.sprtcoding.tourizal.UsersViewPost.UserResortInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ResortDirection extends FragmentActivity implements OnMapReadyCallback {
    GoogleMap gMap;
    FrameLayout map;
    private ImageView backBtn, menuBtn;
    private AutoCompleteTextView mapTypeCTV;
    private TextView closeMapTypeDialog;
    private MaterialButton okBtn;
    private FloatingActionButton startNavigation;
    private static double currentLocationLat = 0;
    private static double currentLocationLon = 0;
    private static String yourCurrentLocation = null, resortLocationName = null;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    public static FusedLocationProviderClient fusedLocationProviderClient;
    private String resortLat, resortLng;
    private static String API_KEY = "AIzaSyANpn7LHul20RBsI-uvZzinLj5JtIiiY5g";
    LocationRequest locationRequest;
    Marker userLocationMarker;
    Circle userLocationAccuracyCircle;
    boolean isStarted = false;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resort_direction);
        map = findViewById(R.id.map);
        backBtn = findViewById(R.id.backBtn);
        menuBtn = findViewById(R.id.menuBtn);
        startNavigation = findViewById(R.id.startNavigation);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        i = getIntent();

        resortLat = i.getStringExtra("Lat");
        resortLng = i.getStringExtra("Lon");
        resortLocationName = i.getStringExtra("LocationName");

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        backBtn.setOnClickListener(view -> {
            finish();
        });

        startNavigation.setOnClickListener(view -> {
            //startLocationUpdates();
            Intent i = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("google.navigation:q="+resortLat + "," + resortLng+"&mode=d"));
            i.setPackage("com.google.android.apps.maps");

            startActivity(i);

        });

        viewMapTypeDialog();

    }

    @SuppressLint("MissingInflatedId")
    private void viewMapTypeDialog() {
        View mapTypeAlertDialog = LayoutInflater.from(ResortDirection.this).inflate(R.layout.map_style_dialog, null);
        AlertDialog.Builder mapTypeAlertDialogBuilder = new AlertDialog.Builder(ResortDirection.this);

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

        String[] mapType = new String[]{
                "Normal Map",
                "Hybrid Map",
                "Satellite Map",
                "Terrain Map"
        };

        ArrayAdapter<String> mapTypeAdapter = new ArrayAdapter<>(
                this,
                R.layout.gender_dropdown_item,
                mapType
        );

        mapTypeCTV.setAdapter(mapTypeAdapter);

        menuBtn.setOnClickListener(view -> {
            mapTypeDialog.show();
        });

        okBtn.setOnClickListener(view -> {
            if (!mapTypeCTV.getText().toString().equals("") || TextUtils.isEmpty(mapTypeCTV.getText().toString())) {
                if (mapTypeCTV.getText().toString().equals("Normal Map")) {
                    gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mapTypeDialog.dismiss();
                } else if (mapTypeCTV.getText().toString().equals("Hybrid Map")) {
                    gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    mapTypeDialog.dismiss();
                } else if (mapTypeCTV.getText().toString().equals("Satellite Map")) {
                    gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    mapTypeDialog.dismiss();
                } else if (mapTypeCTV.getText().toString().equals("Terrain Map")) {
                    gMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    mapTypeDialog.dismiss();
                }
            } else {
                Toast.makeText(this, "Please select map type!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.gMap = googleMap;
        checkLocationPermission();
    }

    private void getLocationDirection() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Uri.parse("https://maps.googleapis.com/maps/api/directions/json")
                .buildUpon()
                .appendQueryParameter("destination", resortLat + ", " + resortLng)
                .appendQueryParameter("origin", currentLocationLat + ", " + currentLocationLon)
                .appendQueryParameter("mode", "driving")
                .appendQueryParameter("key", API_KEY)
                .toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                String status = response.getString("status");
                if (status.equals("OK")) {
                    JSONArray routes = response.getJSONArray("routes");

                    ArrayList<LatLng> points;
                    PolylineOptions polylineOptions = null;

                    for (int i = 0; i < routes.length(); i++) {
                        points = new ArrayList<>();
                        polylineOptions = new PolylineOptions();
                        JSONArray legs = routes.getJSONObject(i).getJSONArray("legs");

                        for (int j = 0; j < legs.length(); j++) {
                            JSONArray steps = legs.getJSONObject(i).getJSONArray("steps");

                            for (int k = 0; k < steps.length(); k++) {
                                String polyline = steps.getJSONObject(k).getJSONObject("polyline").getString("points");
                                List<LatLng> list = decodePoly(polyline);

                                for (int l = 0; l < list.size(); l++) {
                                    LatLng position = new LatLng((list.get(l)).latitude, (list.get(l)).longitude);
                                    points.add(position);
                                }
                            }
                        }
                        polylineOptions.addAll(points);
                        polylineOptions.width(10);
                        polylineOptions.color(ContextCompat.getColor(ResortDirection.this, R.color.light_blue));
                        polylineOptions.geodesic(true);
                    }
                    float zoomLevel = 10.0f;

                    gMap.addPolyline(polylineOptions);
                    userLocationMarker = gMap.addMarker(new MarkerOptions().position(new LatLng(currentLocationLat, currentLocationLon)).title(yourCurrentLocation).icon(bitmapDescriptor(this, R.drawable.car)));
                    gMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(resortLat), Double.parseDouble(resortLng))).title(resortLocationName).icon(bitmapDescriptor(this, R.drawable.marker)));

                    LatLngBounds bounds = new LatLngBounds.Builder()
                            .include(new LatLng(currentLocationLat, currentLocationLon))
                            .include(new LatLng(Double.parseDouble(resortLat), Double.parseDouble(resortLng)))
                            .build();

                    Point point = new Point();
                    getWindowManager().getDefaultDisplay().getSize(point);
                    gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, point.x, 150, 30));
                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocationLat, currentLocationLon), zoomLevel));
                }
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, error -> Toast.makeText(ResortDirection.this, error.getMessage(), Toast.LENGTH_SHORT).show());

        RetryPolicy retryPolicy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(jsonObjectRequest);
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();

        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;

            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)), (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, start getting the location
            getLocation();

            Handler handler = new Handler();
            Runnable runnable = this::getLocationDirection;
            handler.postDelayed(runnable, 3000);
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private BitmapDescriptor bitmapDescriptor(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @SuppressLint("SetTextI18n")
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        // Location retrieved successfully
                        currentLocationLat = location.getLatitude();
                        currentLocationLon = location.getLongitude();

                        Geocoder geocoder = new Geocoder(ResortDirection.this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(currentLocationLat, currentLocationLon, 1);
                            if (addresses.size() > 0) {
                                Address address = addresses.get(0);
                                // Update the TextView with the location name
                                yourCurrentLocation = address.getAddressLine(0);
                            } else {
                                // No address found
                                Toast.makeText(this, "No Address found!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // Location unavailable
                        Toast.makeText(this, "Location not found!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(this, e -> {
                    // Failed to get location
                    Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show();
                });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted
                getLocation();
                enableUserLocation();
                zoomToUserLocation();
            } else {
                // Location permission denied
                Toast.makeText(this, "Location permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Log.d("ResortDirection", "onLocationResult: " + locationResult.getLastLocation());
            if (gMap != null) {
                setUserLocationMarker(locationResult.getLastLocation());
            }
        }
    };

    private void setUserLocationMarker(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (userLocationMarker == null) {
            //Create a new marker
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bike_top));
            markerOptions.rotation(location.getBearing());
            markerOptions.anchor((float) 0.5, (float) 0.5);
            userLocationMarker = gMap.addMarker(markerOptions);
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        } else {
            //use the previously created marker
            userLocationMarker.setPosition(latLng);
            userLocationMarker.setRotation(location.getBearing());
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        }

        if (userLocationAccuracyCircle == null) {
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(latLng);
            circleOptions.strokeWidth(4);
            circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
            circleOptions.fillColor(Color.argb(32, 255, 0, 0));
            circleOptions.radius(location.getAccuracy());
            userLocationAccuracyCircle = gMap.addCircle(circleOptions);
        } else {
            userLocationAccuracyCircle.setCenter(latLng);
            userLocationAccuracyCircle.setRadius(location.getAccuracy());
        }
    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        gMap.setMyLocationEnabled(true);
    }

    private void zoomToUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
//                mMap.addMarker(new MarkerOptions().position(latLng));
            }
        });
    }


//    private void startLocationUpdates() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
//    }
//
//    private void stopLocationUpdates() {
//        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
//    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

}