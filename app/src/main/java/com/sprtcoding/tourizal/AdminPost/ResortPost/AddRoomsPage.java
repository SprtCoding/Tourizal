package com.sprtcoding.tourizal.AdminPost.ResortPost;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.sprtcoding.tourizal.Adapter.ImageListVPAdapter;
import com.sprtcoding.tourizal.Adapter.ResortSelectAdapter;
import com.sprtcoding.tourizal.FireStoreDB.DBQuery;
import com.sprtcoding.tourizal.FireStoreDB.MyCompleteListener;
import com.sprtcoding.tourizal.Model.ResortsModel;
import com.sprtcoding.tourizal.R;
import com.sprtcoding.tourizal.Utility.NetworkChangeListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class AddRoomsPage extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser _user;
    private StorageReference roomsPhotoRef;
    FirebaseFirestore DB;
    CollectionReference resortCollectionRef;
    private ImageView _roomsPhoto, backBtn;
    private TextView _openFile, _title;
    private MaterialButton _postBtn, _from_Day_Btn, _to_Day_Btn, _from_Night_Btn, _to_Night_Btn;
    private RelativeLayout _pickImagesBtn;
    private ViewPager _imagesVP;
    private TextInputEditText _roomNumberET, _descriptionET
            , _dayPriceET, _nightPriceET, _maxDayET, _excessDayET, _maxNightET, _excessNightET;
    int t1Hour, t1Minute;
    ProgressDialog _loading;
    private String _roomsID, roomPicID, roomPicName;
    private boolean isUpdate;
    private String updateRoomID, updateResortID, updateRoomPicURL, updateRoomDesc, updateDayAvailability, updateNightAvailability, updateRoomPicName, updateRoomPicID;
    private float updateDayPrice, updateNightPrice, dayExcess, nightExcess;
    private int updateRoomNo, maxNight, maxDay;
    public static String _resortID;
    private static final int PHOTO_IMAGE_REQUEST = 1;
    private Uri _imgURI;
    List<ResortsModel> resortsModels;
    ArrayList<Uri> imageListUri;
    ArrayList<String> roomPicIDs, roomNames, photoUrls, UrlsList, updatedRoomIds, updatedRoomNames;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rooms_page);
        _var();

        resortsModels = new ArrayList<>();
        imageListUri = new ArrayList<>();
        UrlsList = new ArrayList<>();
        roomPicIDs = new ArrayList<>();
        roomNames = new ArrayList<>();

        _imagesVP.setClipToPadding(false);
        _imagesVP.setClipChildren(false);
        _imagesVP.setPageMargin(30);

        DBQuery.g_firestore = FirebaseFirestore.getInstance();

        DB = FirebaseFirestore.getInstance();
        resortCollectionRef = DB.collection("RESORTS");

        _loading = new ProgressDialog(this);
        _loading.setTitle("Uploading");
        _loading.setMessage("Please wait...");

        mAuth = FirebaseAuth.getInstance();
        _user = mAuth.getCurrentUser();

        if(getIntent() != null) {
            isUpdate = getIntent().getBooleanExtra("isUpdate", false);
            updateRoomNo = getIntent().getIntExtra("roomNo", 0);
            updateRoomID = getIntent().getStringExtra("roomID");
            updateResortID = getIntent().getStringExtra("resortID");
            updateRoomPicURL = getIntent().getStringExtra("roomPic");
            updateRoomDesc = getIntent().getStringExtra("roomDesc");
            updateDayAvailability = getIntent().getStringExtra("dayAvailability");
            updateNightAvailability = getIntent().getStringExtra("nightAvailability");
            updateDayPrice = getIntent().getFloatExtra("dayPrice", 0);
            updateNightPrice = getIntent().getFloatExtra("nightPrice", 0);
            updateRoomPicName = getIntent().getStringExtra("roomPicName");
            updateRoomPicID = getIntent().getStringExtra("roomPicID");
            maxNight = getIntent().getIntExtra("maxNight", 0);
            maxDay = getIntent().getIntExtra("maxDay", 0);
            dayExcess = getIntent().getFloatExtra("dayExcess",0);
            nightExcess = getIntent().getFloatExtra("nightExcess", 0);

            if(updateDayAvailability != null && updateNightAvailability != null) {
                // Split the string based on the '-' delimiter
                String[] dayParts = updateDayAvailability.split(" - ");
                String[] nightParts = updateNightAvailability.split(" - ");

                // Extract the second time (the second part)
                String dayStartTime = dayParts[0];
                String dayEndTime = dayParts[1];
                String nightStartTime = nightParts[0];
                String nightEndTime = nightParts[1];

                if(isUpdate) {
                    _title.setText("Update Room");
                    //Picasso.get().load(updateRoomPicURL).placeholder(R.drawable.room_bed).into(_roomsPhoto);
                    _roomNumberET.setText(String.valueOf(updateRoomNo));
                    _descriptionET.setText(updateRoomDesc);
                    _from_Day_Btn.setText(dayStartTime);
                    _to_Day_Btn.setText(dayEndTime);
                    _from_Night_Btn.setText(nightStartTime);
                    _to_Night_Btn.setText(nightEndTime);
                    _dayPriceET.setText(String.valueOf(updateDayPrice));
                    _nightPriceET.setText(String.valueOf(updateNightPrice));
                    _maxDayET.setText(String.valueOf(maxDay));
                    _maxNightET.setText(String.valueOf(maxNight));
                    _excessDayET.setText(String.valueOf(dayExcess));
                    _excessNightET.setText(String.valueOf(nightExcess));

                    resortCollectionRef
                            .document(updateResortID)
                            .collection("ROOMS")
                            .document(updateRoomID).get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            photoUrls = documentSnapshot.contains("ROOM_PHOTO_URL")
                                    ? (ArrayList<String>) documentSnapshot.get("ROOM_PHOTO_URL")
                                    : new ArrayList<>();

                            updatedRoomIds = documentSnapshot.contains("ROOM_PIC_ID")
                                    ? (ArrayList<String>) documentSnapshot.get("ROOM_PIC_ID")
                                    : new ArrayList<>();

                            updatedRoomNames = documentSnapshot.contains("ROOM_PIC_NAME")
                                    ? (ArrayList<String>) documentSnapshot.get("ROOM_PIC_NAME")
                                    : new ArrayList<>();

//                            assert photoUrls != null;
//                            imageListUri = convertToUriList(photoUrls);

                            // Pass photoUrls to your ViewPager adapter
                            ImageListVPAdapter adapter = new ImageListVPAdapter(this,imageListUri, photoUrls, true);
                            _imagesVP.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }).addOnFailureListener(e -> {
                        // Handle failure
                    });
                }
            }
        }

        _pickImagesBtn.setOnClickListener(view -> PickImageFromGallery());

        roomsPhotoRef = FirebaseStorage.getInstance().getReference("RoomsPhotos/");

        //_openFile.setOnClickListener(view -> openFileImage());

        _from_Day_Btn.setOnClickListener(view -> openTimeFromDayDialog());

        _to_Day_Btn.setOnClickListener(view -> openTimeToDayDialog());

        _from_Night_Btn.setOnClickListener(view -> openTimeFromNightDialog());

        _to_Night_Btn.setOnClickListener(view -> openTimeToNightDialog());

        _postBtn.setOnClickListener(view -> {
            _loading.show();
            String roomNumber = _roomNumberET.getText().toString();
            String description = _descriptionET.getText().toString();
            String fromDayTime = _from_Day_Btn.getText().toString();
            String toDayTime = _to_Day_Btn.getText().toString();
            String fromNightTime = _from_Night_Btn.getText().toString();
            String toNightTime = _to_Night_Btn.getText().toString();
            String nightPrice = _nightPriceET.getText().toString();
            String dayPrice = _dayPriceET.getText().toString();
            String maxDay = _maxDayET.getText().toString();
            String maxNight = _maxNightET.getText().toString();
            String excessDay = _excessDayET.getText().toString();
            String excessNight = _excessNightET.getText().toString();

            if(TextUtils.isEmpty(roomNumber)) {
                Toast.makeText(this, "Room number is required!", Toast.LENGTH_SHORT).show();
                _loading.dismiss();
            }else if(TextUtils.isEmpty(description)) {
                Toast.makeText(this, "Description is required!", Toast.LENGTH_SHORT).show();
                _loading.dismiss();
            }else if(TextUtils.isEmpty(fromDayTime)) {
                Toast.makeText(this, "Please add day time!", Toast.LENGTH_SHORT).show();
                _loading.dismiss();
            }else if(TextUtils.isEmpty(toDayTime)) {
                Toast.makeText(this, "Please add day time!", Toast.LENGTH_SHORT).show();
                _loading.dismiss();
            }else if(TextUtils.isEmpty(fromNightTime)) {
                Toast.makeText(this, "Please add day time!", Toast.LENGTH_SHORT).show();
                _loading.dismiss();
            }else if(TextUtils.isEmpty(toNightTime)) {
                Toast.makeText(this, "Please add day time!", Toast.LENGTH_SHORT).show();
                _loading.dismiss();
            }else if(TextUtils.isEmpty(nightPrice)) {
                Toast.makeText(this, "Please add night price!", Toast.LENGTH_SHORT).show();
                _loading.dismiss();
            }else if(TextUtils.isEmpty(dayPrice)) {
                Toast.makeText(this, "Please add day price!", Toast.LENGTH_SHORT).show();
                _loading.dismiss();
            }else if(TextUtils.isEmpty(maxDay)) {
                Toast.makeText(this, "Please add maximum day!", Toast.LENGTH_SHORT).show();
                _loading.dismiss();
            }else if(TextUtils.isEmpty(maxNight)) {
                Toast.makeText(this, "Please add maximum night!", Toast.LENGTH_SHORT).show();
                _loading.dismiss();
            }else if(TextUtils.isEmpty(excessDay)) {
                Toast.makeText(this, "Please add excess day price!", Toast.LENGTH_SHORT).show();
                _loading.dismiss();
            }else if(TextUtils.isEmpty(excessNight)) {
                Toast.makeText(this, "Please add excess night price!", Toast.LENGTH_SHORT).show();
                _loading.dismiss();
            }else {
                if(isUpdate) {
                    updateFile();
                }else {
                    uploadFile();
                }
            }
        });

        backBtn.setOnClickListener(view -> {
            Intent gotoManageRooms = new Intent(this, ManageRooms.class);
            startActivity(gotoManageRooms);
            finish();
        });
    }

    private void PickImageFromGallery() {
        Intent openFile = new Intent();
        openFile.setType("image/*");
        openFile.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        openFile.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(openFile, 1);
    }

    public static String generateRandomUid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    private void updateFile() {
        if (photoUrls == null || photoUrls.isEmpty() || imageListUri == null || imageListUri.isEmpty()) {
            updateWithoutPhoto();
//            Toast.makeText(this, "No images to be updated.", Toast.LENGTH_SHORT).show();
//            _loading.dismiss();
//            return;
        }

        for(int i = 0; i < photoUrls.size(); i++) {
            roomPicID = generateRandomUid();
            String oldIMGUrls = photoUrls.get(i);

            if(oldIMGUrls != null) {
                for(int x = 0; x < imageListUri.size(); x++) {
                    Uri newImageUriList = imageListUri.get(x);

                    if (newImageUriList != null) {

                        StorageReference oldImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(oldIMGUrls);
                        oldImageRef.delete().addOnSuccessListener(aVoid -> {
                            // Upload the new image
                            byte[] update_bytes = new byte[0];
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), newImageUriList);
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
                                update_bytes = byteArrayOutputStream.toByteArray();
                            }catch (IOException e) {
                                e.printStackTrace();
                            }

                            roomPicName = String.valueOf(System.currentTimeMillis()); // You may want to generate a unique name
                            StorageReference newImageRef = roomsPhotoRef.child(roomPicID).child(roomPicName + "." + getFileExtension(newImageUriList));
                            newImageRef.putBytes(update_bytes)
                                    .addOnCompleteListener(task -> {
                                        newImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                            UrlsList.add(String.valueOf(uri));
                                            if(UrlsList.size() == imageListUri.size()) {
                                                uploadAllUpdates(UrlsList);
                                            }
                                        }).addOnFailureListener(e -> {
                                            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            _loading.dismiss();
                                        });
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        _loading.dismiss();
                                    });
                        });
                    }else {
                        Toast.makeText(this, "No image to be updated.", Toast.LENGTH_SHORT).show();
                        _loading.dismiss();
                    }
                }
            }else {
                Toast.makeText(this, "Null photo urls.", Toast.LENGTH_SHORT).show();
                _loading.dismiss();
            }
        }
    }
    private void updateWithoutPhoto() {
        String roomNumber = _roomNumberET.getText().toString();
        String description = _descriptionET.getText().toString();
        String fromDayTime = _from_Day_Btn.getText().toString();
        String toDayTime = _to_Day_Btn.getText().toString();
        String fromNightTime = _from_Night_Btn.getText().toString();
        String toNightTime = _to_Night_Btn.getText().toString();
        String nightPrice = _nightPriceET.getText().toString();
        String dayPrice = _dayPriceET.getText().toString();
        String maxDay = _maxDayET.getText().toString();
        String maxNight = _maxNightET.getText().toString();
        String excessDay = _excessDayET.getText().toString();
        String excessNight = _excessNightET.getText().toString();

        Map<String, Object> updateValue = new HashMap<>();
        updateValue.put("ROOM_NO", Integer.parseInt(roomNumber));
        updateValue.put("DESCRIPTION", description);
        updateValue.put("DAY_AVAILABILITY", fromDayTime + " - " + toDayTime);
        updateValue.put("DAY_PRICE", Float.parseFloat(dayPrice));
        updateValue.put("NIGHT_AVAILABILITY", fromNightTime + " - " + toNightTime);
        updateValue.put("NIGHT_PRICE", Float.parseFloat(nightPrice));
        updateValue.put("DAY_EXCESS", Float.parseFloat(excessDay));
        updateValue.put("NIGHT_EXCESS", Float.parseFloat(excessNight));
        updateValue.put("MAX_DAY", Integer.parseInt(maxDay));
        updateValue.put("MAX_NIGHT", Integer.parseInt(maxNight));

        resortCollectionRef.document(updateResortID)
                .collection("ROOMS").document(updateRoomID)
                .update(updateValue)
                .addOnSuccessListener(unused -> {
                    _loading.dismiss();
                    Toast.makeText(this, "Room " + roomNumber + " updated successfully.", Toast.LENGTH_SHORT).show();
                    Intent gotoManageRooms = new Intent(this, ManageRooms.class);
                    startActivity(gotoManageRooms);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    _loading.dismiss();
                });
    }

    private void uploadAllUpdates(ArrayList<String> urlsList) {
        String roomNumber = _roomNumberET.getText().toString();
        String description = _descriptionET.getText().toString();
        String fromDayTime = _from_Day_Btn.getText().toString();
        String toDayTime = _to_Day_Btn.getText().toString();
        String fromNightTime = _from_Night_Btn.getText().toString();
        String toNightTime = _to_Night_Btn.getText().toString();
        String nightPrice = _nightPriceET.getText().toString();
        String dayPrice = _dayPriceET.getText().toString();
        String maxDay = _maxDayET.getText().toString();
        String maxNight = _maxNightET.getText().toString();
        String excessDay = _excessDayET.getText().toString();
        String excessNight = _excessNightET.getText().toString();

        Map<String, Object> updateValue = new HashMap<>();
        updateValue.put("ROOM_NO", Integer.parseInt(roomNumber));
        updateValue.put("DESCRIPTION", description);
        updateValue.put("DAY_AVAILABILITY", fromDayTime + " - " + toDayTime);
        updateValue.put("DAY_PRICE", Float.parseFloat(dayPrice));
        updateValue.put("NIGHT_AVAILABILITY", fromNightTime + " - " + toNightTime);
        updateValue.put("NIGHT_PRICE", Float.parseFloat(nightPrice));
        updateValue.put("DAY_EXCESS", Float.parseFloat(excessDay));
        updateValue.put("NIGHT_EXCESS", Float.parseFloat(excessNight));
        updateValue.put("MAX_DAY", Integer.parseInt(maxDay));
        updateValue.put("MAX_NIGHT", Integer.parseInt(maxNight));
        updateValue.put("ROOM_PHOTO_URL", urlsList);

        resortCollectionRef.document(updateResortID)
                .collection("ROOMS").document(updateRoomID)
                .update(updateValue)
                .addOnSuccessListener(unused -> {
                    _loading.dismiss();
                    Toast.makeText(this, "Room " + roomNumber + " updated successfully.", Toast.LENGTH_SHORT).show();
                    Intent gotoManageRooms = new Intent(this, ManageRooms.class);
                    startActivity(gotoManageRooms);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    _loading.dismiss();
                });
    }

    private void uploadFile() {
        // Add data to Firestore with an auto-generated document ID
        roomPicID = generateRandomUid();
        for(int i = 0; i < imageListUri.size(); i++) {
            roomPicName = String.valueOf(System.currentTimeMillis());
            Uri individualIMG = imageListUri.get(i);

            byte[] bytes = new byte[0];
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), individualIMG);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
                bytes = byteArrayOutputStream.toByteArray();
            }catch (IOException e) {
                e.printStackTrace();
            }

            if(individualIMG != null) {
                StorageReference resortRef = roomsPhotoRef.child(roomPicID).child(roomPicName + "." + getFileExtension(individualIMG));
                resortRef.putBytes(bytes)
                        .addOnCompleteListener(task -> {
                            resortRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                UrlsList.add(String.valueOf(uri));
                                if(UrlsList.size() == imageListUri.size()) {
                                    uploadAll(UrlsList);
                                }
                            }).addOnFailureListener(e -> {
                                Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                _loading.dismiss();
                            });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            _loading.dismiss();
                        });
            }else {
                Toast.makeText(this, "No file selected!", Toast.LENGTH_SHORT).show();
                _loading.dismiss();
            }
        }
    }

    private void uploadAll(ArrayList<String> urlsList) {
        String roomNumber = _roomNumberET.getText().toString();
        String description = _descriptionET.getText().toString();
        String fromDayTime = _from_Day_Btn.getText().toString();
        String toDayTime = _to_Day_Btn.getText().toString();
        String fromNightTime = _from_Night_Btn.getText().toString();
        String toNightTime = _to_Night_Btn.getText().toString();
        String maxDay = _maxDayET.getText().toString();
        String maxNight = _maxNightET.getText().toString();
        String excessDay = _excessDayET.getText().toString();
        String excessNight = _excessNightET.getText().toString();
        String nightPrice = _nightPriceET.getText().toString();
        String dayPrice = _dayPriceET.getText().toString();
        // Add data to Firestore with an auto-generated document ID
        DocumentReference newDocRef = FirebaseFirestore.getInstance().collection("RESORTS").document();
        _roomsID = newDocRef.getId();

        resortCollectionRef.whereEqualTo("OWNER_UID", _user.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for(DocumentSnapshot doc : queryDocumentSnapshots) {
                        String resort_id = doc.getString("RESORT_ID");
                        DBQuery.setRooms(
                                _user.getUid(),
                                resort_id,
                                _roomsID,
                                Integer.parseInt(roomNumber),
                                description,
                                fromDayTime + " - " + toDayTime,
                                Float.parseFloat(dayPrice),
                                fromNightTime + " - " + toNightTime,
                                Float.parseFloat(excessDay),
                                Float.parseFloat(excessNight),
                                Integer.parseInt(maxDay),
                                Integer.parseInt(maxNight),
                                Float.parseFloat(nightPrice),
                                urlsList,
                                new MyCompleteListener() {
                                    @Override
                                    public void onSuccess() {
                                        _loading.dismiss();
                                        Toast.makeText(AddRoomsPage.this, "Rooms posted successfully.", Toast.LENGTH_SHORT).show();
                                        Intent gotoManageRooms = new Intent(AddRoomsPage.this, ManageRooms.class);
                                        startActivity(gotoManageRooms);
                                        imageListUri.clear();
                                        finish();
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        _loading.dismiss();
                                        Toast.makeText(AddRoomsPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void openTimeToNightDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                AddRoomsPage.this,
                (timePicker, hourOfDay, minute) -> {
                    t1Hour = hourOfDay;
                    t1Minute = minute;

                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.set(0,0,0,t1Hour,t1Minute);
                    _to_Night_Btn.setText(DateFormat.format("hh:mm aa", calendar1));
                }, 12, 0 , false);
        timePickerDialog.updateTime(t1Hour, t1Minute);
        timePickerDialog.show();
    }

    private void openTimeFromNightDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                AddRoomsPage.this,
                (TimePickerDialog.OnTimeSetListener) (timePicker, hourOfDay, minute) -> {
                    t1Hour = hourOfDay;
                    t1Minute = minute;

                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.set(0,0,0,t1Hour,t1Minute);
                    _from_Night_Btn.setText(DateFormat.format("hh:mm aa", calendar1));
                }, 12, 0 , false);
        timePickerDialog.updateTime(t1Hour, t1Minute);
        timePickerDialog.show();
    }

    private void openTimeToDayDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                AddRoomsPage.this,
                (timePicker, hourOfDay, minute) -> {
                    t1Hour = hourOfDay;
                    t1Minute = minute;

                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.set(0,0,0,t1Hour,t1Minute);
                    _to_Day_Btn.setText(DateFormat.format("hh:mm aa", calendar1));
                }, 12, 0 , false);
        timePickerDialog.updateTime(t1Hour, t1Minute);
        timePickerDialog.show();
    }

    private void openTimeFromDayDialog() {

        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute1) -> {
                    Calendar selectedTime = Calendar.getInstance();
                    selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedTime.set(Calendar.MINUTE, minute1);

                    // Format the selected time
                    String formattedTime = DateFormat.format("hh:mm aa", selectedTime).toString();
                    _from_Day_Btn.setText(formattedTime);
                },
                hour,
                minute,
                false // Set to true for 24-hour format, false for AM/PM format
        );

        timePickerDialog.show();
    }

    private void _var() {
        _title = findViewById(R.id.title);
        _from_Day_Btn = findViewById(R.id.from_day_btn);
        _to_Day_Btn = findViewById(R.id.to_day_btn);
        _from_Night_Btn = findViewById(R.id.from_night_btn);
        _to_Night_Btn = findViewById(R.id.to_night_btn);
        _openFile = findViewById(R.id.openFile);
        _postBtn = findViewById(R.id.postBtn);
        _roomNumberET = findViewById(R.id.roomNumberET);
        _descriptionET = findViewById(R.id.descriptionET);
        _dayPriceET = findViewById(R.id.dayPriceET);
        _nightPriceET = findViewById(R.id.nightPriceET);
        _maxDayET = findViewById(R.id.maxDayET);
        _excessDayET = findViewById(R.id.excessDayET);
        _maxNightET = findViewById(R.id.maxNightET);
        _excessNightET = findViewById(R.id.excessNightET);
        _pickImagesBtn = findViewById(R.id.pickImagesBtn);
        _imagesVP = findViewById(R.id.imagesVP);
        backBtn = findViewById(R.id.backBtn);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getClipData() != null) {
            int count = data.getClipData().getItemCount();

            for(int i = 0; i < count; i++) {
                _imgURI = data.getClipData().getItemAt(i).getUri();
                imageListUri.add(_imgURI);
                setImageVPAdapter();
            }
        }
    }

    private void setImageVPAdapter() {
        ImageListVPAdapter imageListVPAdapter = new ImageListVPAdapter(this,imageListUri, photoUrls, false);
        _imagesVP.setAdapter(imageListVPAdapter);
        imageListVPAdapter.notifyDataSetChanged();
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
        Intent gotoManageRooms = new Intent(this, ManageRooms.class);
        startActivity(gotoManageRooms);
        finish();
    }
}