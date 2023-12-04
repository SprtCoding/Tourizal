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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

public class AddPoolPage extends AppCompatActivity {
    private TextInputEditText poolNumberET, poolSizeET, descriptionET;
    private AutoCompleteTextView poolTypeCTV, poolSwimmerCTV;
    private MaterialButton postBtn;
    private ImageView poolPhoto, backBtn;
    private TextView openFile, _title;
    private FirebaseAuth mAuth;
    private FirebaseUser _user;
    private StorageReference poolPhotoRef;
    FirebaseFirestore DB;
    CollectionReference resortCollectionRef;
    ProgressDialog _loading;
    Uri _imgURI;
    private boolean isUpdate;
    String poolPicID, poolPicName, _poolID, updateResortID, updatePoolID, updatePoolPicURL, updatePoolDesc, updatePoolType, updatePoolSwimmer, updatePoolPicName, updatePoolPicID;
    private int updatePoolNo;
    private double updatePoolSize;
    private static final int PHOTO_IMAGE_REQUEST = 1;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pool_page);
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
            updatePoolID = getIntent().getStringExtra("poolID");
            updatePoolPicURL = getIntent().getStringExtra("poolPicUrl");
            updatePoolDesc = getIntent().getStringExtra("poolDesc");
            updatePoolType = getIntent().getStringExtra("poolType");
            updatePoolSwimmer = getIntent().getStringExtra("poolSwimmer");
            updatePoolPicName = getIntent().getStringExtra("poolPicName");
            updatePoolPicID = getIntent().getStringExtra("poolPicID");
            updatePoolNo = getIntent().getIntExtra("poolNo", 0);
            updatePoolSize = getIntent().getDoubleExtra("poolSize", 0);

            if(isUpdate) {
                _title.setText("Update Pool");
                Picasso.get().load(updatePoolPicURL).placeholder(R.drawable.room_bed).into(poolPhoto);
                poolNumberET.setText(String.valueOf(updatePoolNo));
                poolSizeET.setText(String.valueOf(updatePoolSize));
                descriptionET.setText(updatePoolDesc);
                poolTypeCTV.setText(updatePoolType);
                poolSwimmerCTV.setText(updatePoolSwimmer);
            }
        }

        poolPhotoRef = FirebaseStorage.getInstance().getReference("PoolPhotos/");

        openFile.setOnClickListener(view -> {
            openFileImage();
        });

        //pool type dropdown
        String[] poolType = new String[] {
                "Private",
                "Public"
        };

        ArrayAdapter<String> poolTypeAdapter = new ArrayAdapter<>(
                this,
                R.layout.gender_dropdown_item,
                poolType
        );

        poolTypeCTV.setAdapter(poolTypeAdapter);

        //pool swimmer dropdown
        String[] poolSwimmer = new String[] {
                "Kids",
                "Adult"
        };

        ArrayAdapter<String> poolSwimmerAdapter = new ArrayAdapter<>(
                this,
                R.layout.gender_dropdown_item,
                poolSwimmer
        );

        poolSwimmerCTV.setAdapter(poolSwimmerAdapter);

        postBtn.setOnClickListener(view -> {
            _loading.show();
            String _poolNumber = Objects.requireNonNull(poolNumberET.getText()).toString();
            String _poolSize = Objects.requireNonNull(poolSizeET.getText()).toString();
            String _poolType = poolTypeCTV.getText().toString();
            String _poolSwimmer = poolSwimmerCTV.getText().toString();
            String _poolDesc = Objects.requireNonNull(descriptionET.getText()).toString();

            if(TextUtils.isEmpty(_poolNumber)) {
                Toast.makeText(this, "Pool number is required!", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(_poolSize)) {
                Toast.makeText(this, "Pool size is required!", Toast.LENGTH_SHORT).show();
            }else if(_poolType.equals("Select pool type")) {
                Toast.makeText(this, "Please select pool type!", Toast.LENGTH_SHORT).show();
            }else if(_poolSwimmer.equals("Select Swimmer")) {
                Toast.makeText(this, "Please select swimmer!", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(_poolDesc)) {
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
            Intent gotoManageRooms = new Intent(this, ManagePool.class);
            startActivity(gotoManageRooms);
            finish();
        });

    }

    private void _init() {
        _title = findViewById(R.id.title);
        poolNumberET = findViewById(R.id.poolNumberET);
        poolSizeET = findViewById(R.id.poolSizeET);
        descriptionET = findViewById(R.id.descriptionET);
        poolTypeCTV = findViewById(R.id.poolTypeCTV);
        poolSwimmerCTV = findViewById(R.id.poolSwimmerCTV);
        poolPhoto = findViewById(R.id.poolPhoto);
        backBtn = findViewById(R.id.backBtn);
        openFile = findViewById(R.id.openFile);
        postBtn = findViewById(R.id.postBtn);
    }

    private void updateFile() {
        if (_imgURI != null || poolPhoto.getDrawable() != null) {
            if (_imgURI != null) {
                StorageReference resortRef = poolPhotoRef.child(updatePoolPicID).child(updatePoolPicName + "." + getFileExtension(_imgURI));
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
                new ConvertDrawableToUriTask().execute(poolPhoto.getDrawable());
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
                StorageReference resortRef = poolPhotoRef.child(updatePoolPicName + "." + getFileExtension(Uri.parse(myUri)));
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
                            Toast.makeText(AddPoolPage.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                            _loading.dismiss();
                        }
                    } else {
                        // Handle the case when the upload task is not successful
                        Toast.makeText(AddPoolPage.this, "Upload failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        _loading.dismiss();
                    }
                });
            } else {
                // Handle the case when Uri conversion fails.
                Toast.makeText(AddPoolPage.this, "Failed to convert drawable to Uri", Toast.LENGTH_SHORT).show();
                _loading.dismiss();
            }
        }
    }

    private String saveBitmapAndGetUri(Bitmap bitmap) {
        // You need to implement a method to save the Bitmap as a file and return the Uri.
        // Example code for saving a Bitmap to a file:
        File imageFile = new File(getExternalCacheDir(), updatePoolPicName + ".png");
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
        String _poolNumber = Objects.requireNonNull(poolNumberET.getText()).toString();
        String _poolSize = Objects.requireNonNull(poolSizeET.getText()).toString();
        String _poolType = poolTypeCTV.getText().toString();
        String _poolSwimmer = poolSwimmerCTV.getText().toString();
        String _poolDesc = Objects.requireNonNull(descriptionET.getText()).toString();

        Map<String, Object> updateValue = new HashMap<>();
        updateValue.put("POOL_NO", Integer.parseInt(_poolNumber));
        updateValue.put("POOL_SIZE", Double.parseDouble(_poolSize));
        updateValue.put("POOL_TYPE", _poolType);
        updateValue.put("POOL_INFO", _poolSwimmer);
        updateValue.put("POOL_IMG_URL", myUri);
        updateValue.put("POOL_DESC", _poolDesc);

        resortCollectionRef.document(updateResortID)
                .collection("POOL").document(updatePoolID)
                .update(updateValue)
                .addOnSuccessListener(unused -> {
                    _loading.dismiss();
                    Toast.makeText(this, "Pool " + _poolNumber + " updated successfully.", Toast.LENGTH_SHORT).show();
                    Intent gotoManageRooms = new Intent(this, ManagePool.class);
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
            poolPicID = newDocRef.getId();
            poolPicName = String.valueOf(System.currentTimeMillis());
            StorageReference resortRef = poolPhotoRef.child(poolPicID).child(poolPicName + "." + getFileExtension(_imgURI));

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
                            Toast.makeText(AddPoolPage.this, "Upload failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(AddPoolPage.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "No file selected!", Toast.LENGTH_SHORT).show();
        }
    }
    private void uploadAll(String myUri) {
        String _poolNumber = Objects.requireNonNull(poolNumberET.getText()).toString();
        String _poolSize = Objects.requireNonNull(poolSizeET.getText()).toString();
        String _poolType = poolTypeCTV.getText().toString();
        String _poolSwimmer = poolSwimmerCTV.getText().toString();
        String _poolDesc = Objects.requireNonNull(descriptionET.getText()).toString();
        // Add data to Firestore with an auto-generated document ID
        DocumentReference newDocRef = FirebaseFirestore.getInstance().collection("RESORTS").document();
        _poolID = newDocRef.getId();

        resortCollectionRef.whereEqualTo("OWNER_UID", _user.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for(DocumentSnapshot doc : queryDocumentSnapshots) {
                        String resort_id = doc.getString("RESORT_ID");

                        Map<String, Object> poolData = new HashMap<>();
                        poolData.put("OWNER_UID", _user.getUid());
                        poolData.put("RESORT_ID", resort_id);
                        poolData.put("POOL_ID", _poolID);
                        poolData.put("POOL_PIC_ID", poolPicID);
                        poolData.put("POOL_PIC_NAME", poolPicName);
                        poolData.put("POOL_NO", Integer.parseInt(_poolNumber));
                        poolData.put("POOL_SIZE", Double.parseDouble(_poolSize));
                        poolData.put("POOL_TYPE", _poolType);
                        poolData.put("POOL_INFO", _poolSwimmer);
                        poolData.put("POOL_IMG_URL", myUri);
                        poolData.put("POOL_DESC", _poolDesc);

                        assert resort_id != null;
                        resortCollectionRef.document(resort_id)
                                        .collection("POOL").document(_poolID)
                                        .set(poolData).addOnSuccessListener(queryDocumentSnapshots1 -> {
                                    _loading.dismiss();
                                    Intent gotoManagePool = new Intent(this, ManagePool.class);
                                    startActivity(gotoManagePool);
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

            Picasso.get().load(_imgURI).into(poolPhoto);
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