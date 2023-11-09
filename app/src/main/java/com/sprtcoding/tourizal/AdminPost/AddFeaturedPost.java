package com.sprtcoding.tourizal.AdminPost;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sprtcoding.tourizal.FireStoreDB.DBQuery;
import com.sprtcoding.tourizal.FireStoreDB.MyCompleteListener;
import com.sprtcoding.tourizal.R;
import com.sprtcoding.tourizal.Utility.NetworkChangeListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AddFeaturedPost extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDb;
    private DatabaseReference featuredPostRef, userRef, userFeaturedRef;
    FirebaseFirestore DB;
    CollectionReference resortCollectionRef;
    private StorageReference featuredPhotoRef;
    private ImageView _thumbnailPhoto, backBtn;
    private TextView _openFile;
    private TextInputEditText _thumbnailTitleET, _resortNameET;
    private MaterialButton _getLocationBtn, _postBtn;
    private static final int PHOTO_IMAGE_REQUEST = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    public static FusedLocationProviderClient fusedLocationProviderClient;
    private static double lat = 0;
    private static double lon = 0;
    private static String API_KEY = "AIzaSyCzeq76gNROjEZeMJ003cd7A6rCgaD7QTc";
    List<Place.Field> fields;
    private Uri _imgURI;
    private String _ownerName, featuredPhotoID, featuredPhotoName;
    ProgressDialog _loading;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_featured_post);
        _var();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        DBQuery.g_firestore = FirebaseFirestore.getInstance();
        DB = FirebaseFirestore.getInstance();
        resortCollectionRef = DB.collection("RESORTS");

        _loading = new ProgressDialog(this);
        _loading.setCancelable(false);
        _loading.setTitle("Uploading");
        _loading.setMessage("Please wait...");

        // Initialize the SDK
        Places.initialize(getApplicationContext(), API_KEY);

        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);

        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseDatabase.getInstance();
        userRef = mDb.getReference("Users");
        featuredPhotoRef = FirebaseStorage.getInstance().getReference("FeaturedPhotos");

        FirebaseUser _user = mAuth.getCurrentUser();

        if(_user != null) {
            userRef.child(_user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        _ownerName = snapshot.child("Fullname").getValue(String.class);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(AddFeaturedPost.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        _postBtn.setOnClickListener(view -> {
            _loading.show();
            String title = _thumbnailTitleET.getText().toString();
            String name = _resortNameET.getText().toString();
            String location = _getLocationBtn.getText().toString();
            if(TextUtils.isEmpty(title)) {
                _loading.dismiss();
                Toast.makeText(this, "Thumbnail Title cannot be empty!", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(name)) {
                _loading.dismiss();
                Toast.makeText(this, "Resort name cannot be empty!", Toast.LENGTH_SHORT).show();
            }else if(location.equals("Location")) {
                _loading.dismiss();
                Toast.makeText(this, "Please set your location!", Toast.LENGTH_SHORT).show();
            }else {
                uploadFile();
            }
        });

        _openFile.setOnClickListener(view -> {
            openFileImage();
        });

        _getLocationBtn.setOnClickListener(view -> {

            // Start the autocomplete intent.
//            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
//                    .build(this);
//            startAutocomplete.launch(intent);

            checkLocationPermission();
        });

        backBtn.setOnClickListener(view -> {
            Intent i = new Intent(this, FeaturedPost.class);
            startActivity(i);
            finish();
        });
    }

    @SuppressLint("SetTextI18n")
    private final ActivityResultLauncher<Intent> startAutocomplete = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Check the requestCode to ensure it's from the Autocomplete activity
                    if (result.getData() != null && result.getData().getComponent() != null
                            && result.getData().getComponent().getClassName().equals(AutocompleteActivity.class.getName())) {
                        Place place = Autocomplete.getPlaceFromIntent(result.getData());
                        _getLocationBtn.setText(place.getName() + ", " + place.getAddress());
                        lat = place.getLatLng().latitude;
                        lon = place.getLatLng().longitude;
                        // Toast.makeText(this, "Place: ${place.getName()}, ${place.getId()}", Toast.LENGTH_SHORT).show();
                    }
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    // The user canceled the operation.
                    Toast.makeText(this, "User canceled autocomplete", Toast.LENGTH_SHORT).show();
                }
            });

    private void openFileImage() {
        Intent openFile = new Intent();
        openFile.setType("image/*");
        openFile.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(openFile, PHOTO_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadFile() {
        if(_imgURI != null) {
            // Add data to Firestore with an auto-generated document ID
            DocumentReference newDocRef = FirebaseFirestore.getInstance().collection("RESORTS").document();
            featuredPhotoID = newDocRef.getId();
            featuredPhotoName = String.valueOf(System.currentTimeMillis());
            StorageReference thumbnailRef = featuredPhotoRef.child(featuredPhotoID).child( featuredPhotoName + "." + getFileExtension(_imgURI));
            thumbnailRef.putFile(_imgURI).addOnCompleteListener(task -> {
                UploadTask uploadTask =thumbnailRef.putFile(_imgURI);

                uploadTask.continueWithTask((Continuation) task1 -> {

                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return thumbnailRef.getDownloadUrl();
                }).addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful()) {
                        Uri downloadURI = (Uri) task1.getResult();
                        assert downloadURI != null;
                        String myUri = downloadURI.toString();

                        uploadAll(myUri);
                    }
                });
            }).addOnFailureListener(e -> Toast.makeText(AddFeaturedPost.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        }else {
            Toast.makeText(this, "No file selected!", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadAll(String myUri) {
        String title = _thumbnailTitleET.getText().toString();
        String name = _resortNameET.getText().toString();
        String location = _getLocationBtn.getText().toString();
        // Add data to Firestore with an auto-generated document ID
        DocumentReference newDocRef = FirebaseFirestore.getInstance().collection("RESORTS").document();
        String thumbnailID = newDocRef.getId();
        Date dateTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String date = dateFormat.format(dateTime);
        String time = timeFormat.format(dateTime);

        resortCollectionRef.whereEqualTo("OWNER_UID", mAuth.getCurrentUser().getUid())
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if(!queryDocumentSnapshots.isEmpty()) {
                        for(DocumentSnapshot doc : queryDocumentSnapshots) {
                            String resort_id = doc.getString("RESORT_ID");

                            DBQuery.setFeaturedAll(
                                    mAuth.getCurrentUser().getUid(),
                                    resort_id,
                                    thumbnailID,
                                    title,
                                    featuredPhotoID,
                                    featuredPhotoName,
                                    name,
                                    _ownerName,
                                    location,
                                    String.valueOf(lat),
                                    String.valueOf(lon),
                                    time,
                                    date,
                                    myUri
                            );
                            DBQuery.setFeatured(
                                    mAuth.getCurrentUser().getUid(),
                                    resort_id,
                                    thumbnailID,
                                    title,
                                    featuredPhotoID,
                                    featuredPhotoName,
                                    name,
                                    _ownerName,
                                    location,
                                    String.valueOf(lat),
                                    String.valueOf(lon),
                                    time,
                                    date,
                                    myUri,
                                    new MyCompleteListener() {
                                        @Override
                                        public void onSuccess() {
                                            _loading.dismiss();
                                            Toast.makeText(AddFeaturedPost.this, "Successfully posted a thumbnail.", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(AddFeaturedPost.this, FeaturedPost.class);
                                            startActivity(i);
                                            finish();
                                        }

                                        @Override
                                        public void onFailure(Exception e) {
                                            _loading.dismiss();
                                            Toast.makeText(AddFeaturedPost.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                            );
                        }
                    } else {
                        Toast.makeText(this, "No Resort Found! Please add before adding featured post.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    _loading.dismiss();
                    Toast.makeText(AddFeaturedPost.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });

//        HashMap<String, Object> map = new HashMap<>();
//        map.put("UID", mAuth.getCurrentUser().getUid());
//        map.put("ThumbnailID", thumbnailID);
//        map.put("ThumbnailPhoto", myUri);
//        map.put("FeaturedTitle", title);
//        map.put("FeaturedPhotoID", featuredPhotoID);
//        map.put("FeaturedPhotoName", featuredPhotoName);
//        map.put("ResortName", name);
//        map.put("OwnerName", _ownerName);
//        map.put("Location", location);
//        map.put("Lat", String.valueOf(lat));
//        map.put("Lon", String.valueOf(lon));
//        map.put("TimePosted", time);
//        map.put("DatePosted", date);
//
//        assert thumbnailID != null;
//        userFeaturedRef.child(thumbnailID).setValue(map);
//        featuredPostRef.child(mAuth.getCurrentUser().getUid()).child(thumbnailID).setValue(map).addOnCompleteListener(task -> {
//            if(task.isSuccessful()) {
//                _loading.dismiss();
//                Toast.makeText(AddFeaturedPost.this, "Successfully posted a thumbnail.", Toast.LENGTH_SHORT).show();
//                Intent i = new Intent(this, FeaturedPost.class);
//                startActivity(i);
//                finish();
//            }else {
//                _loading.dismiss();
//                Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, start getting the location
            getLocation();
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
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
                        lat = location.getLatitude();
                        lon = location.getLongitude();

                        Geocoder geocoder = new Geocoder(AddFeaturedPost.this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                            if (addresses.size() > 0) {
                                Address address = addresses.get(0);
                                // Update the TextView with the location name
                                _getLocationBtn.setText(address.getAddressLine(0));
                            } else {
                                // No address found
                                _getLocationBtn.setText("No address found!");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // Location unavailable
                        _getLocationBtn.setText("Location unavailable");
                    }
                })
                .addOnFailureListener(this, e -> {
                    // Failed to get location
                    _getLocationBtn.setText("Failed to get location");
                });
    }

    private void _var() {
        _thumbnailPhoto = findViewById(R.id.thumbnailPhoto);
        _openFile = findViewById(R.id.openFile);
        _thumbnailTitleET = findViewById(R.id.thumbnailTitleET);
        _resortNameET = findViewById(R.id.resortNameET);
        _getLocationBtn = findViewById(R.id.getLocationBtn);
        _postBtn = findViewById(R.id.postBtn);
        backBtn = findViewById(R.id.backBtn);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PHOTO_IMAGE_REQUEST && resultCode == RESULT_OK
        && data != null && data.getData() != null) {
            _imgURI = data.getData();

            Picasso.get().load(_imgURI).into(_thumbnailPhoto);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted
                getLocation();
            } else {
                // Location permission denied
                Toast.makeText(this, "Location permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
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
        super.onBackPressed();
        Intent i = new Intent(this, FeaturedPost.class);
        startActivity(i);
        finish();
    }
}