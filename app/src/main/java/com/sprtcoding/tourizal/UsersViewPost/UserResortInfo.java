package com.sprtcoding.tourizal.UsersViewPost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sprtcoding.tourizal.Adapter.FireStoreAdapter.AmenitiesCottageAdapter;
import com.sprtcoding.tourizal.Adapter.FireStoreAdapter.AmenitiesCottagePagerAdapter;
import com.sprtcoding.tourizal.Adapter.FireStoreAdapter.AmenitiesKayakPagerAdapter;
import com.sprtcoding.tourizal.Adapter.FireStoreAdapter.AmenitiesKayakinAdapter;
import com.sprtcoding.tourizal.Adapter.FireStoreAdapter.AmenitiesPoolAdapter;
import com.sprtcoding.tourizal.Adapter.FireStoreAdapter.AmenitiesPoolPagerAdapter;
import com.sprtcoding.tourizal.Adapter.FireStoreAdapter.AmenitiesRoomPagerAdapter;
import com.sprtcoding.tourizal.Adapter.FireStoreAdapter.AmenitiesRoomsAdapter;
import com.sprtcoding.tourizal.Adapter.FireStoreAdapter.CommentListAdapter;
import com.sprtcoding.tourizal.Adapter.FireStoreAdapter.PoolAdapterFS;
import com.sprtcoding.tourizal.Adapter.UserFeaturedViewPagerAdapter;
import com.sprtcoding.tourizal.FireStoreDB.DBQuery;
import com.sprtcoding.tourizal.Model.CottageModel;
import com.sprtcoding.tourizal.Model.FSModel.CottageModelFS;
import com.sprtcoding.tourizal.Model.FSModel.FeaturedModelFS;
import com.sprtcoding.tourizal.Model.FSModel.KayakinModel;
import com.sprtcoding.tourizal.Model.FSModel.PoolModel;
import com.sprtcoding.tourizal.Model.FSModel.RatingsList;
import com.sprtcoding.tourizal.Model.FSModel.RoomFSModel;
import com.sprtcoding.tourizal.Model.FeaturedPostModel;
import com.sprtcoding.tourizal.Model.RoomsModel;
import com.sprtcoding.tourizal.R;
import com.sprtcoding.tourizal.StreetView.KuulaStreetView;
import com.sprtcoding.tourizal.Utility.NetworkChangeListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class UserResortInfo extends AppCompatActivity {
    private ImageView _resortPic, backBtn, _tour_view_btn;
    private TextView _resortName, _ratingValue, _totalRatingNumber, _seeComment, _addRating,
            _no_updates_available, _no_rooms_available, _no_cottage_available, _closeRatingDialogBtn, featured_title
            , owner_name, resort_fee, addRatingTxt;
    private RatingBar _ratingBar, _addRatingValue;
    private MaterialButton _submitRatingBtn;
    private TextInputEditText _reviewET;
    private LinearLayout _featuredLayout, _roomVP_LL, _cottageVP_LL, _poolVP_LL, _kayakVP_LL;
    private RelativeLayout _updateLL, _roomsLL, _cottageLL, _poolLL, _kayakinLL;
    private ViewPager featuredViewPager, roomVP, cottageVP, poolVP, kayakVP;
    private TabLayout tabs, roomTabs, cottageTabs, poolTabs, kayakTabs;
    FirebaseDatabase mDb;
    DatabaseReference userRef;
    FirebaseFirestore DB;
    CollectionReference resortColRef;
    private FirebaseAuth mAuth;
    UserFeaturedViewPagerAdapter userFeaturedViewPagerAdapter;
    AmenitiesCottagePagerAdapter amenitiesCottagePagerAdapter;
    AmenitiesPoolPagerAdapter amenitiesPoolPagerAdapter;
    AmenitiesKayakPagerAdapter amenitiesKayakPagerAdapter;
    AmenitiesRoomPagerAdapter amenitiesRoomPagerAdapter;
    CommentListAdapter commentListAdapter;
    List<FeaturedPostModel> featuredPostModels;
    List<RoomsModel> roomsModels;
    List<CottageModel> cottageModels;
    List<RoomFSModel> roomFSModels;
    List<CottageModelFS> cottageModelFS;
    List<FeaturedModelFS> featuredModelFS;
    List<RatingsList> ratingsLists;
    List<PoolModel> poolModelList;
    List<KayakinModel> kayakinModelList;
    ArrayList<String> imageUrls, roomIds, roomNames;
    Intent getExtraIntent;
    String OwnerID, ResortID, userName, featuredTitle;
    ProgressDialog _ratingLoading;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_resort_info);
        _var();

        featuredPostModels = new ArrayList<>();
        roomsModels = new ArrayList<>();
        cottageModels = new ArrayList<>();
        ratingsLists = new ArrayList<>();
        poolModelList = new ArrayList<>();
        kayakinModelList = new ArrayList<>();

        //fs
        roomFSModels = new ArrayList<>();
        cottageModelFS = new ArrayList<>();
        featuredModelFS = new ArrayList<>();

        DBQuery.g_firestore = FirebaseFirestore.getInstance();

        DB = FirebaseFirestore.getInstance();
        resortColRef = DB.collection("RESORTS");

        _ratingLoading = new ProgressDialog(this);
        _ratingLoading.setTitle("Submit Rating");
        _ratingLoading.setMessage("Please wait...");

        tabs.setupWithViewPager(featuredViewPager);
        featuredViewPager.setClipToPadding(false);
        featuredViewPager.setClipChildren(false);
        featuredViewPager.setPageMargin(30);

        getExtraIntent = getIntent();
        OwnerID = getExtraIntent.getStringExtra("UID");
        ResortID = getExtraIntent.getStringExtra("ResortID");

        featuredTitle = getExtraIntent.getStringExtra("Title");

        if(featuredTitle != null) {
            featured_title.setVisibility(View.VISIBLE);
            featured_title.setText(featuredTitle);
        }

        mDb = FirebaseDatabase.getInstance();
        userRef = mDb.getReference("Users");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser _user = mAuth.getCurrentUser();

        if(_user != null) {
            userRef.child(_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        userName = snapshot.child("Fullname").getValue(String.class);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(UserResortInfo.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        resortColRef.document(ResortID).get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()) {
                String resortPic = documentSnapshot.getString("RESORT_PIC_URL");
                String resortName = documentSnapshot.getString("RESORT_NAME");
                String resortFee = documentSnapshot.getString("RESORT_ENTRANCE_FEE");
                String owner_names = documentSnapshot.getString("OWNER_NAME");

                double fees = Double.parseDouble(resortFee);

                Picasso.get().load(resortPic).into(_resortPic);
                _resortName.setText(resortName);
                owner_name.setText("Owned by: " + owner_names);
                resort_fee.setText("Entrance: " + convertToPhilippinePesoD(fees));
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });

        _tour_view_btn.setOnClickListener(view -> {
            Intent i = new Intent(this, KuulaStreetView.class);
            i.putExtra("resort_name", _resortName.getText().toString().toLowerCase());
            startActivity(i);
        });

        //add rating dialog
        getRatings();
        checkIfYouRate();
        _addRating.setOnClickListener(view -> {
            if(_addRating.getText().toString().equals("Edit")) {
                viewRatingDialog("Edit Ratings");
            }else {
                viewRatingDialog("Add Ratings");
            }
        });
        _seeComment.setOnClickListener(view -> {
            viewCommentListDialog();
        });
        //end of add rating

        //amenities rooms
        getAmenitiesRooms();

        //amenities cottage
        getAmenitiesCottage();

        //amenities pool
        getAmenitiesPool();

        //amenities kayak
        getAmenitiesKayakin();

        //featured post or updates

        getFeaturedPost();

        backBtn.setOnClickListener(view -> {
            finish();
        });

    }

    private void getAmenitiesRooms() {
        roomTabs.setupWithViewPager(roomVP);
        roomVP.setClipToPadding(false);
        roomVP.setClipChildren(false);
        roomVP.setPageMargin(30);

        resortColRef.document(ResortID).collection("ROOMS")
                .addSnapshotListener((value, error) -> {
                    if(error == null && value != null) {
                        if(!value.isEmpty()) {
                            roomFSModels.clear();
                            for(QueryDocumentSnapshot doc : value) {
                                imageUrls = doc.contains("ROOM_PHOTO_URL")
                                        ? (ArrayList<String>) doc.get("ROOM_PHOTO_URL")
                                        : new ArrayList<>();

                                roomNames = doc.contains("ROOM_PIC_NAME")
                                        ? (ArrayList<String>) doc.get("ROOM_PIC_NAME")
                                        : new ArrayList<>();

                                roomIds = doc.contains("ROOM_PIC_ID")
                                        ? (ArrayList<String>) doc.get("ROOM_PIC_ID")
                                        : new ArrayList<>();

                                roomFSModels.add(new RoomFSModel(
                                        doc.getString("OWNER_UID"),
                                        doc.getString("RESORT_ID"),
                                        doc.getString("ROOM_ID"),
                                        doc.getString("ROOM_PIC_ID"),
                                        doc.getString("ROOM_PIC_NAME"),
                                        doc.getString("DESCRIPTION"),
                                        imageUrls,
                                        doc.getLong("ROOM_NO").intValue(),
                                        doc.getLong("MAX_DAY").intValue(),
                                        doc.getLong("MAX_NIGHT").intValue(),
                                        doc.getLong("NIGHT_PRICE").intValue(),
                                        doc.getLong("NIGHT_PRICE").intValue(),
                                        doc.getLong("DAY_EXCESS").intValue(),
                                        doc.getLong("NIGHT_EXCESS").intValue()
                                ));
                            }
                            amenitiesRoomPagerAdapter = new AmenitiesRoomPagerAdapter(this, roomFSModels, this, getSupportFragmentManager());
                            roomVP.setAdapter(amenitiesRoomPagerAdapter);
                        } else {
                            _no_rooms_available.setVisibility(View.VISIBLE);
                            _roomVP_LL.setVisibility(View.GONE);
                            //_rooms_rv.setVisibility(View.GONE);
                            _roomsLL.setVisibility(View.GONE);
                        }
                    }else {
                        Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getAmenitiesCottage() {
        cottageTabs.setupWithViewPager(cottageVP);
        cottageVP.setClipToPadding(false);
        cottageVP.setClipChildren(false);
        cottageVP.setPageMargin(30);

        resortColRef.document(ResortID).collection("COTTAGE")
                .addSnapshotListener((value, error) -> {
                    if(error == null && value != null) {
                        if(!value.isEmpty()) {
                            cottageModelFS.clear();
                            for(QueryDocumentSnapshot doc : value) {
                                cottageModelFS.add(new CottageModelFS(
                                        doc.getString("OWNER_UID"),
                                        doc.getString("RESORT_ID"),
                                        doc.getString("COTTAGE_ID"),
                                        doc.getString("COTTAGE_PIC_ID"),
                                        doc.getString("COTTAGE_PIC_NAME"),
                                        doc.getLong("COTTAGE_NO").intValue(),
                                        doc.getString("DESCRIPTION"),
                                        doc.getString("COTTAGE_PHOTO_URL"),
                                        doc.getLong("PRICE").intValue()
                                ));
                            }
                            amenitiesCottagePagerAdapter = new AmenitiesCottagePagerAdapter(UserResortInfo.this, cottageModelFS, getSupportFragmentManager());
                            cottageVP.setAdapter(amenitiesCottagePagerAdapter);
                        } else {
                            _no_cottage_available.setVisibility(View.VISIBLE);
                            _cottageVP_LL.setVisibility(View.GONE);
                            _cottageLL.setVisibility(View.GONE);
                        }
                    }else {
                        Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getAmenitiesPool() {
        poolTabs.setupWithViewPager(poolVP);
        poolVP.setClipToPadding(false);
        poolVP.setClipChildren(false);
        poolVP.setPageMargin(30);

        resortColRef.document(ResortID).collection("POOL")
                .addSnapshotListener((value, error) -> {
                    if(error == null && value != null) {
                        if(!value.isEmpty()) {
                            poolModelList.clear();
                            for(QueryDocumentSnapshot doc : value) {
                                poolModelList.add(new PoolModel(
                                        doc.getString("POOL_IMG_URL"),
                                        doc.getString("POOL_TYPE"),
                                        doc.getString("POOL_INFO"),
                                        doc.getString("POOL_DESC"),
                                        doc.getString("POOL_PIC_ID"),
                                        doc.getString("POOL_PIC_NAME"),
                                        doc.getString("OWNER_UID"),
                                        doc.getString("RESORT_ID"),
                                        doc.getString("POOL_ID"),
                                        doc.getDouble("POOL_SIZE"),
                                        doc.getLong("POOL_NO").intValue()
                                ));
                            }
                            amenitiesPoolPagerAdapter = new AmenitiesPoolPagerAdapter(this, poolModelList);
                            poolVP.setAdapter(amenitiesPoolPagerAdapter);
                        } else {
                            _poolVP_LL.setVisibility(View.GONE);
                            _poolLL.setVisibility(View.GONE);
                        }
                    }else {
                        Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getAmenitiesKayakin() {
        kayakTabs.setupWithViewPager(kayakVP);
        kayakVP.setClipToPadding(false);
        kayakVP.setClipChildren(false);
        kayakVP.setPageMargin(30);

        resortColRef.document(ResortID).collection("KAYAKIN")
                .addSnapshotListener((value, error) -> {
                    if(error == null && value != null) {
                        if(!value.isEmpty()) {
                            kayakinModelList.clear();
                            for(QueryDocumentSnapshot doc : value) {
                                kayakinModelList.add(new KayakinModel(
                                        doc.getString("OWNER_UID"),
                                        doc.getString("RESORT_UID"),
                                        doc.getString("KAYAKIN_UID"),
                                        doc.getString("KAYAKIN_PIC_ID"),
                                        doc.getString("KAYAKIN_PIC_NAME"),
                                        doc.getString("KAYAKIN_IMG_URL"),
                                        doc.getString("KAYAKIN_DESC"),
                                        doc.getLong("KAYAKIN_NO").intValue(),
                                        doc.getLong("RENT_PRICE").floatValue()
                                ));
                            }
                            amenitiesKayakPagerAdapter = new AmenitiesKayakPagerAdapter(this, kayakinModelList);
                            kayakVP.setAdapter(amenitiesKayakPagerAdapter);
                        } else {
                            _kayakVP_LL.setVisibility(View.GONE);
                            _kayakinLL.setVisibility(View.GONE);
                        }
                    }else {
                        Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getFeaturedPost() {
        resortColRef.document(ResortID).collection("FEATURED")
                .addSnapshotListener((value, error) -> {
                    if(error == null && value != null) {
                        if(!value.isEmpty()) {
                            featuredModelFS.clear();
                            for(QueryDocumentSnapshot doc : value) {
                                featuredModelFS.add(new FeaturedModelFS(
                                        doc.getString("OWNER_UID"),
                                        doc.getString("RESORT_ID"),
                                        doc.getString("FEATURED_ID"),
                                        doc.getString("FEATURED_TITLE"),
                                        doc.getString("FEATURED_PHOTO_ID"),
                                        doc.getString("FEATURED_PHOTO_NAME"),
                                        doc.getString("RESORT_NAME"),
                                        doc.getString("OWNER_NAME"),
                                        doc.getString("LOCATION"),
                                        doc.getString("LAT"),
                                        doc.getString("LNG"),
                                        doc.getString("TIME"),
                                        doc.getString("DATE"),
                                        doc.getString("FEATURED_PHOTO_URL")
                                ));
                            }
                            userFeaturedViewPagerAdapter = new UserFeaturedViewPagerAdapter(UserResortInfo.this, featuredModelFS);
                            featuredViewPager.setAdapter(userFeaturedViewPagerAdapter);
                            autoImageSlide();
                        } else {
                            _no_updates_available.setVisibility(View.VISIBLE);
                            _featuredLayout.setVisibility(View.GONE);
                            _updateLL.setVisibility(View.GONE);
                        }
                    }else {
                        Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void checkIfYouRate() {
        CollectionReference resortColRef = DB.collection("RATINGS");

        resortColRef.whereEqualTo("RESORT_ID", ResortID)
                .whereEqualTo("USER_ID", mAuth.getCurrentUser().getUid())
                .addSnapshotListener((value, error) -> {
                    if(error == null && value != null) {
                        if(!value.isEmpty()) {
                            _addRating.setText("Edit");
                        }else {
                            _addRating.setText("Rate");
                        }
                    }else {
                        Toast.makeText(this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private float ratingSum = 0;

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void getRatings() {
        CollectionReference resortColRef = DB.collection("RATINGS");
        DecimalFormat decimalFormat = new DecimalFormat("#.#");

        resortColRef.whereEqualTo("RESORT_ID", ResortID)
                        .addSnapshotListener((value, error) -> {
                            if(error == null && value != null) {
                                if(!value.isEmpty()) {
                                    ratingSum = 0;
                                    for(QueryDocumentSnapshot doc : value) {
                                        Double rating = doc.getDouble("RATINGS");
                                        ratingSum += rating;
                                    }
                                    long numberOfReviews = value.size();
                                    float avgRating = ratingSum/numberOfReviews;

                                    _ratingBar.setRating(avgRating);
                                    _ratingValue.setText(String.format("%.1f", avgRating));
                                    _totalRatingNumber.setText("/"+numberOfReviews);
                                    _seeComment.setText(numberOfReviews + " Comment/s");
                                }
                            } else {
                                Toast.makeText(this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
    }

    private void viewRatingDialog(String status) {
        View ratingAlertDialog = LayoutInflater.from(UserResortInfo.this).inflate(R.layout.add_rating_dialog, null);
        AlertDialog.Builder ratingAlertDialogBuilder = new AlertDialog.Builder(UserResortInfo.this);

        ratingAlertDialogBuilder.setView(ratingAlertDialog);

        _closeRatingDialogBtn = ratingAlertDialog.findViewById(R.id.closeRatingDialogBtn);
        _reviewET = ratingAlertDialog.findViewById(R.id.reviewET);
        _addRatingValue = ratingAlertDialog.findViewById(R.id.addRatingValue);
        _submitRatingBtn = ratingAlertDialog.findViewById(R.id.submitRatingBtn);
        addRatingTxt = ratingAlertDialog.findViewById(R.id.addRatingTxt);

        CollectionReference resortColRef = DB.collection("RATINGS");

        if(status.equals("Edit Ratings")) {
            resortColRef.whereEqualTo("RESORT_ID", ResortID)
                    .whereEqualTo("USER_ID", mAuth.getCurrentUser().getUid())
                    .addSnapshotListener((value, error) -> {
                        if(error == null && value != null) {
                            if(!value.isEmpty()) {
                                ratingSum = 0;
                                for(QueryDocumentSnapshot doc : value) {
                                    float rating = doc.getLong("RATINGS");
                                    String comments = doc.getString("COMMENTS");
                                    _reviewET.setText(comments);
                                    _addRatingValue.setRating(rating);
                                }
                            }
                        } else {
                            Toast.makeText(this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        final AlertDialog ratingDialog = ratingAlertDialogBuilder.create();

        Objects.requireNonNull(ratingDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ratingDialog.setCanceledOnTouchOutside(false);

        addRatingTxt.setText(status);

        _closeRatingDialogBtn.setOnClickListener(view -> {
            ratingDialog.cancel();
        });

        Date dateTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String date = dateFormat.format(dateTime);
        String time = timeFormat.format(dateTime);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        _submitRatingBtn.setOnClickListener(view1 -> {
            _ratingLoading.show();

            if(!TextUtils.isEmpty(_reviewET.getText().toString()) || _addRatingValue.getRating() != 0) {

                CollectionReference ratingCol = DB.collection("RATINGS");

                if(status.equals("Edit Ratings")) {
                    Map<String, Object> mapUpdate = new HashMap<>();
                    mapUpdate.put("DATE_POSTED", date);
                    mapUpdate.put("TIME_POSTED", time);
                    mapUpdate.put("RATINGS", _addRatingValue.getRating());
                    mapUpdate.put("COMMENTS", _reviewET.getText().toString());

                    ratingCol.whereEqualTo("RESORT_ID", ResortID)
                            .whereEqualTo("USER_ID", mAuth.getCurrentUser().getUid())
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                if(!queryDocumentSnapshots.isEmpty()) {
                                    for(QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                        String id = doc.getId();

                                        ratingCol.document(id)
                                                .update(mapUpdate)
                                                .addOnSuccessListener(queryDocumentSnapshots1 -> {
                                                    Toast.makeText(UserResortInfo.this, "Rating updated successfully!", Toast.LENGTH_SHORT).show();
                                                    _ratingLoading.dismiss();
                                                    ratingDialog.cancel();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    _ratingLoading.dismiss();
                                                    ratingDialog.cancel();
                                                });
                                    }
                                }
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show());
                }else {
                    Map<String, Object> map = new HashMap<>();
                    map.put("RESORT_ID", ResortID);
                    map.put("USER_ID", mAuth.getCurrentUser().getUid());
                    map.put("OWNER_ID", OwnerID);
                    map.put("DATE_POSTED", date);
                    map.put("TIME_POSTED", time);
                    map.put("NAME_OF_USER", userName);
                    map.put("RATINGS", _addRatingValue.getRating());
                    map.put("COMMENTS", _reviewET.getText().toString());

                    ratingCol.add(map).addOnSuccessListener(documentReference -> {
                        Toast.makeText(UserResortInfo.this, "Rating submitted successfully!", Toast.LENGTH_SHORT).show();
                        _ratingLoading.dismiss();
                        ratingDialog.cancel();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        _ratingLoading.dismiss();
                        ratingDialog.cancel();
                    });
                }
            }else {
                Toast.makeText(this, "Please add some comment.", Toast.LENGTH_SHORT).show();
                _reviewET.requestFocus();
                _ratingLoading.dismiss();
            }
        });

        ratingDialog.show();
    }

    private void viewCommentListDialog() {
        View commentAlertDialog = LayoutInflater.from(UserResortInfo.this).inflate(R.layout.comment_list_dialog, null);
        AlertDialog.Builder commentAlertDialogBuilder = new AlertDialog.Builder(UserResortInfo.this);

        commentAlertDialogBuilder.setView(commentAlertDialog);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView closeBtn = commentAlertDialog.findViewById(R.id.close_btn);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView resortName = commentAlertDialog.findViewById(R.id.resort_name);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) RatingBar resortRating = commentAlertDialog.findViewById(R.id.ratingBar);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView resortPic = commentAlertDialog.findViewById(R.id.resort_pic);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) RecyclerView comment_rv = commentAlertDialog.findViewById(R.id.comments_rv);

        LinearLayoutManager llmRooms = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        llmRooms.setReverseLayout(true);
        llmRooms.setStackFromEnd(true);
        comment_rv.setHasFixedSize(true);
        comment_rv.setLayoutManager(llmRooms);

        final AlertDialog commentDialog = commentAlertDialogBuilder.create();

        CollectionReference resortCol = DB.collection("RESORTS");
        resortCol.document(ResortID).addSnapshotListener((value, error) -> {
            if(error == null && value != null) {
                if(value.exists()) {
                    String rName = value.getString("RESORT_NAME");
                    String rPicUrl = value.getString("RESORT_PIC_URL");

                    resortName.setText(rName);
                    Picasso.get().load(rPicUrl).fit().placeholder(R.drawable.resort).into(resortPic);
                }else {
                    Toast.makeText(this, "No resort found.", Toast.LENGTH_SHORT).show();
                    commentDialog.dismiss();
                }
            } else {
                Toast.makeText(this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                commentDialog.dismiss();
            }
        });

        CollectionReference ratingCol = DB.collection("RATINGS");
        ratingCol.whereEqualTo("RESORT_ID", ResortID)
                        .addSnapshotListener((value, error) -> {
                            if(error == null && value != null) {
                                if(!value.isEmpty()) {
                                    ratingSum = 0;
                                    ratingsLists.clear();
                                    for(QueryDocumentSnapshot doc : value) {
                                        Double rating = doc.getDouble("RATINGS");
                                        ratingSum += rating;

                                        ratingsLists.add(new RatingsList(
                                                doc.getString("OWNER_ID"),
                                                doc.getString("USER_ID"),
                                                doc.getString("RESORT_ID"),
                                                doc.getString("TIME_POSTED"),
                                                doc.getString("DATE_POSTED"),
                                                doc.getString("NAME_OF_USER"),
                                                doc.getString("COMMENTS"),
                                                doc.getLong("RATINGS")
                                        ));

                                    }

                                    commentListAdapter = new CommentListAdapter(UserResortInfo.this, ratingsLists);
                                    comment_rv.setAdapter(commentListAdapter);

                                    long numberOfReviews = value.size();
                                    float avgRating = ratingSum/numberOfReviews;

                                    resortRating.setRating(avgRating);
                                }else {
                                    Toast.makeText(this, "No Comments yet.", Toast.LENGTH_SHORT).show();
                                    commentDialog.dismiss();
                                }
                            } else {
                                Toast.makeText(this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                                commentDialog.dismiss();
                            }
                        });

        Objects.requireNonNull(commentDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        commentDialog.setCanceledOnTouchOutside(false);

        closeBtn.setOnClickListener(view -> commentDialog.dismiss());

        commentDialog.show();
    }

    private void _var() {
        _roomVP_LL = findViewById(R.id.roomVP_LL);
        roomVP = findViewById(R.id.roomViewPager);
        roomTabs = findViewById(R.id.roomTabs);

        _cottageVP_LL = findViewById(R.id.cottageVP_LL);
        cottageVP = findViewById(R.id.cottageViewPager);
        cottageTabs = findViewById(R.id.cottageTabs);

        _poolVP_LL = findViewById(R.id.poolVP_LL);
        poolVP = findViewById(R.id.poolViewPager);
        poolTabs = findViewById(R.id.poolTabs);

        _kayakVP_LL = findViewById(R.id.kayakVP_LL);
        kayakVP = findViewById(R.id.kayakViewPager);
        kayakTabs = findViewById(R.id.kayakTabs);

        _poolLL = findViewById(R.id.poolLL);
        _kayakinLL = findViewById(R.id.kayakinLL);
        _tour_view_btn = findViewById(R.id.tour_view_btn);
        _resortPic = findViewById(R.id.resortPic);
        _resortName = findViewById(R.id.resortName);
        _ratingValue = findViewById(R.id.ratingValue);
        _seeComment = findViewById(R.id.seeComment);
        _addRating = findViewById(R.id.addRating);
        _ratingBar = findViewById(R.id.ratingBar);
        featuredViewPager = findViewById(R.id.featuredViewPager);
        tabs = findViewById(R.id.tabs);
        backBtn = findViewById(R.id.backBtn);
        _no_updates_available = findViewById(R.id.no_updates_available);
        _no_cottage_available = findViewById(R.id.no_cottage_available);
        _no_rooms_available = findViewById(R.id.no_rooms_available);
        _featuredLayout = findViewById(R.id.featuredLayout);
        _totalRatingNumber = findViewById(R.id.totalRatingNumber);
        featured_title = findViewById(R.id.featured_title);
        _updateLL = findViewById(R.id.updateLL);
        _roomsLL = findViewById(R.id.roomsLL);
        _cottageLL = findViewById(R.id.cottageLL);
        owner_name = findViewById(R.id.owner_name);
        resort_fee = findViewById(R.id.resort_fee);
    }

    private void autoImageSlide() {
        final long DELAY_MS = 5000;
        final long PERIOD_MS = 5000;

        final Handler handler = new Handler();
        final Runnable runnable = () -> {
            if(featuredViewPager.getCurrentItem() == featuredPostModels.size() - 1) {
                featuredViewPager.setCurrentItem(0);
            }else {
                featuredViewPager.setCurrentItem(featuredViewPager.getCurrentItem() + 1, true);
            }
        };

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(runnable);
            }
        },DELAY_MS,PERIOD_MS);
    }

    private String convertToPhilippinePesoD(double amount) {
        // Create a Locale for the Philippines
        Locale philippinesLocale = new Locale("en", "PH");

        // Create a NumberFormat instance for the Philippine Peso currency
        NumberFormat philippinePesoFormat = NumberFormat.getCurrencyInstance(philippinesLocale);

        // Set the currency code to PHP
        philippinePesoFormat.setCurrency(Currency.getInstance("PHP"));

        // Format the numeric amount to Philippine Peso format
        return philippinePesoFormat.format(amount);
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
}