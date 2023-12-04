package com.sprtcoding.tourizal.AdminPost.ResortPost;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sprtcoding.tourizal.FireStoreDB.DBQuery;
import com.sprtcoding.tourizal.R;
import com.sprtcoding.tourizal.Utility.NetworkChangeListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddKayakPage extends AppCompatActivity {
    private TextInputEditText kayakNumberET, rentPriceET, descriptionET;
    private MaterialButton postBtn;
    private ImageView kayakPhoto, backBtn;
    private TextView openFile, _title;
    private FirebaseAuth mAuth;
    private FirebaseUser _user;
    private StorageReference kayakPhotoRef;
    FirebaseFirestore DB;
    CollectionReference resortCollectionRef;
    ProgressDialog _loading;
    Uri _imgURI;
    private boolean isUpdate;
    String kayakPicID, kayakPicName, _kayakID, updateResortID, updateKayakID, updateKayakPicUrl, updateKayakDesc, updateKayakPicName, updateKayakPicID;
    private int updateKayakNo;
    private float updateKayakPrice;
    private static final int PHOTO_IMAGE_REQUEST = 1;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_kayak_page);
        _init();

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
            updateKayakID = getIntent().getStringExtra("kayakID");
            updateKayakPicUrl = getIntent().getStringExtra("kayakPicUrl");
            updateKayakDesc = getIntent().getStringExtra("kayakDesc");
            updateKayakPicName = getIntent().getStringExtra("kayakPicName");
            updateKayakPicID = getIntent().getStringExtra("kayakPicID");
            updateKayakNo = getIntent().getIntExtra("kayakNo", 0);
            updateKayakPrice = getIntent().getFloatExtra("kayakPrice",0);

            if(isUpdate) {
                _title.setText("Update Cottage");
                Picasso.get().load(updateKayakPicUrl).placeholder(R.drawable.kayak).into(kayakPhoto);
                kayakNumberET.setText(String.valueOf(updateKayakNo));
                rentPriceET.setText(String.valueOf(updateKayakPrice));
                descriptionET.setText(updateKayakDesc);
            }
        }

        kayakPhotoRef = FirebaseStorage.getInstance().getReference("KayakPhotos/");

        openFile.setOnClickListener(view -> {
            openFileImage();
        });

        postBtn.setOnClickListener(view -> {
            _loading.show();
            String _kayakNumber = Objects.requireNonNull(kayakNumberET.getText()).toString();
            String _rentPrice = Objects.requireNonNull(rentPriceET.getText()).toString();
            String _kayakDesc = Objects.requireNonNull(descriptionET.getText()).toString();

            if(TextUtils.isEmpty(_kayakNumber)) {
                Toast.makeText(this, "Kayak number is required!", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(_rentPrice)) {
                Toast.makeText(this, "Kayak rent price is required!", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(_kayakDesc)) {
                Toast.makeText(this, "Description is required!", Toast.LENGTH_SHORT).show();
            }else {
                if(isUpdate) {
                    updateFile();
                }else {
                    uploadFile();
                }
            }
        });

        backBtn.setOnClickListener(view -> {
            Intent gotoManageRooms = new Intent(this, ManageKayakin.class);
            startActivity(gotoManageRooms);
            finish();
        });
    }

    private void _init() {
        _title = findViewById(R.id.title);
        kayakNumberET = findViewById(R.id.kayakNumberET);
        rentPriceET = findViewById(R.id.rentPriceET);
        descriptionET = findViewById(R.id.descriptionET);
        kayakPhoto = findViewById(R.id.kayakPhoto);
        backBtn = findViewById(R.id.backBtn);
        openFile = findViewById(R.id.openFile);
        postBtn = findViewById(R.id.postBtn);
    }

    private void updateFile() {
        if (_imgURI != null || kayakPhoto.getDrawable() != null) {
            if (_imgURI != null) {
                StorageReference resortRef = kayakPhotoRef.child(updateKayakPicID).child(updateKayakPicName + "." + getFileExtension(_imgURI));
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
                new ConvertDrawableToUriTask().execute(kayakPhoto.getDrawable());
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
                StorageReference resortRef = kayakPhotoRef.child(updateKayakPicName + "." + getFileExtension(Uri.parse(myUri)));
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
                            Toast.makeText(AddKayakPage.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                            _loading.dismiss();
                        }
                    } else {
                        // Handle the case when the upload task is not successful
                        Toast.makeText(AddKayakPage.this, "Upload failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        _loading.dismiss();
                    }
                });
            } else {
                // Handle the case when Uri conversion fails.
                Toast.makeText(AddKayakPage.this, "Failed to convert drawable to Uri", Toast.LENGTH_SHORT).show();
                _loading.dismiss();
            }
        }
    }

    private String saveBitmapAndGetUri(Bitmap bitmap) {
        // You need to implement a method to save the Bitmap as a file and return the Uri.
        // Example code for saving a Bitmap to a file:
        File imageFile = new File(getExternalCacheDir(), updateKayakPicName + ".png");
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
        String _kayakNumber = Objects.requireNonNull(kayakNumberET.getText()).toString();
        String _rentPrice = Objects.requireNonNull(rentPriceET.getText()).toString();
        String _kayakDesc = Objects.requireNonNull(descriptionET.getText()).toString();

        Map<String, Object> updateValue = new HashMap<>();
        updateValue.put("KAYAKIN_IMG_URL", myUri);
        updateValue.put("KAYAKIN_DESC", _kayakDesc);
        updateValue.put("KAYAKIN_NO", Integer.parseInt(_kayakNumber));
        updateValue.put("RENT_PRICE", Float.parseFloat(_rentPrice));

        resortCollectionRef.document(updateResortID)
                .collection("KAYAKIN").document(updateKayakID)
                .update(updateValue)
                .addOnSuccessListener(unused -> {
                    _loading.dismiss();
                    Toast.makeText(this, "Kayak " + _kayakNumber + " updated successfully.", Toast.LENGTH_SHORT).show();
                    Intent gotoManageRooms = new Intent(this, ManageKayakin.class);
                    startActivity(gotoManageRooms);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    _loading.dismiss();
                });
    }

    private void uploadFile() {
        if (_imgURI != null) {
            // Add data to Firestore with an auto-generated document ID
            DocumentReference newDocRef = FirebaseFirestore.getInstance().collection("RESORTS").document();
            kayakPicID = newDocRef.getId();
            kayakPicName = String.valueOf(System.currentTimeMillis());
            StorageReference resortRef = kayakPhotoRef.child(kayakPicID).child(kayakPicName + "." + getFileExtension(_imgURI));

            resortRef.putFile(_imgURI)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            resortRef.getDownloadUrl()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Uri downloadURI = task1.getResult();
                                            if (downloadURI != null) {
                                                String myUri = downloadURI.toString();
                                                uploadAll(myUri);
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(this, "Upload failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "No file selected!", Toast.LENGTH_SHORT).show();
        }
    }
    private void uploadAll(String myUri) {
        String _kayakNumber = Objects.requireNonNull(kayakNumberET.getText()).toString();
        String _rentPrice = Objects.requireNonNull(rentPriceET.getText()).toString();
        String _kayakDesc = Objects.requireNonNull(descriptionET.getText()).toString();
        // Add data to Firestore with an auto-generated document ID
        DocumentReference newDocRef = FirebaseFirestore.getInstance().collection("RESORTS").document();
        _kayakID = newDocRef.getId();

        resortCollectionRef.whereEqualTo("OWNER_UID", _user.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for(DocumentSnapshot doc : queryDocumentSnapshots) {
                        String resort_id = doc.getString("RESORT_ID");

                        Map<String, Object> poolData = new HashMap<>();
                        poolData.put("OWNER_UID", _user.getUid());
                        poolData.put("RESORT_UID", resort_id);
                        poolData.put("KAYAKIN_UID", _kayakID);
                        poolData.put("KAYAKIN_PIC_ID", kayakPicID);
                        poolData.put("KAYAKIN_PIC_NAME", kayakPicName);
                        poolData.put("KAYAKIN_IMG_URL", myUri);
                        poolData.put("KAYAKIN_DESC", _kayakDesc);
                        poolData.put("KAYAKIN_NO", Integer.parseInt(_kayakNumber));
                        poolData.put("RENT_PRICE", Float.parseFloat(_rentPrice));

                        assert resort_id != null;
                        resortCollectionRef.document(resort_id)
                                .collection("KAYAKIN").document(_kayakID)
                                .set(poolData).addOnSuccessListener(queryDocumentSnapshots1 -> {
                                    _loading.dismiss();
                                    Intent gotoManageKayakin = new Intent(this, ManageKayakin.class);
                                    startActivity(gotoManageKayakin);
                                    finish();
                                }).addOnFailureListener(e -> Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show());
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PHOTO_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            _imgURI = data.getData();

            Picasso.get().load(_imgURI).into(kayakPhoto);
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
        Intent gotoManageRooms = new Intent(this, ManageKayakin.class);
        startActivity(gotoManageRooms);
        finish();
    }
}