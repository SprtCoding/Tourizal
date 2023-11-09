package com.sprtcoding.tourizal.AdminPost.ResortPost;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
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
import com.sprtcoding.tourizal.Adapter.ResortSelectAdapter;
import com.sprtcoding.tourizal.FireStoreDB.DBQuery;
import com.sprtcoding.tourizal.FireStoreDB.MyCompleteListener;
import com.sprtcoding.tourizal.Model.ResortsModel;
import com.sprtcoding.tourizal.R;
import com.sprtcoding.tourizal.Utility.NetworkChangeListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AddRoomsPage extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser _user;
    private FirebaseDatabase mDb;
    private DatabaseReference roomsPostRef, resortPostRef;
    private StorageReference roomsPhotoRef;
    FirebaseFirestore DB;
    CollectionReference resortCollectionRef;
    private ImageView _roomsPhoto, backBtn;
    private TextView _openFile;
    private MaterialButton _postBtn, _from_Day_Btn, _to_Day_Btn, _from_Night_Btn, _to_Night_Btn;
    private TextInputEditText _roomNumberET, _descriptionET
            , _dayPriceET, _nightPriceET;
    int t1Hour, t1Minute;
    ProgressDialog _loading;
    private String _roomsID, roomPicID, roomPicName;
    public static String _resortID;
    private static final int PHOTO_IMAGE_REQUEST = 1;
    private Uri _imgURI;
    List<ResortsModel> resortsModels;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rooms_page);
        _var();

        resortsModels = new ArrayList<>();

        DBQuery.g_firestore = FirebaseFirestore.getInstance();

        DB = FirebaseFirestore.getInstance();
        resortCollectionRef = DB.collection("RESORTS");

        _loading = new ProgressDialog(this);
        _loading.setTitle("Uploading");
        _loading.setMessage("Please wait...");

        mAuth = FirebaseAuth.getInstance();
        _user = mAuth.getCurrentUser();

        roomsPhotoRef = FirebaseStorage.getInstance().getReference("RoomsPhotos/");

        _openFile.setOnClickListener(view -> {
            openFileImage();
        });

        _from_Day_Btn.setOnClickListener(view -> {
            openTimeFromDayDialog();
        });

        _to_Day_Btn.setOnClickListener(view -> {
            openTimeToDayDialog();
        });

        _from_Night_Btn.setOnClickListener(view -> {
            openTimeFromNightDialog();
        });

        _to_Night_Btn.setOnClickListener(view -> {
            openTimeToNightDialog();
        });

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

            if(TextUtils.isEmpty(roomNumber)) {
                Toast.makeText(this, "Room number is required!", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(description)) {
                Toast.makeText(this, "Description is required!", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(fromDayTime)) {
                Toast.makeText(this, "Please add day time!", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(toDayTime)) {
                Toast.makeText(this, "Please add day time!", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(fromNightTime)) {
                Toast.makeText(this, "Please add day time!", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(toNightTime)) {
                Toast.makeText(this, "Please add day time!", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(nightPrice)) {
                Toast.makeText(this, "Please add night price!", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(dayPrice)) {
                Toast.makeText(this, "Please add day price!", Toast.LENGTH_SHORT).show();
            }else {
                uploadFile();
            }
        });

        backBtn.setOnClickListener(view -> {
            Intent gotoManageRooms = new Intent(this, ManageRooms.class);
            startActivity(gotoManageRooms);
            finish();
        });
    }

    private void uploadFile() {
        if(_imgURI != null) {
            // Add data to Firestore with an auto-generated document ID
            DocumentReference newDocRef = FirebaseFirestore.getInstance().collection("RESORTS").document();
            roomPicID = newDocRef.getId();
            roomPicName = String.valueOf(System.currentTimeMillis());
            StorageReference resortRef = roomsPhotoRef.child(roomPicID).child(roomPicName + "." + getFileExtension(_imgURI));
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
            }).addOnFailureListener(e -> Toast.makeText(AddRoomsPage.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        }else {
            Toast.makeText(this, "No file selected!", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadAll(String myUri) {
        String roomNumber = _roomNumberET.getText().toString();
        String description = _descriptionET.getText().toString();
        String fromDayTime = _from_Day_Btn.getText().toString();
        String toDayTime = _to_Day_Btn.getText().toString();
        String fromNightTime = _from_Night_Btn.getText().toString();
        String toNightTime = _to_Night_Btn.getText().toString();
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
                        DBQuery.setRooms(_user.getUid(),
                                resort_id,
                                _roomsID,
                                roomPicID,
                                roomPicName,
                                Integer.parseInt(roomNumber),
                                description,
                                fromDayTime + " - " + toDayTime,
                                Float.parseFloat(dayPrice),
                                fromNightTime + " - " + toNightTime,
                                Float.parseFloat(nightPrice),
                                myUri,
                                new MyCompleteListener() {
                                    @Override
                                    public void onSuccess() {
                                        _loading.dismiss();
                                        Toast.makeText(AddRoomsPage.this, "Rooms posted successfully.", Toast.LENGTH_SHORT).show();
                                        Intent gotoManageRooms = new Intent(AddRoomsPage.this, ManageRooms.class);
                                        startActivity(gotoManageRooms);
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

    private void openTimeToNightDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                AddRoomsPage.this,
                (TimePickerDialog.OnTimeSetListener) (timePicker, hourOfDay, minute) -> {
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
                (TimePickerDialog.OnTimeSetListener) (timePicker, hourOfDay, minute) -> {
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
        _from_Day_Btn = findViewById(R.id.from_day_btn);
        _to_Day_Btn = findViewById(R.id.to_day_btn);
        _from_Night_Btn = findViewById(R.id.from_night_btn);
        _to_Night_Btn = findViewById(R.id.to_night_btn);
        _roomsPhoto = findViewById(R.id.RoomsPhoto);
        _openFile = findViewById(R.id.openFile);
        _postBtn = findViewById(R.id.postBtn);
        _roomNumberET = findViewById(R.id.roomNumberET);
        _descriptionET = findViewById(R.id.descriptionET);
        _dayPriceET = findViewById(R.id.dayPriceET);
        _nightPriceET = findViewById(R.id.nightPriceET);
        backBtn = findViewById(R.id.backBtn);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PHOTO_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            _imgURI = data.getData();

            Picasso.get().load(_imgURI).into(_roomsPhoto);
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
        Intent gotoManageRooms = new Intent(this, ManageRooms.class);
        startActivity(gotoManageRooms);
        finish();
    }
}