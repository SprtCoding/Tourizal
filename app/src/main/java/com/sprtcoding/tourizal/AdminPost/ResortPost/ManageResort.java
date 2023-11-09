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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.sprtcoding.tourizal.Adapter.AdminResortAdapter;
import com.sprtcoding.tourizal.Adapter.FireStoreAdapter.ResortAdapterFS;
import com.sprtcoding.tourizal.Model.FSModel.ResortFireStoreModel;
import com.sprtcoding.tourizal.Model.ResortsModel;
import com.sprtcoding.tourizal.R;
import com.sprtcoding.tourizal.Utility.NetworkChangeListener;

import java.util.ArrayList;
import java.util.List;

public class ManageResort extends AppCompatActivity {
    private ImageView _backBtn;
    private RecyclerView _admin_resort_rv;
    private LinearLayout _no_post_resort_ll;
    private FloatingActionButton _add_resort_btn;
    private FirebaseAuth mAuth;
    List<ResortsModel> resortsModels;
    ProgressDialog _loading, _getDataLoading;
    FirebaseFirestore DB;
    CollectionReference resortCollectionRef;
    ResortAdapterFS resortAdapterFS;
    FirebaseUser _user;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_resort);
        _var();

        resortsModels = new ArrayList<>();

        _loading = new ProgressDialog(this);
        _loading.setTitle("Loading");
        _loading.setMessage("Please wait...");

        _getDataLoading = new ProgressDialog(this);
        _getDataLoading.setMessage("Loading data...");
        _getDataLoading.show();

        mAuth = FirebaseAuth.getInstance();

        //firestore
        DB = FirebaseFirestore.getInstance();
        resortCollectionRef = DB.collection("RESORTS");

        _user = mAuth.getCurrentUser();

        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        _admin_resort_rv.setHasFixedSize(true);
        _admin_resort_rv.setLayoutManager(llm);

        if(_user != null) {

            Query userQuery = resortCollectionRef.whereEqualTo("OWNER_UID", _user.getUid());

            userQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
                if(!queryDocumentSnapshots.isEmpty()) {
                    _getDataLoading.dismiss();
                    FirestoreRecyclerOptions<ResortFireStoreModel> options = new FirestoreRecyclerOptions.Builder<ResortFireStoreModel>()
                            .setQuery(userQuery, ResortFireStoreModel.class)
                            .build();

                    resortAdapterFS = new ResortAdapterFS(options);
                    _admin_resort_rv.setAdapter(resortAdapterFS);

                    resortAdapterFS.startListening();

                    _add_resort_btn.setVisibility(View.GONE);
                }else {
                    _getDataLoading.dismiss();
                    _admin_resort_rv.setVisibility(View.GONE);
                    _no_post_resort_ll.setVisibility(View.VISIBLE);
                    _add_resort_btn.setVisibility(View.VISIBLE);
                }
            }).addOnFailureListener(e -> {
                _getDataLoading.dismiss();
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }

        _add_resort_btn.setOnClickListener(view -> {
            _loading.show();
            Handler handler = new Handler();
            Runnable runnable = () -> {
                _loading.dismiss();
                Intent gotoAddResort = new Intent(ManageResort.this, AddResortPage.class);
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
        _admin_resort_rv = findViewById(R.id.admin_resort_rv);
        _no_post_resort_ll = findViewById(R.id.no_post_resort_ll);
        _add_resort_btn = findViewById(R.id.add_resort_btn);
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
        if(resortAdapterFS != null) {
            resortAdapterFS.stopListening();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}