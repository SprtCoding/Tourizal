package com.sprtcoding.tourizal.AdminPost.ResortPost;

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
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sprtcoding.tourizal.AdminPost.AddFeaturedPost;
import com.sprtcoding.tourizal.AdminPost.FeaturedPost;
import com.sprtcoding.tourizal.FireStoreDB.DBQuery;
import com.sprtcoding.tourizal.FireStoreDB.MyCompleteListener;
import com.sprtcoding.tourizal.R;
import com.sprtcoding.tourizal.Utility.NetworkChangeListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddResortPage extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser _user;
    private StorageReference resortPhotoRef;
    private FirebaseFirestore db;
    private CollectionReference resortCol;
    private ImageView _resortPhoto, backBtn;
    private TextView _openFile, _title;
    private TextInputEditText _ownerET, _resortNameET, _entranceET;
    private MaterialButton _getLocationBtn, _postBtn;
    private static final int PHOTO_IMAGE_REQUEST = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    public static FusedLocationProviderClient fusedLocationProviderClient;
    private String resortPicName;
    private static double lat = 0;
    private static double lon = 0;
    private static String API_KEY = "AIzaSyCzeq76gNROjEZeMJ003cd7A6rCgaD7QTc";
    List<Place.Field> fields;
    private Uri _imgURI;
    private boolean isUpdate;
    private String resortPic, resortName, resortOwner, resortLocation, resortEntrance, picName, resortID;
    ProgressDialog _loading;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_resort_page);
        _var();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        _loading = new ProgressDialog(this);
        _loading.setCancelable(false);
        _loading.setTitle("Uploading");
        _loading.setMessage("Please wait...");

        DBQuery.g_firestore = FirebaseFirestore.getInstance();
        db = FirebaseFirestore.getInstance();

        resortCol = db.collection("RESORTS");

        // Initialize the SDK
        Places.initialize(getApplicationContext(), API_KEY);

        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);

        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

        mAuth = FirebaseAuth.getInstance();
        _user = mAuth.getCurrentUser();

        resortPhotoRef = FirebaseStorage.getInstance().getReference("ResortPhotos");

        if(getIntent() != null) {
            isUpdate = getIntent().getBooleanExtra("isUpdate", false);
            resortPic = getIntent().getStringExtra("resortPic");
            resortName = getIntent().getStringExtra("resortName");
            resortOwner = getIntent().getStringExtra("resortOwner");
            resortLocation = getIntent().getStringExtra("resortLocation");
            resortEntrance = getIntent().getStringExtra("resortEntrance");
            picName = getIntent().getStringExtra("resortPicName");
            resortID = getIntent().getStringExtra("resortID");
            if(isUpdate) {
                _title.setText("Update Resort");
                Picasso.get().load(resortPic).placeholder(R.drawable.resort).into(_resortPhoto);
                _resortNameET.setText(resortName);
                _ownerET.setText(resortOwner);
                _getLocationBtn.setText(resortLocation);
                _entranceET.setText(resortEntrance);
            }
        }

        _postBtn.setOnClickListener(view -> {
            _loading.show();
            String name = _resortNameET.getText().toString();
            String location = _getLocationBtn.getText().toString();
            String entrance = _entranceET.getText().toString();
            if(TextUtils.isEmpty(name)) {
                Toast.makeText(this, "Resort name cannot be empty!", Toast.LENGTH_SHORT).show();
                _loading.dismiss();
            }else if(location.equals("Location")) {
                Toast.makeText(this, "Please set your location!", Toast.LENGTH_SHORT).show();
                _loading.dismiss();
            }else if(TextUtils.isEmpty(entrance)) {
                Toast.makeText(this, "Entrance Fee cannot be empty!", Toast.LENGTH_SHORT).show();
                _loading.dismiss();
            }else {

                if(isUpdate) {
                    updateFile();
                }else {
                    uploadFile();
                }
            }
        });

        _openFile.setOnClickListener(view -> {
            openFileImage();
        });

        _getLocationBtn.setOnClickListener(view -> {

            // Start the autocomplete intent.
//            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
//                    .build(this);
//            startAutocomplete.launch(intent);

            checkLocationPermission();
        });

        backBtn.setOnClickListener(view -> {
            Intent gotoManageResort = new Intent(this, ManageResort.class);
            startActivity(gotoManageResort);
            finish();
        });
    }

    private void updateFile() {
        if (_imgURI != null || _resortPhoto.getDrawable() != null) {
            if (_imgURI != null) {
                StorageReference resortRef = resortPhotoRef.child(picName + "." + getFileExtension(_imgURI));
                UploadTask uploadTask = resortRef.putFile(_imgURI);

                uploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return resortRef.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadURI = (Uri) task.getResult();
                        if (downloadURI != null) {
                            String myUri = downloadURI.toString();
                            uploadAllUpdates(myUri);
                        } else {
                            // Handle the case when downloadURI is null
                            Toast.makeText(AddResortPage.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                            _loading.dismiss();
                        }
                    } else {
                        // Handle the case when the upload task is not successful
                        Toast.makeText(AddResortPage.this, "Upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        _loading.dismiss();
                    }
                });
            } else {
                // If _imgURI is null but there is an image in _resortPhoto, you can extract the drawable and convert it to a Uri.
                // Replace "R.drawable.default_image" with the appropriate default image resource.
                new ConvertDrawableToUriTask().execute(_resortPhoto.getDrawable());
            }
        } else {
            Toast.makeText(this, "No file selected!", Toast.LENGTH_SHORT).show();
            _loading.dismiss();
        }
    }

    private class ConvertDrawableToUriTask extends AsyncTask<Drawable, Void, String> {
        @Override
        protected String doInBackground(Drawable... drawables) {
            Drawable drawable = drawables[0];
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                return saveBitmapAndGetUri(bitmap);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String myUri) {
            if (myUri != null) {
                //uploadAllUpdates(myUri);
                StorageReference resortRef = resortPhotoRef.child(picName + "." + getFileExtension(Uri.parse(myUri)));
                UploadTask uploadTask = resortRef.putFile(Uri.parse(myUri));

                uploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return resortRef.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadURI = (Uri) task.getResult();
                        if (downloadURI != null) {
                            String myUri1 = downloadURI.toString();
                            uploadAllUpdates(myUri1);
                        } else {
                            // Handle the case when downloadURI is null
                            Toast.makeText(AddResortPage.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                            _loading.dismiss();
                        }
                    } else {
                        // Handle the case when the upload task is not successful
                        Toast.makeText(AddResortPage.this, "Upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        _loading.dismiss();
                    }
                });
            } else {
                // Handle the case when Uri conversion fails.
                Toast.makeText(AddResortPage.this, "Failed to convert drawable to Uri", Toast.LENGTH_SHORT).show();
                _loading.dismiss();
            }
        }
    }

    private String saveBitmapAndGetUri(Bitmap bitmap) {
        // You need to implement a method to save the Bitmap as a file and return the Uri.
        // Example code for saving a Bitmap to a file:
         File imageFile = new File(getExternalCacheDir(), picName + ".png");
         OutputStream os;
         try {
             os = new FileOutputStream(imageFile);
             bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
             os.flush();
             os.close();
             return Uri.fromFile(imageFile).toString();
         } catch (IOException e) {
             e.printStackTrace();
             return null;
         }
    }

    private void uploadAllUpdates(String myUri) {
        String owner = _ownerET.getText().toString();
        String entrance = _entranceET.getText().toString();
        String name = _resortNameET.getText().toString();
        String location = _getLocationBtn.getText().toString();
        Date dateTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String date = dateFormat.format(dateTime);
        String time = timeFormat.format(dateTime);

        Map<String, Object> updateValue = new HashMap<>();
        updateValue.put("RESORT_PIC_URL", myUri);
        updateValue.put("RESORT_ENTRANCE_FEE", entrance);
        updateValue.put("RESORT_PIC_NAME", picName);
        updateValue.put("RESORT_NAME", name);
        updateValue.put("OWNER_NAME", owner);
        updateValue.put("LOCATION", location);
        updateValue.put("LAT", lat);
        updateValue.put("LNG", lon);
        updateValue.put("TIME", time);
        updateValue.put("DATE", date);

        resortCol.document(resortID)
                .update(updateValue)
                .addOnSuccessListener(unused -> {
                    _loading.dismiss();
                    Toast.makeText(AddResortPage.this, "Resort updated successfully.", Toast.LENGTH_SHORT).show();
                    Intent gotoManageResort = new Intent(AddResortPage.this, ManageResort.class);
                    startActivity(gotoManageResort);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    _loading.dismiss();
                });
    }

    private void _var() {
        _resortPhoto = findViewById(R.id.ResortsPhoto);
        _openFile = findViewById(R.id.openFile);
        _ownerET = findViewById(R.id.ownerET);
        _entranceET = findViewById(R.id.entranceET);
        _resortNameET = findViewById(R.id.resortNameET);
        _getLocationBtn = findViewById(R.id.getLocationBtn);
        _postBtn = findViewById(R.id.postBtn);
        _title = findViewById(R.id.title);
        backBtn = findViewById(R.id.backBtn);
    }

    @SuppressLint("SetTextI18n")
    private final ActivityResultLauncher<Intent> startAutocomplete = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        Place place = Autocomplete.getPlaceFromIntent(intent);
                        _getLocationBtn.setText(place.getName() + "," + place.getAddress());
                        lat = place.getLatLng().latitude;
                        lon = place.getLatLng().longitude;
                        //Toast.makeText(this, "Place: ${place.getName()}, ${place.getId()}", Toast.LENGTH_SHORT).show();
                    }
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    // The user canceled the operation.
                    Toast.makeText(this, "User canceled autocomplete", Toast.LENGTH_SHORT).show();
                }
            });

    private void uploadFile() {
        resortPicName = String.valueOf(System.currentTimeMillis());
        if(_imgURI != null) {
            StorageReference resortRef = resortPhotoRef.child( resortPicName+ "." + getFileExtension(_imgURI));
            resortRef.putFile(_imgURI).addOnCompleteListener(task -> {
                UploadTask uploadTask =resortRef.putFile(_imgURI);

                uploadTask.continueWithTask((Continuation) task1 -> {

                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return resortRef.getDownloadUrl();
                }).addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful()) {
                        Uri downloadURI = (Uri) task1.getResult();
                        assert downloadURI != null;
                        String myUri = downloadURI.toString();

                        uploadAll(myUri);
                    }
                });
            }).addOnFailureListener(e -> Toast.makeText(AddResortPage.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        }else {
            Toast.makeText(this, "No file selected!", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadAll(String myUri) {
        String owner = _ownerET.getText().toString();
        String entrance = _entranceET.getText().toString();
        String name = _resortNameET.getText().toString();
        String location = _getLocationBtn.getText().toString();
        Date dateTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String date = dateFormat.format(dateTime);
        String time = timeFormat.format(dateTime);

        DBQuery.setResort(_user.getUid(),
                myUri,
                entrance,
                resortPicName,
                name,
                owner,
                location,
                lat,
                lon,
                time,
                date, new MyCompleteListener() {
                    @Override
                    public void onSuccess() {
                        _loading.dismiss();
                        Toast.makeText(AddResortPage.this, "Resort posted successfully.", Toast.LENGTH_SHORT).show();
                        Intent gotoManageResort = new Intent(AddResortPage.this, ManageResort.class);
                        startActivity(gotoManageResort);
                        finish();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        _loading.dismiss();
                        Toast.makeText(AddResortPage.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

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

                        Geocoder geocoder = new Geocoder(AddResortPage.this, Locale.getDefault());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PHOTO_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            _imgURI = data.getData();

            Picasso.get().load(_imgURI).into(_resortPhoto);
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
        Intent gotoManageResort = new Intent(this, ManageResort.class);
        startActivity(gotoManageResort);
        finish();
    }
}