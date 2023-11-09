package com.sprtcoding.tourizal.UserMenuFragment.UserOtherOptions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sprtcoding.tourizal.AdminMenu.Reservation.Details;
import com.sprtcoding.tourizal.FCM.FCMNotificationSender;
import com.sprtcoding.tourizal.R;
import com.sprtcoding.tourizal.Utility.NetworkChangeListener;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MyReservedDetails extends AppCompatActivity {
    private ImageView back_btn, _img;
    private TextView _my_name_txt, _room_name, _date_reserved, _contact, _status, price;
    private MaterialButton cancel_btn;
    String user_name, room_name, img_url, reserved_date, number, status, id, userToken
            ,owner_id, resort_id, resort_name, amenities_type, amenities;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference reservedColRef, resortColRef;
    private ProgressDialog loading;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reserved_details);
        _init();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();

        loading = new ProgressDialog(this);
        loading.setTitle("Loading");
        loading.setMessage("Please wait...");
        loading.setCancelable(false);

        db = FirebaseFirestore.getInstance();
        reservedColRef = db.collection("RESERVATION");
        resortColRef = db.collection("RESORTS");

        back_btn.setOnClickListener(view -> finish());

        if(getIntent() != null) {
            resort_id = getIntent().getStringExtra("resort_id");
            img_url = getIntent().getStringExtra("img");
            id = getIntent().getStringExtra("reserved_id");
            status = getIntent().getStringExtra("status");
            owner_id = getIntent().getStringExtra("owner_id");
            user_name = getIntent().getStringExtra("my_name");
            number = getIntent().getStringExtra("number");
            reserved_date = getIntent().getStringExtra("reserved_date");
            amenities_type = getIntent().getStringExtra("amenities_type");
            room_name = String.valueOf(getIntent().getIntExtra("room_name", 0));
            double total_payment = getIntent().getDoubleExtra("payment", 0);

            // Define the color for the status part
            int statusDeclinedColor = Color.rgb(191, 64, 64);
            int statusApprovedColor = Color.rgb(49, 149, 91);
            int statusPendingColor = Color.rgb(251, 181, 63);
            int statusCancelledColor = Color.rgb(43, 47, 84);

            switch (status) {
                case "Approved": {
                    // Create a SpannableString
                    SpannableString spannableStatus = new SpannableString("Status: " + status);
                    // Apply the color only to the status part
                    spannableStatus.setSpan(new ForegroundColorSpan(statusApprovedColor), 8, spannableStatus.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    _status.setText(spannableStatus);
                    break;
                }
                case "Declined": {
                    // Create a SpannableString
                    SpannableString spannableStatus = new SpannableString("Status: " + status);
                    // Apply the color only to the status part
                    spannableStatus.setSpan(new ForegroundColorSpan(statusDeclinedColor), 8, spannableStatus.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    _status.setText(spannableStatus);
                    break;
                }
                case "Pending": {
                    // Create a SpannableString
                    SpannableString spannableStatus = new SpannableString("Status: " + status);
                    // Apply the color only to the status part
                    spannableStatus.setSpan(new ForegroundColorSpan(statusPendingColor), 8, spannableStatus.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    _status.setText(spannableStatus);
                    break;
                }
                case "Cancelled": {
                    // Create a SpannableString
                    SpannableString spannableStatus = new SpannableString("Status: " + status);
                    // Apply the color only to the status part
                    spannableStatus.setSpan(new ForegroundColorSpan(statusCancelledColor), 8, spannableStatus.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    _status.setText(spannableStatus);
                    break;
                }
            }

            if(amenities_type.equals("Rooms")) {
                amenities = "Room";
            } else {
                amenities = "Cottage";
            }

            resortColRef.document(resort_id)
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            resort_name = task.getResult().getString("RESORT_NAME");
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show());

            if(status.equals("Approved") || status.equals("Declined") || status.equals("Cancelled")) {
                cancel_btn.setText("Delete");
                cancel_btn.setOnClickListener(view -> {
                    loading.show();
                    reservedColRef.document(id)
                            .delete()
                            .addOnSuccessListener(unused -> {
                                loading.dismiss();
                                Toast.makeText(this, amenities + " " + room_name + " Removed.", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show());
                });
            }else {
                cancel_btn.setOnClickListener(view -> {
                    loading.show();
                    Map<String, Object> map = new HashMap<>();
                    map.put("STATUS", "Cancelled");

                    reservedColRef.document(id)
                            .update(map)
                            .addOnSuccessListener(unused -> {
                                loading.dismiss();
                                sendNotification(
                                        owner_id,
                                        user_name,
                                        room_name
                                );
                                Toast.makeText(this, amenities + " " + room_name + " Cancelled.", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show());
                });
            }

            Picasso.get().load(img_url).into(_img);
            _my_name_txt.setText(user_name);
            _room_name.setText(amenities + " " + room_name);
            _date_reserved.setText(reserved_date);
            _contact.setText(number);
            price.setText("Total Payment: " + convertToPhilippinePeso(total_payment));
        }
    }

    public static String convertToPhilippinePeso(double amount) {
        // Create a Locale for the Philippines
        Locale philippinesLocale = new Locale("en", "PH");

        // Create a NumberFormat for currency in the Philippines
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(philippinesLocale);

        // Set the currency code to PHP (Philippine Peso)
        currencyFormatter.setCurrency(Currency.getInstance("PHP"));

        // Format the amount as Philippine Peso currency
        String formattedAmount = currencyFormatter.format(amount);

        return formattedAmount;
    }

    private void _init() {
        back_btn = findViewById(R.id.back_btn);
        _img = findViewById(R.id.img);
        _my_name_txt = findViewById(R.id.my_name_txt);
        _room_name = findViewById(R.id.room_name);
        _date_reserved = findViewById(R.id.date_reserved);
        _contact = findViewById(R.id.contact);
        _status = findViewById(R.id.status);
        cancel_btn = findViewById(R.id.cancel_btn);
        price = findViewById(R.id.price);
    }

    private void sendNotification(String ID, String name, String roomName) {
        FirebaseDatabase.getInstance().getReference("UserToken").child(ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    userToken = snapshot.child("token").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyReservedDetails.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            FCMNotificationSender.sendNotification(
                    MyReservedDetails.this,
                    userToken,
                    "TouRizal",
                    name + " Cancelled " + roomName + "."
            );
        }, 3000);
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