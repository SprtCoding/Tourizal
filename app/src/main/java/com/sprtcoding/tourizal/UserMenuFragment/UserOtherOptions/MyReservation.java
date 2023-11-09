package com.sprtcoding.tourizal.UserMenuFragment.UserOtherOptions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
import com.sprtcoding.tourizal.Adapter.FireStoreAdapter.MyReservationAdapterFS;
import com.sprtcoding.tourizal.Adapter.ReviewsCommentRateAdapter;
import com.sprtcoding.tourizal.Model.FSModel.ReservationModelFS;
import com.sprtcoding.tourizal.R;
import com.sprtcoding.tourizal.Utility.NetworkChangeListener;

import java.util.ArrayList;
import java.util.List;

public class MyReservation extends AppCompatActivity {
    private ImageView _back_btn;
    private RecyclerView _my_reservation_rv;
    private LinearLayout _no_data;
    private MyReservationAdapterFS myReservationAdapterFS;
    List<ReservationModelFS> reservationModelFSList;
    FirebaseFirestore DB;
    CollectionReference reservationColRef;
    FirebaseAuth mAuth;
    FirebaseUser user;
    ProgressDialog loading;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservation);
        init();

        reservationModelFSList = new ArrayList<>();

        loading = new ProgressDialog(this);
        loading.setMessage("Loading data...");
        loading.show();

        DB = FirebaseFirestore.getInstance();
        reservationColRef = DB.collection("RESERVATION");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        LinearLayoutManager llmRooms = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        _my_reservation_rv.setHasFixedSize(true);
        _my_reservation_rv.setLayoutManager(llmRooms);

        if(user != null) {
            reservationColRef
                    .whereEqualTo("MY_UID", user.getUid())
                    .addSnapshotListener((value, error) -> {
                        if(error == null && value != null) {
                            if(!value.isEmpty()) {
                                reservationModelFSList.clear();
                                for(QueryDocumentSnapshot doc : value) {
                                    reservationModelFSList.add(new ReservationModelFS(
                                            doc.getString("OWNER_UID"),
                                            doc.getString("MY_UID"),
                                            doc.getString("RESORT_ID"),
                                            doc.getString("AMENITIES_ID"),
                                            doc.getString("RESERVED_ID"),
                                            doc.getString("NAME_OF_USER"),
                                            doc.getString("CONTACT_OF_USER"),
                                            doc.getString("LOCATION_OF_USER"),
                                            doc.getString("DATE_RESERVATION"),
                                            doc.getString("TIME"),
                                            doc.getString("DATE"),
                                            doc.getString("STATUS"),
                                            doc.getString("ROOM_PHOTO_URL"),
                                            doc.getString("DAYTIME"),
                                            doc.getString("USER_PHOTO_URL"),
                                            doc.getString("AMENITIES_TYPE"),
                                            doc.getLong("PRICE").longValue(),
                                            doc.getLong("AMENITIES_NO").intValue(),
                                            doc.getBoolean("READ").booleanValue()
                                    ));
                                }
                                loading.dismiss();
                                myReservationAdapterFS = new MyReservationAdapterFS(this, reservationModelFSList);
                                _my_reservation_rv.setAdapter(myReservationAdapterFS);
                            } else {
                                loading.dismiss();
                                _my_reservation_rv.setVisibility(View.GONE);
                                _no_data.setVisibility(View.VISIBLE);
                            }
                        } else {
                            loading.dismiss();
                            Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        _back_btn.setOnClickListener(view -> finish());
    }

    private void init() {
        _back_btn = findViewById(R.id.back_btn);
        _my_reservation_rv = findViewById(R.id.my_reservation_rv);
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