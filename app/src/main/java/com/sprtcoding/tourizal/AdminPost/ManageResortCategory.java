package com.sprtcoding.tourizal.AdminPost;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.sprtcoding.tourizal.AdminPost.ResortPost.ManageCottages;
import com.sprtcoding.tourizal.AdminPost.ResortPost.ManageKayakin;
import com.sprtcoding.tourizal.AdminPost.ResortPost.ManagePool;
import com.sprtcoding.tourizal.AdminPost.ResortPost.ManageResort;
import com.sprtcoding.tourizal.AdminPost.ResortPost.ManageRooms;
import com.sprtcoding.tourizal.R;
import com.sprtcoding.tourizal.Utility.NetworkChangeListener;
import com.squareup.picasso.Picasso;

public class ManageResortCategory extends AppCompatActivity {
    private ImageView _backBtn, resort_img, room_img, cottage_img, pool_img, kayakin_img;
    private CardView _yourResortCard, _yourRoomsCard, _yourCottagesCard, _yourPoolCard, _yourKayakinCard;
    private TextView _yourResortTV, _yourRoomsTV, _yourCottagesTV, _yourPoolTV, _yourKayakinTV;
    ProgressDialog _loading;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_resort_category);
        _var();

        _loading = new ProgressDialog(this);
        _loading.setTitle("Loading");
        _loading.setMessage("Please wait...");

        Picasso.get().load(R.drawable.resort).fit().into(resort_img);
        Picasso.get().load(R.drawable.room_bed).fit().into(room_img);
        Picasso.get().load(R.drawable.cottages).fit().into(cottage_img);
        Picasso.get().load(R.drawable.pool).fit().into(pool_img);
        Picasso.get().load(R.drawable.kayak).fit().into(kayakin_img);

        _yourResortTV.setOnClickListener(view -> {
            _loading.show();
            Handler handler = new Handler();
            Runnable runnable = () -> {
                _loading.dismiss();
                Intent gotoResortPost = new Intent(this, ManageResort.class);
                startActivity(gotoResortPost);
            };
            handler.postDelayed(runnable, 2000);
        });

        _yourResortCard.setOnClickListener(view -> {
            _loading.show();
            Handler handler = new Handler();
            Runnable runnable = () -> {
                _loading.dismiss();
                Intent gotoResortPost = new Intent(this, ManageResort.class);
                startActivity(gotoResortPost);
            };
            handler.postDelayed(runnable, 2000);
        });

        _yourRoomsTV.setOnClickListener(view -> {
            _loading.show();
            Handler handler = new Handler();
            Runnable runnable = () -> {
                _loading.dismiss();
                Intent gotoRoomsPost = new Intent(this, ManageRooms.class);
                startActivity(gotoRoomsPost);
            };
            handler.postDelayed(runnable, 2000);
        });

        _yourRoomsCard.setOnClickListener(view -> {
            _loading.show();
            Handler handler = new Handler();
            Runnable runnable = () -> {
                _loading.dismiss();
                Intent gotoRoomsPost = new Intent(this, ManageRooms.class);
                startActivity(gotoRoomsPost);
            };
            handler.postDelayed(runnable, 2000);
        });

        _yourCottagesTV.setOnClickListener(view -> {
            _loading.show();
            Handler handler = new Handler();
            Runnable runnable = () -> {
                _loading.dismiss();
                Intent gotoCottagePost = new Intent(this, ManageCottages.class);
                startActivity(gotoCottagePost);
            };
            handler.postDelayed(runnable, 2000);
        });

        _yourCottagesCard.setOnClickListener(view -> {
            _loading.show();
            Handler handler = new Handler();
            Runnable runnable = () -> {
                _loading.dismiss();
                Intent gotoCottagePost = new Intent(this, ManageCottages.class);
                startActivity(gotoCottagePost);
            };
            handler.postDelayed(runnable, 2000);
        });

        _yourPoolCard.setOnClickListener(view -> {
            _loading.show();
            Handler handler = new Handler();
            Runnable runnable = () -> {
                _loading.dismiss();
                Intent gotoPoolPost = new Intent(this, ManagePool.class);
                startActivity(gotoPoolPost);
            };
            handler.postDelayed(runnable, 2000);
        });

        _yourPoolTV.setOnClickListener(view -> {
            _loading.show();
            Handler handler = new Handler();
            Runnable runnable = () -> {
                _loading.dismiss();
                Intent gotoPoolPost = new Intent(this, ManagePool.class);
                startActivity(gotoPoolPost);
            };
            handler.postDelayed(runnable, 2000);
        });

        _yourKayakinCard.setOnClickListener(view -> {
            _loading.show();
            Handler handler = new Handler();
            Runnable runnable = () -> {
                _loading.dismiss();
                Intent gotoKayakinPost = new Intent(this, ManageKayakin.class);
                startActivity(gotoKayakinPost);
            };
            handler.postDelayed(runnable, 2000);
        });

        _yourKayakinTV.setOnClickListener(view -> {
            _loading.show();
            Handler handler = new Handler();
            Runnable runnable = () -> {
                _loading.dismiss();
                Intent gotoKayakinPost = new Intent(this, ManageKayakin.class);
                startActivity(gotoKayakinPost);
            };
            handler.postDelayed(runnable, 2000);
        });

        _backBtn.setOnClickListener(view -> {
            finish();
        });
    }

    private void _var() {
        _backBtn = findViewById(R.id.backBtn);
        _yourResortCard = findViewById(R.id.yourResortCard);
        _yourRoomsCard = findViewById(R.id.yourRoomsCard);
        _yourCottagesCard = findViewById(R.id.yourCottagesCard);
        _yourPoolCard = findViewById(R.id.yourPoolCard);
        _yourKayakinCard = findViewById(R.id.yourKayakinCard);
        _yourResortTV = findViewById(R.id.viewYourResortTV);
        _yourRoomsTV = findViewById(R.id.viewYourRoomsTV);
        _yourCottagesTV = findViewById(R.id.viewYourCottagesTV);
        _yourPoolTV = findViewById(R.id.viewYourPoolTV);
        _yourKayakinTV = findViewById(R.id.viewYourKayakinTV);
        resort_img = findViewById(R.id.resort_img);
        room_img = findViewById(R.id.room_img);
        cottage_img = findViewById(R.id.cottage_img);
        pool_img = findViewById(R.id.pool_img);
        kayakin_img = findViewById(R.id.kayakin_img);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}