package com.sprtcoding.tourizal.UserInformation;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.constant.ImageProvider;
import com.google.android.gms.tasks.Continuation;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sprtcoding.tourizal.AdminDashboardPage;
import com.sprtcoding.tourizal.AdminPost.ResortPost.AddCottagePage;
import com.sprtcoding.tourizal.R;
import com.sprtcoding.tourizal.UserDashBoard;
import com.sprtcoding.tourizal.Utility.NetworkChangeListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public class UserBasicInformation extends AppCompatActivity {
    private AutoCompleteTextView _genderCTV;
    private MaterialButton _dateBtn, _saveBtn;
    private TextInputEditText _ageET;
    private CircleImageView _profilePic;
    private ImageView _openGallery;
    private RelativeLayout profileRL;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDb;
    private DatabaseReference userRef;
    private StorageReference profilePhotoRef;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Uri imageUri;
    private String profilePicID, profilePicName, accountType;
    private boolean hasProfile = false;
    ProgressDialog loading;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_basic_information);
        _var();

        loading = new ProgressDialog(this);
        loading.setTitle("Loading");
        loading.setMessage("Please wait...");

        //firebase authentication instance
        mAuth = FirebaseAuth.getInstance();
        //firebase database and reference
        mDb = FirebaseDatabase.getInstance();
        userRef = mDb.getReference("Users");
        profilePhotoRef = FirebaseStorage.getInstance().getReference("ProfilePhotos/");

        userRef.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    if(snapshot.hasChild("PhotoURL")) {
                        profileRL.setVisibility(View.GONE);
                        hasProfile = true;
                    }else {
                        profileRL.setVisibility(View.VISIBLE);
                        hasProfile = false;
                    }
                    if(snapshot.hasChild("AccountType")) {
                        accountType = snapshot.child("AccountType").getValue(String.class);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserBasicInformation.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //gender dropdown
        String[] gender = new String[] {
                "Male",
                "Female"
        };

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                this,
                R.layout.gender_dropdown_item,
                gender
        );

        _genderCTV.setAdapter(genderAdapter);//end of gender dropdown

        //date picker
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.clear();

        long today = MaterialDatePicker.todayInUtcMilliseconds();

//        CalendarConstraints.Builder calendarConstraintBuilder = new CalendarConstraints.Builder();
//        calendarConstraintBuilder.setValidator(DateValidatorPointForward.now());

        MaterialDatePicker.Builder datePickerBuilder = MaterialDatePicker.Builder.datePicker();
        datePickerBuilder.setTitleText("Select Date of Birth");
        datePickerBuilder.setSelection(today);
//        datePickerBuilder.setCalendarConstraints(calendarConstraintBuilder.build());
        final MaterialDatePicker materialDatePicker = datePickerBuilder.build();

        materialDatePicker.addOnPositiveButtonClickListener(selection -> _dateBtn.setText(materialDatePicker.getHeaderText()));

        _dateBtn.setOnClickListener(view -> {
            materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        });

        _saveBtn.setOnClickListener(view -> {
            loading.show();
            String _age = _ageET.getText().toString();
            String _gender = _genderCTV.getText().toString();
            String _bod = _dateBtn.getText().toString();

            if(hasProfile) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("Age", _age);
                map.put("Gender", _gender);
                map.put("DateOfBirth", _bod);

                FirebaseUser _user = mAuth.getCurrentUser();

                userRef.child(_user.getUid()).updateChildren(map).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        if(accountType.equals("User")) {
                            loading.dismiss();
                            Intent gotoDashboard = new Intent(UserBasicInformation.this, UserDashBoard.class);
                            startActivity(gotoDashboard);
                            finish();
                        }else if(accountType.equals("Admin")) {
                            loading.dismiss();
                            Intent gotoDashboard = new Intent(UserBasicInformation.this, AdminDashboardPage.class);
                            startActivity(gotoDashboard);
                            finish();
                        }

                    }else {
                        loading.dismiss();
                        Toast.makeText(UserBasicInformation.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                if(TextUtils.isEmpty(_age)) {
                    Toast.makeText(UserBasicInformation.this, "Age cannot be empty!", Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }else if(imageUri == null) {
                    Toast.makeText(UserBasicInformation.this, "Please select your profile picture!", Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }else if(TextUtils.isEmpty(_gender)) {
                    Toast.makeText(UserBasicInformation.this, "Please select your gender!", Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }else if(TextUtils.isEmpty(_bod)) {
                    Toast.makeText(UserBasicInformation.this, "Please select your Birthdate!", Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }else {
                    uploadFile();
                }
            }

        });

        _openGallery.setOnClickListener(view -> {
            ImagePicker.Companion.with(this)
                    .crop()
                    .cropOval()
                    .maxResultSize(512,512,true)
                    .provider(ImageProvider.BOTH) //Or bothCameraGallery()
                    .createIntentFromDialog((Function1)(new Function1(){
                        public Object invoke(Object var1){
                            this.invoke((Intent)var1);
                            return Unit.INSTANCE;
                        }

                        public final void invoke(@NotNull Intent it){
                            Intrinsics.checkNotNullParameter(it,"it");
                            launcher.launch(it);
                        }
                    }));
        });
    }

    private void _var() {
        _genderCTV = findViewById(R.id.genderCTV);
        _dateBtn = findViewById(R.id.dateBtn);
        _saveBtn = findViewById(R.id.saveBtn);
        _ageET = findViewById(R.id.ageET);
        _profilePic = findViewById(R.id.profilePic);
        _openGallery = findViewById(R.id.openGallery);
        profileRL = findViewById(R.id.profileRL);
    }

    private void uploadFile() {
        if(imageUri != null) {
            profilePicID = userRef.push().getKey();
            assert profilePicID != null;
            profilePicName = String.valueOf(System.currentTimeMillis());
            StorageReference resortRef = profilePhotoRef.child(profilePicID).child( profilePicName + "." + getFileExtension(imageUri));
            resortRef.putFile(imageUri).addOnCompleteListener(task -> {
                UploadTask uploadTask =resortRef.putFile(imageUri);

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
            }).addOnFailureListener(e -> Toast.makeText(UserBasicInformation.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void uploadAll(String myUri) {
        String _age = _ageET.getText().toString();
        String _gender = _genderCTV.getText().toString();
        String _bod = _dateBtn.getText().toString();

        if(!hasProfile) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("Age", _age);
            map.put("Gender", _gender);
            map.put("DateOfBirth", _bod);
            map.put("PhotoURL", myUri);
            map.put("PhotoID", profilePicID);
            map.put("PhotoName", profilePicName);

            FirebaseUser _user = mAuth.getCurrentUser();

            userRef.child(_user.getUid()).updateChildren(map).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    if(accountType.equals("User")) {
                        loading.dismiss();
                        Intent gotoDashboard = new Intent(UserBasicInformation.this, UserDashBoard.class);
                        startActivity(gotoDashboard);
                        finish();
                    }else if(accountType.equals("Admin")) {
                        loading.dismiss();
                        Intent gotoDashboard = new Intent(UserBasicInformation.this, AdminDashboardPage.class);
                        startActivity(gotoDashboard);
                        finish();
                    }
                }else {
                    loading.dismiss();
                    Toast.makeText(UserBasicInformation.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    ActivityResultLauncher<Intent> launcher=
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),(ActivityResult result)->{
                if(result.getResultCode()==RESULT_OK){
                    imageUri=result.getData().getData();
                    // Use the uri to load the image
                    Picasso.get().load(imageUri).into(_profilePic);
                }else if(result.getResultCode()==ImagePicker.RESULT_ERROR){
                    // Use ImagePicker.Companion.getError(result.getData()) to show an error
                    Toast.makeText(this, ImagePicker.Companion.getError(result.getData()), Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, intentFilter);
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}