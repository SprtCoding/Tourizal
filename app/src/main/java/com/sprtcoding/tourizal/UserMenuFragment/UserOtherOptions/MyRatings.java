package com.sprtcoding.tourizal.UserMenuFragment.UserOtherOptions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sprtcoding.tourizal.Adapter.FireStoreAdapter.MyRatingsAdapter;
import com.sprtcoding.tourizal.Model.FSModel.RatingsList;
import com.sprtcoding.tourizal.R;
import com.sprtcoding.tourizal.Utility.NetworkChangeListener;

import java.util.ArrayList;
import java.util.List;

public class MyRatings extends AppCompatActivity {
    private ImageView _back_btn;
    private RecyclerView _my_ratings_rv;
    private LinearLayout _no_data;
    private List<RatingsList> ratingsLists;
    MyRatingsAdapter myRatingsAdapter;
    FirebaseFirestore DB;
    CollectionReference ratingsColRef;
    FirebaseAuth mAuth;
    FirebaseUser user;
    ProgressDialog loading;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ratings);
        _init();

        DB = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        ratingsLists = new ArrayList<>();

        ratingsColRef = DB.collection("RATINGS");

        LinearLayoutManager llmRooms = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        _my_ratings_rv.setHasFixedSize(true);
        _my_ratings_rv.setLayoutManager(llmRooms);

        ratingsColRef.whereEqualTo("USER_ID", user.getUid())
                .addSnapshotListener((value, error) -> {
                    if(error == null && value != null) {
                        if(!value.isEmpty()) {
                            ratingsLists.clear();
                            for(QueryDocumentSnapshot doc : value) {
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
                            myRatingsAdapter = new MyRatingsAdapter(this, ratingsLists);
                            _my_ratings_rv.setAdapter(myRatingsAdapter);
                        } else {
                            _my_ratings_rv.setVisibility(View.GONE);
                            _no_data.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        _back_btn.setOnClickListener(view -> finish());

    }

    private void _init() {
        _back_btn = findViewById(R.id.back_btn);
        _my_ratings_rv = findViewById(R.id.my_ratings_rv);
        _no_data = findViewById(R.id.no_data);
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