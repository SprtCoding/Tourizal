package com.sprtcoding.tourizal.AdminPost.ResortPost;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
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
import java.util.HashMap;
import java.util.List;

public class AddCottagePage extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser _user;
    private FirebaseDatabase mDb;
    private DatabaseReference cottagePostRef, resortPostRef;
    private StorageReference cottagePhotoRef;
    FirebaseFirestore DB;
    CollectionReference resortCollectionRef;
    private ImageView _cottagePhoto, backBtn;
    private TextView _openFile;
    private MaterialButton _postBtn;
    private TextInputEditText _cottageNumberET, _descriptionET, _cottageFeeET;
    private RecyclerView _resort_rv;
    ProgressDialog _loading;
    private String _cottageID, cottagePicID, cottagePicName;
    public static String _resortID;
    private static final int PHOTO_IMAGE_REQUEST = 1;
    private Uri _imgURI;
    ResortSelectAdapter resortSelectAdapter;
    List<ResortsModel> resortsModels;
    public static AlertDialog resortDialog;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cottage_page);
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

        cottagePhotoRef = FirebaseStorage.getInstance().getReference("CottagesPhotos/");

        _openFile.setOnClickListener(view -> {
            openFileImage();
        });

        _postBtn.setOnClickListener(view -> {
            _loading.show();
            String cottageNumber = _cottageNumberET.getText().toString();
            String description = _descriptionET.getText().toString();
            String cottageFee = _cottageFeeET.getText().toString();

            if(TextUtils.isEmpty(cottageNumber)) {
                Toast.makeText(this, "Cottage number is required!", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(description)) {
                Toast.makeText(this, "Description is required!", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(cottageFee)) {
                Toast.makeText(this, "Please add cottage fee!", Toast.LENGTH_SHORT).show();
            }else {
                uploadFile();
            }
        });

        backBtn.setOnClickListener(view -> {
            Intent gotoManageRooms = new Intent(this, ManageCottages.class);
            startActivity(gotoManageRooms);
            finish();
        });
    }

    private void uploadFile() {
        if(_imgURI != null) {
            // Add data to Firestore with an auto-generated document ID
            DocumentReference newDocRef = FirebaseFirestore.getInstance().collection("RESORTS").document();
            cottagePicID = newDocRef.getId();
            cottagePicName = String.valueOf(System.currentTimeMillis());
            StorageReference resortRef = cottagePhotoRef.child(cottagePicID).child( cottagePicName + "." + getFileExtension(_imgURI));
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
            }).addOnFailureListener(e -> Toast.makeText(AddCottagePage.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        }else {
            Toast.makeText(this, "No file selected!", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadAll(String myUri) {
        String cottageNumber = _cottageNumberET.getText().toString();
        String description = _descriptionET.getText().toString();
        String cottageFee = _cottageFeeET.getText().toString();
        // Add data to Firestore with an auto-generated document ID
        DocumentReference newDocRef = FirebaseFirestore.getInstance().collection("RESORTS").document();
        _cottageID = newDocRef.getId();

        resortCollectionRef.whereEqualTo("OWNER_UID", _user.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for(DocumentSnapshot doc : queryDocumentSnapshots) {
                        String resort_id = doc.getString("RESORT_ID");
                        DBQuery.setCottage(
                                _user.getUid(),
                                resort_id,
                                _cottageID,
                                cottagePicID,
                                cottagePicName,
                                Integer.parseInt(cottageNumber),
                                description,
                                Float.parseFloat(cottageFee),
                                myUri,
                                new MyCompleteListener() {
                                    @Override
                                    public void onSuccess() {
                                        _loading.dismiss();
                                        Toast.makeText(AddCottagePage.this, "Cottage posted successfully.", Toast.LENGTH_SHORT).show();
                                        Intent gotoManageCottage = new Intent(AddCottagePage.this, ManageCottages.class);
                                        startActivity(gotoManageCottage);
                                        finish();
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        _loading.dismiss();
                                        Toast.makeText(AddCottagePage.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );
                    }
                }).addOnFailureListener(e -> {
                    _loading.dismiss();
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void openFileImage() {
        Intent openFile = new Intent();
        openFile.setType("image/*");
        openFile.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(openFile, PHOTO_IMAGE_REQUEST);
    }

    private void _var() {
        _cottagePhoto = findViewById(R.id.CottagePhoto);
        _openFile = findViewById(R.id.openFile);
        _postBtn = findViewById(R.id.postBtn);
        _cottageNumberET = findViewById(R.id.cottageNumberET);
        _descriptionET = findViewById(R.id.descriptionET);
        _cottageFeeET = findViewById(R.id.cottageFeeET);
        backBtn = findViewById(R.id.backBtn);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PHOTO_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            _imgURI = data.getData();

            Picasso.get().load(_imgURI).into(_cottagePhoto);
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