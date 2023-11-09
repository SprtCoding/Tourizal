package com.sprtcoding.tourizal.AdminPost;

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
import com.sprtcoding.tourizal.Adapter.FeaturedPostAdapter;
import com.sprtcoding.tourizal.Adapter.FireStoreAdapter.FeaturedAdapterFS;
import com.sprtcoding.tourizal.Model.FSModel.FeaturedModelFS;
import com.sprtcoding.tourizal.Model.FeaturedPostModel;
import com.sprtcoding.tourizal.R;
import com.sprtcoding.tourizal.Utility.NetworkChangeListener;

import java.util.ArrayList;
import java.util.List;

public class FeaturedPost extends AppCompatActivity {
    private ImageView _backBtn;
    private RecyclerView _admin_featured_rv;
    private LinearLayout _no_post_featured_ll;
    private FloatingActionButton _add_featured_btn;
    private FirebaseAuth mAuth;
    FirebaseUser _user;
    private FirebaseDatabase mDb;
    private DatabaseReference featuredPostRef;
    FirebaseFirestore DB;
    CollectionReference resortCollectionRef;
    FeaturedAdapterFS featuredAdapterFS;
    FeaturedPostAdapter featuredPostAdapter;
    List<FeaturedPostModel> featuredPostModels;
    ProgressDialog _loading, _getDataLoading;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_featured_post);
        _var();

        featuredPostModels = new ArrayList<>();

        DB = FirebaseFirestore.getInstance();
        resortCollectionRef = DB.collection("RESORTS");

        _loading = new ProgressDialog(this);
        _loading.setTitle("Loading");
        _loading.setMessage("Please wait...");

        _getDataLoading = new ProgressDialog(this);
        _getDataLoading.setMessage("Loading data...");
        _getDataLoading.show();

        mAuth = FirebaseAuth.getInstance();
        _user = mAuth.getCurrentUser();
        mDb = FirebaseDatabase.getInstance();
        featuredPostRef = mDb.getReference("FeaturedPost");

        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        _admin_featured_rv.setHasFixedSize(true);
        _admin_featured_rv.setLayoutManager(llm);

        if(_user != null) {
            resortCollectionRef.whereEqualTo("OWNER_UID", _user.getUid())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for(DocumentSnapshot doc : queryDocumentSnapshots) {
                            String resort_id = doc.getString("RESORT_ID");
                            if(resort_id != null) {
                                Query userQuery = resortCollectionRef.document(resort_id)
                                        .collection("FEATURED")
                                        .whereEqualTo("RESORT_ID", resort_id);

                                userQuery.get().addOnSuccessListener(queryDocumentSnapshots1 -> {
                                    if (!queryDocumentSnapshots1.isEmpty()) {
                                        _getDataLoading.dismiss();
                                        // Log a message here
                                        Log.d("Debug", "Data found in FEATURED collection");

                                        FirestoreRecyclerOptions<FeaturedModelFS> options = new FirestoreRecyclerOptions.Builder<FeaturedModelFS>()
                                                .setQuery(userQuery, FeaturedModelFS.class)
                                                .build();

                                        featuredAdapterFS = new FeaturedAdapterFS(options);
                                        _admin_featured_rv.setAdapter(featuredAdapterFS);

                                        featuredAdapterFS.startListening();
                                    } else {
                                        _getDataLoading.dismiss();
                                        // Log a message here
                                        Log.d("Debug", "No data found in FEATURED collection");
                                        _admin_featured_rv.setVisibility(View.GONE);
                                        _no_post_featured_ll.setVisibility(View.VISIBLE);
                                    }
                                }).addOnFailureListener(e -> {
                                    _getDataLoading.dismiss();
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

//        featuredPostRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()) {
//                    featuredPostModels.clear();
//                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                        FeaturedPostModel featuredPost = dataSnapshot.getValue(FeaturedPostModel.class);
//                        featuredPostModels.add(featuredPost);
//                    }
//                    featuredPostAdapter = new FeaturedPostAdapter(FeaturedPost.this, featuredPostModels);
//                    _admin_featured_rv.setAdapter(featuredPostAdapter);
//                }else {
//                    _admin_featured_rv.setVisibility(View.GONE);
//                    _no_post_featured_ll.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(FeaturedPost.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });

        _add_featured_btn.setOnClickListener(view -> {
            _loading.show();
            Handler handler = new Handler();
            Runnable runnable = () -> {
                _loading.dismiss();
                Intent gotoFeaturedPost = new Intent(this, AddFeaturedPost.class);
                startActivity(gotoFeaturedPost);
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
        _admin_featured_rv = findViewById(R.id.admin_featured_rv);
        _no_post_featured_ll = findViewById(R.id.no_post_featured_ll);
        _add_featured_btn = findViewById(R.id.add_featured_btn);
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
        if(featuredAdapterFS != null) {
            featuredAdapterFS.stopListening();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}