package com.sprtcoding.tourizal.Adapter.FireStoreAdapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sprtcoding.tourizal.FCM.FCMNotificationSender;
import com.sprtcoding.tourizal.FireStoreDB.DBQuery;
import com.sprtcoding.tourizal.FireStoreDB.MyCompleteListener;
import com.sprtcoding.tourizal.Model.FSModel.ReservationModelFS;
import com.sprtcoding.tourizal.R;
import com.sprtcoding.tourizal.UserMenuFragment.UserOtherOptions.MyReservedDetails;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MyReservationAdapterFS extends RecyclerView.Adapter<MyReservationAdapterFS.ViewHolder>{
    Context context;
    List<ReservationModelFS> reservationModelFSList;
    AlertDialog.Builder cancelAlertBuilder;
    double price = 0;
    private FirebaseFirestore db;
    private CollectionReference reservedColRef;
    String userToken, amenities;

    public MyReservationAdapterFS(Context context, List<ReservationModelFS> reservationModelFSList) {
        this.context = context;
        this.reservationModelFSList = reservationModelFSList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_reservation_list,parent,false);
        return new MyReservationAdapterFS.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReservationModelFS reservation = reservationModelFSList.get(position);

        ProgressDialog loading = new ProgressDialog(context);
        loading.setTitle("Delete");
        loading.setMessage("Please wait...");

        db = FirebaseFirestore.getInstance();
        reservedColRef = db.collection("RESERVATION");

        DBQuery.g_firestore = FirebaseFirestore.getInstance();

        if(reservation.getAMENITIES_TYPE().equals("Rooms")) {
            holder.room_no.setText("Room " + reservation.getAMENITIES_NO());
            amenities = "Room";
        } else if(reservation.getAMENITIES_TYPE().equals("Cottage")) {
            holder.room_no.setText("Cottage " + reservation.getAMENITIES_NO());
            amenities = "Cottage";
        }

        holder.room_price.setText(convertToPhilippinePeso(reservation.getPRICE()));
        Picasso.get().load(reservation.getROOM_PHOTO_URL()).fit().into(holder.pic);

        price = reservation.getPRICE();

        // Define the color for the status part
        int statusDeclinedColor = Color.rgb(191, 64, 64);
        int statusApprovedColor = Color.rgb(49, 149, 91);
        int statusPendingColor = Color.rgb(251, 181, 63);
        int statusCancelledColor = Color.rgb(43, 47, 84);

        switch (reservation.getSTATUS()) {
            case "Approved": {
                // Create a SpannableString
                SpannableString spannableStatus = new SpannableString("Status: " + reservation.getSTATUS());
                // Apply the color only to the status part
                spannableStatus.setSpan(new ForegroundColorSpan(statusApprovedColor), 8, spannableStatus.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.status.setText(spannableStatus);
                break;
            }
            case "Declined": {
                // Create a SpannableString
                SpannableString spannableStatus = new SpannableString("Status: " + reservation.getSTATUS());
                // Apply the color only to the status part
                spannableStatus.setSpan(new ForegroundColorSpan(statusDeclinedColor), 8, spannableStatus.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.status.setText(spannableStatus);
                break;
            }
            case "Pending": {
                // Create a SpannableString
                SpannableString spannableStatus = new SpannableString("Status: " + reservation.getSTATUS());
                // Apply the color only to the status part
                spannableStatus.setSpan(new ForegroundColorSpan(statusPendingColor), 8, spannableStatus.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.status.setText(spannableStatus);
                break;
            }
            case "Cancelled": {
                // Create a SpannableString
                SpannableString spannableStatus = new SpannableString("Status: " + reservation.getSTATUS());
                // Apply the color only to the status part
                spannableStatus.setSpan(new ForegroundColorSpan(statusCancelledColor), 8, spannableStatus.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.status.setText(spannableStatus);
                break;
            }
        }

        cancelAlertBuilder = new AlertDialog.Builder(context);

        if(reservation.getSTATUS().equals("Approved") || reservation.getSTATUS().equals("Declined") || reservation.getSTATUS().equals("Cancelled")) {
            holder.cancel_btn.setVisibility(View.GONE);
        }
        holder.cancel_btn.setOnClickListener(view -> {
            cancelAlertBuilder.setTitle("Cancel Reservation")
                    .setMessage("Are you sure want to cancel " + amenities + " " + reservation.getAMENITIES_NO() + "?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        loading.show();
                        Handler handler = new Handler();
                        Runnable runnable = () -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("STATUS", "Cancelled");

                            reservedColRef.document(reservation.getRESERVED_ID())
                                    .update(map)
                                    .addOnSuccessListener(unused -> {
                                        loading.dismiss();
                                        sendNotification(
                                                reservation.getOWNER_UID(),
                                                reservation.getNAME_OF_USER(),
                                                String.valueOf(reservation.getAMENITIES_NO())
                                        );
                                        Toast.makeText(context, amenities + " " + reservation.getAMENITIES_NO() + " Cancelled.", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show());
                        };
                        handler.postDelayed(runnable, 3000);
                    })
                    .setNegativeButton("No", (dialogInterface, i) -> {
                        dialogInterface.cancel();
                        loading.dismiss();
                    })
                    .show();
        });

        holder.details_btn.setOnClickListener(view -> {
            Intent i = new Intent(context, MyReservedDetails.class);
            i.putExtra("amenities_type", reservation.getAMENITIES_TYPE());
            i.putExtra("reserved_id", reservation.getRESERVED_ID());
            i.putExtra("resort_id", reservation.getRESORT_ID());
            i.putExtra("status", reservation.getSTATUS());
            i.putExtra("room_name", reservation.getAMENITIES_NO());
            i.putExtra("owner_id", reservation.getOWNER_UID());
            i.putExtra("my_name", reservation.getNAME_OF_USER());
            i.putExtra("img", reservation.getROOM_PHOTO_URL());
            i.putExtra("number", reservation.getCONTACT_OF_USER());
            i.putExtra("reserved_date", reservation.getDATE_RESERVATION());
            i.putExtra("payment", price);
            context.startActivity(i);
        });

    }

    @Override
    public int getItemCount() {
        return reservationModelFSList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView room_no, room_price, status, cancel_btn, details_btn;
        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            room_no = itemView.findViewById(R.id.room_no);
            room_price = itemView.findViewById(R.id.room_price);
            status = itemView.findViewById(R.id.status);
            cancel_btn = itemView.findViewById(R.id.cancel_btn);
            details_btn = itemView.findViewById(R.id.details_btn);
            pic = itemView.findViewById(R.id.pic);
        }
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
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            FCMNotificationSender.sendNotification(
                    context,
                    userToken,
                    "TouRizal",
                    name + " Cancelled " + roomName + "."
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
