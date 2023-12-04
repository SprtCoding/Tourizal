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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.sprtcoding.tourizal.Adapter.FireStoreAdapter.KayakinAdapterFS;
import com.sprtcoding.tourizal.Model.FSModel.KayakinModel;
import com.sprtcoding.tourizal.R;
import com.sprtcoding.tourizal.Utility.NetworkChangeListener;

import java.util.ArrayList;
import java.util.List;

public class ManageKayakin extends AppCompatActivity {
    private ImageView _backBtn;
    private RecyclerView _admin_kayakin_rv;
    private LinearLayout _no_post_kayakin_ll;
    private FloatingActionButton _add_kayakin_btn;
    FirebaseAuth mAuth;
    private KayakinAdapterFS kayakinAdapterFS;
    FirebaseFirestore DB;
    CollectionReference resortCollectionRef;
    List<KayakinModel> kayakinModels;
    ProgressDialog _loading, _getDataLoading;
    FirebaseUser _user;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_kayakin);
        _init();

        kayakinModels = new ArrayList<>();

        _loading = new ProgressDialog(this);
        _loading.setTitle("Loading");
        _loading.setMessage("Please wait...");

        _getDataLoading = new ProgressDialog(this);
        _getDataLoading.setMessage("Loading data...");
        _getDataLoading.show();

        DB = FirebaseFirestore.getInstance();
        resortCollectionRef = DB.collection("RESORTS");

        mAuth = FirebaseAuth.getInstance();

        _user = mAuth.getCurrentUser();

        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);
        _admin_kayakin_rv.setHasFixedSize(true);
        _admin_kayakin_rv.setLayoutManager(llm);

        if(_user != null) {
            resortCollectionRef.whereEqualTo("OWNER_UID", _user.getUid())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for(DocumentSnapshot doc : queryDocumentSnapshots) {
                            String resort_id = doc.getString("RESORT_ID");
                            if(resort_id != null) {
                                Query userQuery = resortCollectionRef.document(resort_id)
                                        .collection("KAYAKIN")
                                        .whereEqualTo("RESORT_UID", resort_id);

                                userQuery.get().addOnSuccessListener(queryDocumentSnapshots1 -> {
                                    if(!queryDocumentSnapshots1.isEmpty()) {
                                        _getDataLoading.dismiss();
                                        FirestoreRecyclerOptions<KayakinModel> options = new FirestoreRecyclerOptions.Builder<KayakinModel>()
                                                .setQuery(userQuery, KayakinModel.class)
                                                .build();

                                        kayakinAdapterFS = new KayakinAdapterFS(options);
                                        _admin_kayakin_rv.setAdapter(kayakinAdapterFS);

                                        kayakinAdapterFS.startListening();
                                    }else {
                                        // Log a message here
                                        Log.d("Debug", "No data found in Kayakin collection");
                                        _getDataLoading.dismiss();
                                        _admin_kayakin_rv.setVisibility(View.GONE);
                                        _no_post_kayakin_ll.setVisibility(View.VISIBLE);
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

        _add_kayakin_btn.setOnClickListener(view -> {
            _loading.show();
            Handler handler = new Handler();
            Runnable runnable = () -> {
                _loading.dismiss();
                Intent gotoAddKayak = new Intent(this, AddKayakPage.class);
                startActivity(gotoAddKayak);
                finish();
            };
            handler.postDelayed(runnable, 2000);
        });

        _backBtn.setOnClickListener(view -> {
            finish();
        });
    }
    private void _init() {
        _backBtn = findViewById(R.id.backBtn);
        _admin_kayakin_rv = findViewById(R.id.admin_kayakin_rv);
        _no_post_kayakin_ll = findViewById(R.id.no_post_kayakin_ll);
        _add_kayakin_btn = findViewById(R.id.add_kayakin_btn);
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
        if(kayakinAdapterFS != null) {
            kayakinAdapterFS.stopListening();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}