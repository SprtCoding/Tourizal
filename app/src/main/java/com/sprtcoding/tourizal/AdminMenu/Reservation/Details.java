package com.sprtcoding.tourizal.AdminMenu.Reservation;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sprtcoding.tourizal.FCM.FCMNotificationSender;
import com.sprtcoding.tourizal.R;
import com.sprtcoding.tourizal.Utility.NetworkChangeListener;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Details extends AppCompatActivity {
    private ImageView back_btn, _img;
    private TextView _my_name_txt, _user_name_txt, _room_name, _date_reserved, _contact, _status, price, guestNo, dayStayed, hourStayed;
    private MaterialButton approve_btn, decline_btn;
    String my_name, user_name, room_name, img_url, reserved_date, number, status, id, userToken
            ,user_id, resort_id, resort_name, amenities_type, _hour_stayed;
    int _guestNo, _dayStayed;
    double room_price;
    private FirebaseFirestore db;
    private CollectionReference reservedColRef, resortColRef;
    private ProgressDialog loading;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        _init();

        loading = new ProgressDialog(this);
        loading.setTitle("Loading");
        loading.setMessage("Please wait...");
        loading.setCancelable(false);

        db = FirebaseFirestore.getInstance();
        reservedColRef = db.collection("RESERVATION");
        resortColRef = db.collection("RESORTS");

        back_btn.setOnClickListener(view -> finish());

        getMyIntent();

        resortColRef.document(resort_id)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        resort_name = task.getResult().getString("RESORT_NAME");
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show());

        Picasso.get().load(img_url).into(_img);
        _my_name_txt.setText(my_name);
        _user_name_txt.setText(user_name);

        if(amenities_type.equals("Rooms")) {
            _room_name.setText("Room " + room_name);
        } else {
            _room_name.setText("Cottage " + room_name);
        }

        _date_reserved.setText(reserved_date);
        _contact.setText(number);

        approve_btn.setOnClickListener(view -> {
            loading.show();
            Map<String, Object> map = new HashMap<>();
            map.put("STATUS", "Approved");

            reservedColRef.document(id)
                    .update(map)
                    .addOnSuccessListener(unused -> {
                        loading.dismiss();
                        sendNotification(
                                user_id,
                                user_name,
                                resort_name
                        );
                        getMyIntent();
                        Toast.makeText(this, "Approved successfully!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show());
        });

    }

    @SuppressLint("SetTextI18n")
    private void getMyIntent() {
        if(getIntent() != null) {
            my_name = getIntent().getStringExtra("my_name");
            user_name = getIntent().getStringExtra("user_name");
            room_name = String.valueOf(getIntent().getIntExtra("room_name", 0));
            img_url = getIntent().getStringExtra("img");
            reserved_date = getIntent().getStringExtra("reserved_date");
            number = getIntent().getStringExtra("number");
            status = getIntent().getStringExtra("status");
            id = getIntent().getStringExtra("reserved_id");
            user_id = getIntent().getStringExtra("user_id");
            resort_id = getIntent().getStringExtra("resort_id");
            String prices = getIntent().getStringExtra("pp");
            room_price = Double.parseDouble(prices);
            amenities_type = getIntent().getStringExtra("amenities_type");
            _hour_stayed = getIntent().getStringExtra("hour_stayed");
            _guestNo = getIntent().getIntExtra("guest_no",0);
            _dayStayed = getIntent().getIntExtra("day_stayed",0);

            price.setText("Total Payment: " + convertToPhilippinePeso(room_price));

            hourStayed.setText(_hour_stayed);
            guestNo.setText("" + _guestNo);
            dayStayed.setText("" + _dayStayed);
        }

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

        if(status.equals("Approved") || status.equals("Declined") || status.equals("Cancelled")) {
            approve_btn.setVisibility(View.GONE);
            decline_btn.setText("Delete");

            decline_btn.setOnClickListener(view -> {
                loading.show();
                Map<String, Object> map = new HashMap<>();
                map.put("REMOVED", true);
                reservedColRef.document(id)
                        .update(map)
                        .addOnSuccessListener(unused -> {
                            loading.dismiss();
                            Toast.makeText(this, "Removed successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show());
            });
        }else {
            decline_btn.setOnClickListener(view -> {
                loading.show();
                Map<String, Object> map = new HashMap<>();
                map.put("STATUS", "Declined");

                reservedColRef.document(id)
                        .update(map)
                        .addOnSuccessListener(unused -> {
                            loading.dismiss();
                            sendDeclineNotification(
                                    user_id,
                                    user_name,
                                    resort_name
                            );
                            Toast.makeText(this, "Declined successfully!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show());
            });
        }
    }

    private void _init() {
        back_btn = findViewById(R.id.back_btn);
        _img = findViewById(R.id.img);
        _my_name_txt = findViewById(R.id.my_name_txt);
        _user_name_txt = findViewById(R.id.user_name_txt);
        _room_name = findViewById(R.id.room_name);
        _date_reserved = findViewById(R.id.date_reserved);
        _contact = findViewById(R.id.contact);
        _status = findViewById(R.id.status);
        approve_btn = findViewById(R.id.approve_btn);
        decline_btn = findViewById(R.id.decline_btn);
        price = findViewById(R.id.price);
        guestNo = findViewById(R.id.guestNo);
        dayStayed = findViewById(R.id.dayStayed);
        hourStayed = findViewById(R.id.hourStayed);
    }

    private void sendNotification(String ID, String name, String resortName) {
        FirebaseDatabase.getInstance().getReference("UserToken").child(ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    userToken = snapshot.child("token").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Details.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            FCMNotificationSender.sendNotification(
                    Details.this,
                    userToken,
                    "TouRizal",
                    "Hello,\n"+ name + "\n" +
                            resortName +
                            " Approved your booking."
            );
        }, 3000);
    }

    private void sendDeclineNotification(String ID, String name, String resortName) {
        FirebaseDatabase.getInstance().getReference("UserToken").child(ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    userToken = snapshot.child("token").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Details.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            FCMNotificationSender.sendNotification(
                    Details.this,
                    userToken,
                    "TouRizal",
                    "Hello,\n"+ name + "\n" +
                            resortName +
                            " Declined your booking."
            );
        }, 3000);
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