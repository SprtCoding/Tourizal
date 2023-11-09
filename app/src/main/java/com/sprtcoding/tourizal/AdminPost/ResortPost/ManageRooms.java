package com.sprtcoding.tourizal.AdminPost.ResortPost;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.sprtcoding.tourizal.Adapter.FireStoreAdapter.RoomAdapterFS;
import com.sprtcoding.tourizal.Model.FSModel.RoomFSModel;
import com.sprtcoding.tourizal.Model.RoomsModel;
import com.sprtcoding.tourizal.R;
import com.sprtcoding.tourizal.Utility.NetworkChangeListener;

import java.util.ArrayList;
import java.util.List;

public class ManageRooms extends AppCompatActivity {
    private ImageView _backBtn;
    private RecyclerView _admin_rooms_rv;
    private LinearLayout _no_post_rooms_ll;
    private FloatingActionButton _add_rooms_btn;
    FirebaseAuth mAuth;
    FirebaseDatabase mDb;
    DatabaseReference roomsPostRef;
    List<RoomsModel> roomsModels;
    ProgressDialog _loading, _getDataLoading;
    FirebaseFirestore DB;
    CollectionReference resortCollectionRef;
    RoomAdapterFS roomAdapterFS;
    FirebaseUser _user;
    String resort_id;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_rooms);
        _var();

        roomsModels = new ArrayList<>();

        _loading = new ProgressDialog(this);
        _loading.setTitle("Loading");
        _loading.setMessage("Please wait...");

        _getDataLoading = new ProgressDialog(this);
        _getDataLoading.setMessage("Loading data...");
        _getDataLoading.show();

        DB = FirebaseFirestore.getInstance();
        resortCollectionRef = DB.collection("RESORTS");

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseDatabase.getInstance();
        roomsPostRef = mDb.getReference("Rooms");

        _user = mAuth.getCurrentUser();

        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        _admin_rooms_rv.setHasFixedSize(true);
        _admin_rooms_rv.setLayoutManager(llm);

        if(_user != null) {
            resortCollectionRef.whereEqualTo("OWNER_UID", _user.getUid())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for(DocumentSnapshot doc : queryDocumentSnapshots) {
                            resort_id = doc.getString("RESORT_ID");
                            if(resort_id != null) {
                                Query userQuery = resortCollectionRef.document(resort_id)
                                        .collection("ROOMS")
                                        .whereEqualTo("RESORT_ID", resort_id)
                                        .orderBy("ROOM_NO", Query.Direction.DESCENDING);

                                userQuery.get().addOnSuccessListener(queryDocumentSnapshots1 -> {
                                    if(!queryDocumentSnapshots1.isEmpty()) {
                                        _getDataLoading.dismiss();
                                        FirestoreRecyclerOptions<RoomFSModel> options = new FirestoreRecyclerOptions.Builder<RoomFSModel>()
                                                .setQuery(userQuery, RoomFSModel.class)
                                                .build();

                                        roomAdapterFS = new RoomAdapterFS(options);
                                        _admin_rooms_rv.setAdapter(roomAdapterFS);

                                        roomAdapterFS.startListening();
                                    }else {
                                        // Log a message here
                                        Log.d("Debug", "No data found in ROOMS collection");
                                        _getDataLoading.dismiss();
                                        _admin_rooms_rv.setVisibility(View.GONE);
                                        _no_post_rooms_ll.setVisibility(View.VISIBLE);
                                    }
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });

                            }else {
                                _getDataLoading.dismiss();
                                Toast.makeText(this, "No Resort Found! Please add one.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(e -> {
                        _getDataLoading.dismiss();
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }

//        roomsPostRef.child(_user.getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()) {
//                    roomsModels.clear();
//                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                        RoomsModel rooms = dataSnapshot.getValue(RoomsModel.class);
//                        roomsModels.add(rooms);
//                    }
//                    adminRoomsAdapter = new AdminRoomsAdapter(ManageRooms.this, roomsModels);
//                    _admin_rooms_rv.setAdapter(adminRoomsAdapter);
//                }else {
//                    _admin_rooms_rv.setVisibility(View.GONE);
//                    _no_post_rooms_ll.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(ManageRooms.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });

        _add_rooms_btn.setOnClickListener(view -> {
            _loading.show();
            Handler handler = new Handler();
            Runnable runnable = () -> {
                _loading.dismiss();
                Intent gotoAddResort = new Intent(ManageRooms.this, AddRoomsPage.class);
                startActivity(gotoAddResort);
                finish();
            };
            handler.postDelayed(runnable, 2000);
        });

        _backBtn.setOnClickListener(view -> {
            finish();
        });
    }

    private void _var() {
        _backBtn = findViewById(R.id.backBtn);
        _admin_rooms_rv = findViewById(R.id.admin_rooms_rv);
        _no_post_rooms_ll = findViewById(R.id.no_post_rooms_ll);
        _add_rooms_btn = findViewById(R.id.add_rooms_btn);
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
        if(roomAdapterFS != null) {
            roomAdapterFS.stopListening();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}