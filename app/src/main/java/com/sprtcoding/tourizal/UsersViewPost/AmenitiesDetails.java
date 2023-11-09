package com.sprtcoding.tourizal.UsersViewPost;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.sprtcoding.tourizal.R;
import com.sprtcoding.tourizal.Utility.NetworkChangeListener;
import com.squareup.picasso.Picasso;

public class AmenitiesDetails extends AppCompatActivity {
    private ImageView roomPic, backBtn;
    private TextView roomName, roomDescription, _amenitiesType;
    String amenitiesType, roomPicURL, roomDesc;
    int roomNo;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amenities_details);
        _init();

        if(getIntent() != null) {
            amenitiesType = getIntent().getStringExtra("AmenitiesType");
            roomPicURL = getIntent().getStringExtra("RoomPicURL");
            roomDesc = getIntent().getStringExtra("Details");
            roomNo = getIntent().getIntExtra("RoomName", 0);

            _amenitiesType.setText(amenitiesType + " Description");
            Picasso.get()
                    .load(roomPicURL)
                    .fit()
                    .placeholder(R.drawable.resort)
                    .into(roomPic);
            roomDescription.setText(roomDesc);

            if(amenitiesType.equals("Room")) {
                roomName.setText("Room " + roomNo);
            } else if(amenitiesType.equals("Cottage")) {
                roomName.setText("Cottage " + roomNo);
            }
        }

        backBtn.setOnClickListener(view -> finish());
    }

    private void _init() {
        roomPic = findViewById(R.id.roomPic);
        backBtn = findViewById(R.id.backBtn);
        roomName = findViewById(R.id.roomName);
        roomDescription = findViewById(R.id.roomDescription);
        _amenitiesType = findViewById(R.id.amenitiesType);
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