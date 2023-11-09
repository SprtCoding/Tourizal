package com.sprtcoding.tourizal.Adapter.FireStoreAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sprtcoding.tourizal.FCM.FCMNotificationSender;
import com.sprtcoding.tourizal.FireStoreDB.DBQuery;
import com.sprtcoding.tourizal.FireStoreDB.MyCompleteListener;
import com.sprtcoding.tourizal.Model.FSModel.RoomFSModel;
import com.sprtcoding.tourizal.R;
import com.sprtcoding.tourizal.UsersViewPost.AmenitiesDetails;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AmenitiesRoomsAdapter extends RecyclerView.Adapter<AmenitiesRoomsAdapter.AmenitiesRoomsViewHolder>{
    Context context;
    Activity activity;
    List<RoomFSModel> roomsModels;
    boolean isClicked = false;
    private ImageView _closeDialogBtn;
    private TextView room_no_text;
    private CircleImageView room_pic;
    private TextInputEditText your_name_ET, your_contact_ET;
    private MaterialButton getLocationBtn, getDateBtn, reserved_btn;
    private AutoCompleteTextView _day_night;
    private FirebaseDatabase db;
    private DatabaseReference ref;
    private String reserved_id, userToken;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    public static FusedLocationProviderClient fusedLocationProviderClient;
    FirebaseAuth mAuth;
    float price = 0;
    FragmentManager supportFragment;

    public AmenitiesRoomsAdapter(Context context, List<RoomFSModel> roomsModels, Activity activity, FragmentManager supportFragment) {
        this.context = context;
        this.roomsModels = roomsModels;
        this.activity = activity;
        this.supportFragment = supportFragment;
    }

    @NonNull
    @Override
    public AmenitiesRoomsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.amenities_room_list,parent,false);
        return new AmenitiesRoomsAdapter.AmenitiesRoomsViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AmenitiesRoomsViewHolder holder, int position) {
        RoomFSModel rooms = roomsModels.get(position);

        db = FirebaseDatabase.getInstance();
        ref = db.getReference("Users");

        reserved_id = ref.push().getKey();

        DBQuery.g_firestore = FirebaseFirestore.getInstance();

        Picasso.get().load(rooms.getROOM_PHOTO_URL()).into(holder._roomPhoto);
        holder._roomName.setText("ROOM " + rooms.getROOM_NO());
        holder._rentPrice.setText(convertToPhilippinePeso(rooms.getDAY_PRICE()));
        holder._roomDescription.setText(rooms.getDESCRIPTION());

        holder._switchBtn.setOnClickListener(view -> {
            if (isClicked) {
                // Switch back to night mode
                holder._switchBtn.setImageResource(R.drawable.baseline_bedtime_24);
                Picasso.get().load(rooms.getROOM_PHOTO_URL()).into(holder._roomPhoto);
                holder._roomName.setText("ROOM " + rooms.getROOM_NO());
                holder._rentPrice.setText(convertToPhilippinePeso(rooms.getNIGHT_PRICE()));
            } else {
                // Switch to day mode
                holder._switchBtn.setImageResource(R.drawable.baseline_wb_sunny_24); // Change to the day icon
                Picasso.get().load(rooms.getROOM_PHOTO_URL()).into(holder._roomPhoto);
                holder._roomName.setText("ROOM " + rooms.getROOM_NO());
                holder._rentPrice.setText(convertToPhilippinePeso(rooms.getDAY_PRICE()));
                // Update other views for day mode if needed
            }
            // Toggle the value of isClicked
            isClicked = !isClicked;
        });

        mAuth = FirebaseAuth.getInstance();

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("Users");

        ref.child(mAuth.getCurrentUser().getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()) {
                                    String photoURL = snapshot.child("PhotoURL").getValue(String.class);
                                    String name = snapshot.child("Fullname").getValue(String.class);
                                    viewReservedDialog(holder, rooms, photoURL, name);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

        holder.itemView.setOnClickListener(view -> {
            Intent i = new Intent(context, AmenitiesDetails.class);
            i.putExtra("AmenitiesType", "Room");
            i.putExtra("Details", rooms.getDESCRIPTION());
            i.putExtra("RoomPicURL", rooms.getROOM_PHOTO_URL());
            i.putExtra("RoomName", rooms.getROOM_NO());
            context.startActivity(i);
        });

    }

    @Override
    public int getItemCount() {
        return roomsModels.size();
    }

    public static class AmenitiesRoomsViewHolder extends RecyclerView.ViewHolder {
        ImageView _roomPhoto, _switchBtn;
        TextView _roomName, _rentPrice, _reserveBtn, _roomDescription;
        CardView _amenitiesRoomCard;

        public AmenitiesRoomsViewHolder(@NonNull View itemView) {
            super(itemView);

            _roomDescription = itemView.findViewById(R.id.roomDescription);
            _roomPhoto = itemView.findViewById(R.id.roomPhoto);
            _roomName = itemView.findViewById(R.id.roomName);
            _rentPrice = itemView.findViewById(R.id.rentPrice);
            _reserveBtn = itemView.findViewById(R.id.reserveBtn);
            _amenitiesRoomCard = itemView.findViewById(R.id.amenitiesRoomCard);
            _switchBtn = itemView.findViewById(R.id.switchBtn);

        }
    }

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    private void viewReservedDialog(AmenitiesRoomsViewHolder holder, RoomFSModel rooms, String userPic, String name) {
        ProgressDialog _loading;
        _loading = new ProgressDialog(context);
        _loading.setTitle("Reservation");
        _loading.setMessage("Please wait...");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        View reservationDialog = LayoutInflater.from(context).inflate(R.layout.reservation_dialog, null);
        AlertDialog.Builder reservationDialogBuilder = new AlertDialog.Builder(context);

        reservationDialogBuilder.setView(reservationDialog);

        _closeDialogBtn = reservationDialog.findViewById(R.id.close_btn);
        _day_night = reservationDialog.findViewById(R.id.day_night);
        room_no_text = reservationDialog.findViewById(R.id.room_no_text);
        room_pic = reservationDialog.findViewById(R.id.room_pic);
        your_name_ET = reservationDialog.findViewById(R.id.your_name_ET);
        your_contact_ET = reservationDialog.findViewById(R.id.your_contact_ET);
        getLocationBtn = reservationDialog.findViewById(R.id.getLocationBtn);
        getDateBtn = reservationDialog.findViewById(R.id.getDateBtn);
        reserved_btn = reservationDialog.findViewById(R.id.reserved_btn);

        final AlertDialog reservationDialogs = reservationDialogBuilder.create();

        reservationDialogs.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        reservationDialogs.setCanceledOnTouchOutside(false);

        String[] day_night_item = new String[] {"Day", "Night"};

        ArrayAdapter<String> day_Night_adapter = new ArrayAdapter<>(
                context,
                R.layout.day_time_list_item,
                day_night_item
        );

        _day_night.setAdapter(day_Night_adapter);
        _day_night.setOnItemClickListener((adapterView, view, i, l) -> {
            String item = adapterView.getItemAtPosition(i).toString();
            if(item.equals("Day")) {
                price = rooms.getDAY_PRICE();
            } else if (item.equals("Night")) {
                price = rooms.getNIGHT_PRICE();
            }
        });

        room_no_text.setText("Reserved ROOM " + rooms.getROOM_NO());
        your_name_ET.setText(name);

        Picasso.get().load(rooms.getROOM_PHOTO_URL()).into(room_pic);

        _closeDialogBtn.setOnClickListener(view -> reservationDialogs.cancel());

        getLocationBtn.setOnClickListener(view -> {
            checkLocationPermission();
        });

        getDateBtn.setOnClickListener(view -> {
            // Create a date picker builder
            MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date to Reserved")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();

            materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                String date = new SimpleDateFormat("LLL dd yyyy", Locale.getDefault()).format(new Date(selection));
                getDateBtn.setText(date);
            });
            materialDatePicker.show(supportFragment, "date");
        });

        reserved_btn.setOnClickListener(view -> {
            _loading.show();
            String your_name = your_name_ET.getText().toString();
            String your_phone = your_contact_ET.getText().toString();
            String dayNight = _day_night.getText().toString();
            String your_location = getLocationBtn.getText().toString();
            String reservation_date = getDateBtn.getText().toString();
            Date dateTime = Calendar.getInstance().getTime();
            SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd yyyy", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String date = dateFormat.format(dateTime);
            String time = timeFormat.format(dateTime);

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();

            if(TextUtils.isEmpty(your_name)) {
                Toast.makeText(context, "Your name is required.", Toast.LENGTH_SHORT).show();
                your_name_ET.requestFocus();
                _loading.dismiss();
            } else if (TextUtils.isEmpty(your_phone)) {
                Toast.makeText(context, "Your Phone Number is required.", Toast.LENGTH_SHORT).show();
                your_contact_ET.requestFocus();
                _loading.dismiss();
            } else if (dayNight.equals("")) {
                Toast.makeText(context, "Select Day or Night.", Toast.LENGTH_SHORT).show();
                _loading.dismiss();
            } else if (your_location.equals("Your Location")) {
                Toast.makeText(context, "Your Location is required.", Toast.LENGTH_SHORT).show();
                _loading.dismiss();
            } else if (reservation_date.equals("Reservation Date")) {
                Toast.makeText(context, "Reservation Date is required.", Toast.LENGTH_SHORT).show();
                _loading.dismiss();
            } else {
                if(your_contact_ET.length() < 11) {
                    _loading.dismiss();
                    Toast.makeText(context, "Please input valid phone number!", Toast.LENGTH_SHORT).show();
                }else {
                    DBQuery.setReservationRoomUser(
                            rooms.getOWNER_UID(),
                            user.getUid(),
                            "Rooms",
                            false,
                            rooms.getRESORT_ID(),
                            rooms.getROOM_ID(),
                            reserved_id,
                            rooms.getROOM_NO(),
                            your_name,
                            your_phone,
                            your_location,
                            reservation_date,
                            time,
                            date,
                            "Pending",
                            dayNight,
                            price,
                            false,
                            userPic,
                            rooms.getROOM_PHOTO_URL()
                            , new MyCompleteListener() {
                                @Override
                                public void onSuccess() {
                                    sendNotification(rooms.getOWNER_UID(), your_name, String.valueOf(rooms.getROOM_NO()));
                                    Toast.makeText(context, "Room " + rooms.getROOM_NO() + " reserved successfully.", Toast.LENGTH_SHORT).show();
                                    reservationDialogs.cancel();
                                    _loading.dismiss();
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    _loading.dismiss();
                                }
                            }
                    );
                }
            }
        });

        holder._reserveBtn.setOnClickListener(view -> {
            reservationDialogs.show();
        });
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, start getting the location
            getLocation();
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        // Location retrieved successfully
                        double lat = location.getLatitude();
                        double lon = location.getLongitude();

                        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                            if (addresses.size() > 0) {
                                Address address = addresses.get(0);
                                // Update the TextView with the location name
                                getLocationBtn.setText(address.getAddressLine(0));
                            } else {
                                // No address found
                                getLocationBtn.setText("No address found!");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // Location unavailable
                        getLocationBtn.setText("Location unavailable");
                    }
                }).addOnFailureListener(e -> {
                    // Failed to get location
                    getLocationBtn.setText("Failed to get location");
                });
    }

    private void sendNotification(String OwnerID, String UserName, String book_no) {
        FirebaseDatabase.getInstance().getReference("UserToken").child(OwnerID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    userToken = snapshot.child("token").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            FCMNotificationSender.sendNotification(
                    context,
                    userToken,
                    "TouRizal",
                    UserName + " booked room " + book_no + "."
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
}
