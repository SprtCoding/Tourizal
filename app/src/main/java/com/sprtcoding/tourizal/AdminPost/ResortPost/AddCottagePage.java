package com.sprtcoding.tourizal.AdminPost.ResortPost;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AddCottagePage extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser _user;
    private StorageReference cottagePhotoRef;
    FirebaseFirestore DB;
    CollectionReference resortCollectionRef;
    private ImageView _cottagePhoto, backBtn;
    private TextView _openFile, _title;
    private MaterialButton _postBtn;
    private TextInputEditText _cottageNumberET, _descriptionET, _cottageFeeET;
    ProgressDialog _loading;
    private String _cottageID, cottagePicID, cottagePicName;
    public static String _resortID, updateResortID, updateCottageID, updateCottagePicURL, updateCottageDesc, updateCottagePicName, updateCottagePicID;
    private int updateCottageNo;
    private float updateCottageFee;
    private boolean isUpdate;
    private static final int PHOTO_IMAGE_REQUEST = 1;
    private Uri _imgURI;
    List<ResortsModel> resortsModels;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @SuppressLint("SetTextI18n")
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

        if(getIntent() != null) {
            isUpdate = getIntent().getBooleanExtra("isUpdate",false);
            updateResortID = getIntent().getStringExtra("resortID");
            updateCottageID = getIntent().getStringExtra("cottageID");
            updateCottagePicURL = getIntent().getStringExtra("cottagePicUrl");
            updateCottageDesc = getIntent().getStringExtra("cottageDesc");
            updateCottageNo = getIntent().getIntExtra("cottageNo", 0);
            updateCottageFee = getIntent().getFloatExtra("cottageFee", 0);
            updateCottagePicName  = getIntent().getStringExtra("cottagePicName");
            updateCottagePicID  = getIntent().getStringExtra("cottagePicID");

            if(isUpdate) {
                _title.setText("Update Cottage");
                Picasso.get().load(updateCottagePicURL).placeholder(R.drawable.room_bed).into(_cottagePhoto);
                _cottageNumberET.setText(String.valueOf(updateCottageNo));
                _cottageFeeET.setText(String.valueOf(updateCottageFee));
                _descriptionET.setText(updateCottageDesc);
            }
        }

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
                if(isUpdate) {
                    updateFile();
                }else {
                    uploadFile();
                }
            }
        });

        backBtn.setOnClickListener(view -> {
            Intent gotoManageRooms = new Intent(this, ManageCottages.class);
            startActivity(gotoManageRooms);
            finish();
        });
    }

    private void updateFile() {
        if (_imgURI != null || _cottagePhoto.getDrawable() != null) {
            if (_imgURI != null) {
                StorageReference resortRef = cottagePhotoRef.child(updateCottagePicID).child(updateCottagePicName + "." + getFileExtension(_imgURI));
                UploadTask uploadTask = resortRef.putFile(_imgURI);

                uploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
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
                            Toast.makeText(this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                            _loading.dismiss();
                        }
                    } else {
                        // Handle the case when the upload task is not successful
                        Toast.makeText(this, "Upload failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        _loading.dismiss();
                    }
                });
            } else {
                // If _imgURI is null but there is an image in _resortPhoto, you can extract the drawable and convert it to a Uri.
                // Replace "R.drawable.default_image" with the appropriate default image resource.
                new ConvertDrawableToUriTask().execute(_cottagePhoto.getDrawable());
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
                StorageReference resortRef = cottagePhotoRef.child(updateCottagePicName + "." + getFileExtension(Uri.parse(myUri)));
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
                            Toast.makeText(AddCottagePage.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                            _loading.dismiss();
                        }
                    } else {
                        // Handle the case when the upload task is not successful
                        Toast.makeText(AddCottagePage.this, "Upload failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        _loading.dismiss();
                    }
                });
            } else {
                // Handle the case when Uri conversion fails.
                Toast.makeText(AddCottagePage.this, "Failed to convert drawable to Uri", Toast.LENGTH_SHORT).show();
                _loading.dismiss();
            }
        }
    }

    private String saveBitmapAndGetUri(Bitmap bitmap) {
        // You need to implement a method to save the Bitmap as a file and return the Uri.
        // Example code for saving a Bitmap to a file:
        File imageFile = new File(getExternalCacheDir(), updateCottagePicName + ".png");
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
        String cottageNumber = _cottageNumberET.getText().toString();
        String description = _descriptionET.getText().toString();
        String cottageFee = _cottageFeeET.getText().toString();

        Map<String, Object> updateValue = new HashMap<>();
        updateValue.put("COTTAGE_NO", Integer.parseInt(cottageNumber));
        updateValue.put("DESCRIPTION", description);
        updateValue.put("PRICE", Float.parseFloat(cottageFee));
        updateValue.put("COTTAGE_PHOTO_URL", myUri);

        resortCollectionRef.document(updateResortID)
                .collection("COTTAGE").document(updateCottageID)
                .update(updateValue)
                .addOnSuccessListener(unused -> {
                    _loading.dismiss();
                    Toast.makeText(this, "Cottage " + cottageNumber + " updated successfully.", Toast.LENGTH_SHORT).show();
                    Intent gotoManageRooms = new Intent(this, ManageCottages.class);
                    startActivity(gotoManageRooms);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    _loading.dismiss();
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
        _title = findViewById(R.id.title);
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
        Intent gotoManageRooms = new Intent(this, ManageCottages.class);
        startActivity(gotoManageRooms);
        finish();
    }
}