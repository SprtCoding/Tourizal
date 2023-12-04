package com.sprtcoding.tourizal.Adapter.FireStoreAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;

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
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class AmenitiesRoomPagerAdapter extends PagerAdapter {
    Context context;
    Activity activity;
    List<RoomFSModel> roomsModels;
    boolean isClicked = false;
    ImageView _closeDialogBtn;
    MaterialButton reserved_btn;
    int t1Hour, t1Minute, t1HourTo, t1MinuteTo;
    FirebaseDatabase db;
    DatabaseReference ref;
    private String reserved_id, userToken;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    public static FusedLocationProviderClient fusedLocationProviderClient;
    FirebaseAuth mAuth;
    float price = 0, pricePerHour = 0, pricePerMinutes = 0;
    int hours_consumed, minutes_consumed;
    FragmentManager supportFragment;

    public AmenitiesRoomPagerAdapter(Context context, List<RoomFSModel> roomsModels, Activity activity, FragmentManager supportFragment) {
        this.context = context;
        this.roomsModels = roomsModels;
        this.activity = activity;
        this.supportFragment = supportFragment;
    }
    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.amenities_room_list, container, false);

        final TextView _roomDescription = view.findViewById(R.id.roomDescription);
        final ImageView _roomPhoto = view.findViewById(R.id.roomPhoto);
        final TextView _roomName = view.findViewById(R.id.roomName);
        final TextView _rentPrice = view.findViewById(R.id.rentPrice);
        final TextView _reserveBtn = view.findViewById(R.id.reserveBtn);
        final ImageView _switchBtn = view.findViewById(R.id.switchBtn);

        RoomFSModel room = roomsModels.get(position);

        db = FirebaseDatabase.getInstance();
        ref = db.getReference("Users");

        reserved_id = ref.push().getKey();

        DBQuery.g_firestore = FirebaseFirestore.getInstance();

        Picasso.get().load(room.getROOM_PHOTO_URL().get(position)).into(_roomPhoto);
        _roomName.setText("ROOM " + room.getROOM_NO());
        _rentPrice.setText(convertToPhilippinePeso(room.getDAY_PRICE()));
        _roomDescription.setText(room.getDESCRIPTION());

        _switchBtn.setOnClickListener(view1 -> {
            if (isClicked) {
                // Switch back to night mode
                _switchBtn.setImageResource(R.drawable.baseline_bedtime_24);
                Picasso.get().load(room.getROOM_PHOTO_URL().get(position)).into(_roomPhoto);
                _roomName.setText("ROOM " + room.getROOM_NO());
                _rentPrice.setText(convertToPhilippinePeso(room.getNIGHT_PRICE()));
            } else {
                // Switch to day mode
                _switchBtn.setImageResource(R.drawable.baseline_wb_sunny_24); // Change to the day icon
                Picasso.get().load(room.getROOM_PHOTO_URL().get(position)).into(_roomPhoto);
                _roomName.setText("ROOM " + room.getROOM_NO());
                _rentPrice.setText(convertToPhilippinePeso(room.getDAY_PRICE()));
                // Update other views for day mode if needed
            }
            // Toggle the value of isClicked
            isClicked = !isClicked;
        });

        mAuth = FirebaseAuth.getInstance();

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("Users");

        ref.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            String photoURL = snapshot.child("PhotoURL").getValue(String.class);
                            String name = snapshot.child("Fullname").getValue(String.class);
                            viewReservedDialog(_reserveBtn, room, photoURL, name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        view.setOnClickListener(view1 -> {
            Intent i = new Intent(context, AmenitiesDetails.class);
            i.putExtra("AmenitiesType", "Room");
            i.putExtra("Details", room.getDESCRIPTION());
            i.putExtra("RoomPicURL", room.getROOM_PHOTO_URL());
            i.putExtra("RoomName", room.getROOM_NO());
            context.startActivity(i);
        });

        container.addView(view);

        return view;
    }
    @Override
    public int getCount() {
        return roomsModels.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    private void viewReservedDialog(TextView reservedBtn, RoomFSModel rooms, String userPic, String name) {
        ProgressDialog _loading;
        _loading = new ProgressDialog(context);
        _loading.setTitle("Reservation");
        _loading.setMessage("Please wait...");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        View reservationDialog = LayoutInflater.from(context).inflate(R.layout.reservation_dialog, null);
        AlertDialog.Builder reservationDialogBuilder = new AlertDialog.Builder(context);

        reservationDialogBuilder.setView(reservationDialog);

        _closeDialogBtn = reservationDialog.findViewById(R.id.close_btn);
        AutoCompleteTextView _day_night = reservationDialog.findViewById(R.id.day_night);
        TextView room_no_text = reservationDialog.findViewById(R.id.room_no_text);
        CircleImageView room_pic = reservationDialog.findViewById(R.id.room_pic);
        TextInputEditText your_name_ET = reservationDialog.findViewById(R.id.your_name_ET);
        TextInputEditText your_contact_ET = reservationDialog.findViewById(R.id.your_contact_ET);
        MaterialButton getLocationBtn = reservationDialog.findViewById(R.id.getLocationBtn);
        MaterialButton getDateBtn = reservationDialog.findViewById(R.id.getDateBtn);
        reserved_btn = reservationDialog.findViewById(R.id.reserved_btn);
        TextInputEditText daysET = reservationDialog.findViewById(R.id.daysET);
        MaterialButton from_day_btn = reservationDialog.findViewById(R.id.from_day_btn_reserved);
        MaterialButton to_day_btn = reservationDialog.findViewById(R.id.to_day_btn_reserved);
        TextInputEditText guestNumberET = reservationDialog.findViewById(R.id.guestNumberET);

        final AlertDialog reservationDialogs = reservationDialogBuilder.create();

        Objects.requireNonNull(reservationDialogs.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        reservationDialogs.setCanceledOnTouchOutside(false);

        from_day_btn.setOnClickListener(view -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    context,
                    (TimePickerDialog.OnTimeSetListener) (timePicker, hourOfDay, minute) -> {
                        t1Hour = hourOfDay;
                        t1Minute = minute;

                        Calendar calendar1 = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
                        calendar1.set(0,0,0,t1Hour,t1Minute);
                        from_day_btn.setText(sdf.format(calendar1.getTime()));
                    }, 12, 0 , false);
            timePickerDialog.updateTime(t1Hour, t1Minute);
            timePickerDialog.show();
        });
        to_day_btn.setOnClickListener(view -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    context,
                    (TimePickerDialog.OnTimeSetListener) (timePicker, hourOfDay, minute) -> {
                        t1HourTo = hourOfDay;
                        t1MinuteTo = minute;

                        Calendar calendar1 = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
                        calendar1.set(0,0,0,t1HourTo,t1MinuteTo);
                        to_day_btn.setText(sdf.format(calendar1.getTime()));
                    }, 12, 0 , false);
            timePickerDialog.updateTime(t1HourTo, t1MinuteTo);
            timePickerDialog.show();
        });

        to_day_btn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                calculateDuration();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

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
                pricePerHour = price / 24;
                pricePerMinutes = pricePerHour / 60;
            } else if (item.equals("Night")) {
                price = rooms.getNIGHT_PRICE();
                pricePerHour = price / 24;
                pricePerMinutes = pricePerHour / 60;
            }
        });

        room_no_text.setText("Reserved ROOM " + rooms.getROOM_NO());
        your_name_ET.setText(name);

        Picasso.get().load(rooms.getROOM_PHOTO_URL().get(roomsModels.size())).into(room_pic);

        _closeDialogBtn.setOnClickListener(view -> reservationDialogs.cancel());

        getLocationBtn.setOnClickListener(view -> {
            //check permission
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start getting the location
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
                                    assert addresses != null;
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
            } else {
                // Request location permission
                ActivityCompat.requestPermissions(activity,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
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
            String your_name = Objects.requireNonNull(your_name_ET.getText()).toString();
            String your_phone = Objects.requireNonNull(your_contact_ET.getText()).toString();
            String dayNight = _day_night.getText().toString();
            String your_location = getLocationBtn.getText().toString();
            String reservation_date = getDateBtn.getText().toString();
            String day_stayed = Objects.requireNonNull(daysET.getText()).toString();
            String fromHours_stayed = from_day_btn.getText().toString();
            String toHours_stayed = to_day_btn.getText().toString();
            String guest_no = Objects.requireNonNull(guestNumberET.getText()).toString();
            float days_price_total = price * Integer.parseInt(day_stayed);
            float hours_price_total = pricePerHour * hours_consumed;
            float minutes_price_total = pricePerMinutes * minutes_consumed;
            float total_payment = days_price_total + hours_price_total + minutes_price_total;
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
            } else if (TextUtils.isEmpty(guest_no)) {
                Toast.makeText(context, "Guest Number is required.", Toast.LENGTH_SHORT).show();
                guestNumberET.requestFocus();
                _loading.dismiss();
            } else if (TextUtils.isEmpty(day_stayed)) {
                Toast.makeText(context, "Day Stayed is required.", Toast.LENGTH_SHORT).show();
                daysET.requestFocus();
                _loading.dismiss();
            } else if (fromHours_stayed.equals("FROM")) {
                Toast.makeText(context, "Please select hours stayed.", Toast.LENGTH_SHORT).show();
                _loading.dismiss();
            } else if (toHours_stayed.equals("TO")) {
                Toast.makeText(context, "Please select hours stayed.", Toast.LENGTH_SHORT).show();
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
                    assert user != null;
                    DBQuery.setReservationRoomUser(
                            rooms.getOWNER_UID(),
                            user.getUid(),
                            "Rooms",
                            Integer.parseInt(day_stayed),
                            fromHours_stayed + " - " + toHours_stayed,
                            Integer.parseInt(guest_no),
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
                            total_payment,
                            false,
                            userPic,
                            rooms.getROOM_PHOTO_URL().get(roomsModels.size())
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

        reservedBtn.setOnClickListener(view -> {
            reservationDialogs.show();
        });
    }
    // Method to calculate and display the duration
    private void calculateDuration() {
        if (t1HourTo >= 0 && t1MinuteTo >= 0 && t1Hour >= 0 && t1Minute >= 0) {
            long fromMillis = (long) t1Hour * 60 * 60 * 1000 + (long) t1Minute * 60 * 1000;
            long toMillis = (long) t1HourTo * 60 * 60 * 1000 + (long) t1MinuteTo * 60 * 1000;

            // Handle cases where the "To" time is earlier than the "From" time
            if (toMillis < fromMillis) {
                toMillis += TimeUnit.DAYS.toMillis(1); // Add 1 day to "To" time
            }

            long durationMillis = toMillis - fromMillis;

            long hours = TimeUnit.MILLISECONDS.toHours(durationMillis);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis) % 60;

            // Display the duration
            //String duration = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
            // Display the duration as separate integers
            displayDurationAsIntegers((int) hours, (int) minutes);
        }
    }
    // Method to display the duration as separate integers
    private void displayDurationAsIntegers(int hours, int minutes) {
        // Here you can use the hours and minutes as needed.
        // For example, you can update TextViews or perform other actions.
        //Log.d("Duration", "Hours: " + hours + ", Minutes: " + minutes);
        hours_consumed = hours;
        minutes_consumed = minutes;
        Toast.makeText(context, ""+hours_consumed+" hrs " + minutes_consumed + " minutes", Toast.LENGTH_SHORT).show();
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
                    UserName + " reserved room " + book_no + "."
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

        return currencyFormatter.format(amount);
    }
}
