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
import android.os.Parcel;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sprtcoding.tourizal.CustomDateSelector;
import com.sprtcoding.tourizal.FCM.FCMNotificationSender;
import com.sprtcoding.tourizal.FireStoreDB.DBQuery;
import com.sprtcoding.tourizal.FireStoreDB.MyCompleteListener;
import com.sprtcoding.tourizal.Model.FSModel.CottageModelFS;
import com.sprtcoding.tourizal.R;
import com.sprtcoding.tourizal.UsersViewPost.AmenitiesDetails;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AmenitiesCottageAdapter extends RecyclerView.Adapter<AmenitiesCottageAdapter.AmenitiesCottageViewHolder>{
    Context context;
    List<CottageModelFS> cottageModels;
    private FirebaseDatabase db;
    private DatabaseReference ref;
    private ImageView _closeDialogBtn;
    private TextView room_no_text;
    private CircleImageView room_pic;
    private TextInputEditText your_name_ET, your_contact_ET;
    private MaterialButton getLocationBtn, getDateBtn, reserved_btn;
    private String reserved_id, userToken;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    public static FusedLocationProviderClient fusedLocationProviderClient;
    FragmentManager supportFragment;
    MaterialDatePicker<Long> materialDatePicker;
    FirebaseAuth mAuth;
    FirebaseFirestore fDB;
    List<Long> bookedDates;
    float price = 0;

    public AmenitiesCottageAdapter(Context context, List<CottageModelFS> cottageModels, FragmentManager supportFragment) {
        this.context = context;
        this.cottageModels = cottageModels;
        this.supportFragment = supportFragment;
    }

    @NonNull
    @Override
    public AmenitiesCottageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.amenities_cottage_list,parent,false);
        return new AmenitiesCottageAdapter.AmenitiesCottageViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AmenitiesCottageViewHolder holder, int position) {
        CottageModelFS cottage = cottageModels.get(position);

        db = FirebaseDatabase.getInstance();
        ref = db.getReference("Users");

        reserved_id = ref.push().getKey();

        holder.cottageName.setText("Cottage "+cottage.getCOTTAGE_NO());
        holder.cottageFee.setText(convertToPhilippinePeso(cottage.getPRICE()));
        holder.cottageDescription.setText(cottage.getDESCRIPTION());
        Picasso.get().load(cottage.getCOTTAGE_PHOTO_URL()).into(holder.cottageImage);

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
                            viewReservedDialog(holder, cottage, photoURL, name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        holder.itemView.setOnClickListener(view -> {
            Intent i = new Intent(context, AmenitiesDetails.class);
            i.putExtra("AmenitiesType", "Cottage");
            i.putExtra("Details", cottage.getDESCRIPTION());
            i.putExtra("RoomPicURL", cottage.getCOTTAGE_PHOTO_URL());
            i.putExtra("RoomName", cottage.getCOTTAGE_NO());
            context.startActivity(i);
        });

    }

    @SuppressLint("SetTextI18n")
    private void viewReservedDialog(AmenitiesCottageViewHolder holder, CottageModelFS cottage, String photoURL, String name) {
        ProgressDialog _loading;
        _loading = new ProgressDialog(context);
        _loading.setTitle("Reservation");
        _loading.setMessage("Please wait...");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        View reservationDialog = LayoutInflater.from(context).inflate(R.layout.reservation_dialog, null);
        AlertDialog.Builder reservationDialogBuilder = new AlertDialog.Builder(context);

        reservationDialogBuilder.setView(reservationDialog);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextInputLayout daytime_ll = reservationDialog.findViewById(R.id.daytime_ll);

        _closeDialogBtn = reservationDialog.findViewById(R.id.close_btn);
        room_no_text = reservationDialog.findViewById(R.id.room_no_text);
        room_pic = reservationDialog.findViewById(R.id.room_pic);
        your_name_ET = reservationDialog.findViewById(R.id.your_name_ET);
        your_contact_ET = reservationDialog.findViewById(R.id.your_contact_ET);
        getLocationBtn = reservationDialog.findViewById(R.id.getLocationBtn);
        getDateBtn = reservationDialog.findViewById(R.id.getDateBtn);
        reserved_btn = reservationDialog.findViewById(R.id.reserved_btn);

        fDB = FirebaseFirestore.getInstance();

        bookedDates = new ArrayList<>();

        CollectionReference reservedCol = fDB.collection("RESERVATION");
        reservedCol.whereEqualTo("AMENITIES_TYPE", "Cottage")
                        .addSnapshotListener((value, error) -> {
                            if(error == null && value != null) {
                                if(!value.isEmpty()) {
                                    for(QueryDocumentSnapshot doc : value) {
                                        // Get the booked date field from your Firestore document
                                        String bookedDate = doc.getString("DATE_RESERVATION");
                                        if (bookedDate != null) {
                                            // Parse the string date to a Date object
                                            try {
                                                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd yyyy", Locale.getDefault());
                                                Date date = dateFormat.parse(bookedDate);
                                                // Convert the Date object to a timestamp (milliseconds since Unix epoch)
                                                Long timestamp = date.getTime();
                                                bookedDates.add(timestamp);
                                            } catch (ParseException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    }
                                    //getCalendarConstraints(bookedDates);
                                }
                            } else {
                                Toast.makeText(context, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

        daytime_ll.setVisibility(View.GONE);

        final AlertDialog reservationDialogs = reservationDialogBuilder.create();

        Objects.requireNonNull(reservationDialogs.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        reservationDialogs.setCanceledOnTouchOutside(false);

        room_no_text.setText("Reserved COTTAGE " + cottage.getCOTTAGE_NO());
        your_name_ET.setText(name);

        Picasso.get().load(cottage.getCOTTAGE_PHOTO_URL()).into(room_pic);

        _closeDialogBtn.setOnClickListener(view -> reservationDialogs.cancel());

        getLocationBtn.setOnClickListener(view -> {
            checkLocationPermission();
        });

        getDateBtn.setOnClickListener(view -> {
            // Create a custom date validator to disable already booked dates.
            CalendarConstraints.DateValidator customDateValidator = new CalendarConstraints.DateValidator() {
                @Override
                public boolean isValid(long date) {
                    return !bookedDates.contains(date);
                }

                @Override
                public int describeContents() {
                    return 0;
                }

                @Override
                public void writeToParcel(Parcel dest, int flags) {
                    // You can leave this empty.
                }
            };

            // Create the constraints with the custom date validator.
            CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
            constraintsBuilder.setValidator(customDateValidator);
            CalendarConstraints calendarConstraints = constraintsBuilder.build();

            // Create a date picker builder
            materialDatePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date to Reserved")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setCalendarConstraints(calendarConstraints)
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
                            cottage.getOWNER_UID(),
                            user.getUid(),
                            "Cottage",
                            false,
                            cottage.getRESORT_ID(),
                            cottage.getCOTTAGE_ID(),
                            reserved_id,
                            cottage.getCOTTAGE_NO(),
                            your_name,
                            your_phone,
                            your_location,
                            reservation_date,
                            time,
                            date,
                            "Pending",
                            "",
                            cottage.getPRICE(),
                            false,
                            photoURL,
                            cottage.getCOTTAGE_PHOTO_URL()
                            , new MyCompleteListener() {
                                @Override
                                public void onSuccess() {
                                    sendNotification(cottage.getOWNER_UID(), your_name, String.valueOf(cottage.getCOTTAGE_NO()));
                                    Toast.makeText(context, "Cottage " + cottage.getCOTTAGE_NO() + " reserved successfully.", Toast.LENGTH_SHORT).show();
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

        holder.reserveBtn.setOnClickListener(view -> {
            reservationDialogs.show();
        });
    }

    // Calculate the difference in days between two timestamps.
    private int getDaysDifference(Long startTimestamp, Long endTimestamp) {
        long difference = endTimestamp - startTimestamp;
        return (int) (difference / (24 * 60 * 60 * 1000));
    }

//    private void updateDatePickerConstraints(List<Long> bookedDates) {
//        // Create a CalendarConstraints.DateValidator that checks for booked dates
//        CalendarConstraints.DateValidator dateValidator = new CalendarConstraints.DateValidator() {
//            @Override
//            public boolean isValid(long date) {
//                // Check if the date is not in the bookedDates list
//                return !bookedDates.contains(date);
//            }
//
//            @Override
//            public int describeContents() {
//                return 0;
//            }
//
//            @Override
//            public void writeToParcel(Parcel dest, int flags) {
//            }
//        };
//
//        // Create a new CalendarConstraints.Builder
//        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
//
//        // Set the start and end dates for the constraints (adjust these as needed)
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.YEAR, -1); // Example: Set the start date to one year ago
//        long minDate = calendar.getTimeInMillis();
//        calendar.add(Calendar.YEAR, 2); // Example: Set the end date to two years from now
//        long maxDate = calendar.getTimeInMillis();
//        constraintsBuilder.setStart(minDate);
//        constraintsBuilder.setEnd(maxDate);
//
//        // Set the date validator to the constraints
//        constraintsBuilder.setValidator(dateValidator);
//
//        materialDatePicker = MaterialDatePicker.Builder.datePicker()
//                .setTitleText("Select Date")
//                .setCalendarConstraints(constraintsBuilder.build())
//                .build();
//    }

//    private CalendarConstraints getCalendarConstraints(List<Long> bookedDates) {
//        // Get the current year
//        Calendar calendar = Calendar.getInstance();
//        int currentYear = calendar.get(Calendar.YEAR);
//
//        // Define the date range (you can customize the range)
//        CalendarConstraints.DateValidator dateValidator = new CalendarConstraints.DateValidator() {
//            @Override
//            public boolean isValid(long date) {
//                // Check if the date is within the allowed range and not in the booked dates list
//                calendar.setTimeInMillis(date);
//                int year = calendar.get(Calendar.YEAR);
//                return year == currentYear && !bookedDates.contains(date);
//            }
//
//            @Override
//            public int describeContents() {
//                return 0;
//            }
//
//            @Override
//            public void writeToParcel(Parcel dest, int flags) {
//            }
//        };
//
//        // Create the constraints
//        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
//        constraintsBuilder.setStart(System.currentTimeMillis());
//        constraintsBuilder.setValidator(dateValidator);
//
//        return constraintsBuilder.build();
//    }

    @Override
    public int getItemCount() {
        return cottageModels.size();
    }

    public static class AmenitiesCottageViewHolder extends RecyclerView.ViewHolder {
        ImageView cottageImage;
        TextView cottageName, cottageFee, reserveBtn, cottageDescription;

        public AmenitiesCottageViewHolder(@NonNull View itemView) {
            super(itemView);
            cottageDescription = itemView.findViewById(R.id.cottageDescription);
            cottageImage = itemView.findViewById(R.id.cottagePhoto);
            cottageName = itemView.findViewById(R.id.cottageName);
            cottageFee = itemView.findViewById(R.id.cottageFee);
            reserveBtn = itemView.findViewById(R.id.reserveBtn);
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, start getting the location
            getLocation();
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(((Activity) context),
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
                    UserName + " booked Cottage " + book_no + "."
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
