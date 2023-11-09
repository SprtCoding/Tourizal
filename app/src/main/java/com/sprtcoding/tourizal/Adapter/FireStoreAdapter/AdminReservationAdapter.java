package com.sprtcoding.tourizal.Adapter.FireStoreAdapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sprtcoding.tourizal.AdminMenu.Reservation.Details;
import com.sprtcoding.tourizal.FCM.FCMNotificationSender;
import com.sprtcoding.tourizal.FireStoreDB.DBQuery;
import com.sprtcoding.tourizal.FireStoreDB.MyCompleteListener;
import com.sprtcoding.tourizal.Model.FSModel.ReservationModelFS;
import com.sprtcoding.tourizal.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminReservationAdapter extends RecyclerView.Adapter<AdminReservationAdapter.ViewHolder>{
    Context context;
    List<ReservationModelFS> reservationModelFSList;
    String my_name, userToken, resort_name, price;
    ProgressDialog loading;
    private FirebaseFirestore db;
    private CollectionReference reservedColRef, resortColRef;

    public AdminReservationAdapter(Context context, List<ReservationModelFS> reservationModelFSList) {
        this.context = context;
        this.reservationModelFSList = reservationModelFSList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_reservation_list,parent,false);
        return new AdminReservationAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReservationModelFS reserved = reservationModelFSList.get(position);

        db = FirebaseFirestore.getInstance();
        reservedColRef = db.collection("RESERVATION");
        resortColRef = db.collection("RESORTS");

        resortColRef.document(reserved.getRESORT_ID())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        resort_name = task.getResult().getString("RESORT_NAME");
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show());

        loading = new ProgressDialog(context);
        loading.setTitle("Loading");
        loading.setMessage("Please wait...");
        loading.setCancelable(false);

        Picasso.get().load(reserved.getUSER_PHOTO_URL()).fit().into(holder.user_pic);
        holder.user_name.setText(reserved.getNAME_OF_USER());
        holder.user_contact.setText(reserved.getCONTACT_OF_USER());

        if(reserved.getAMENITIES_TYPE().equals("Rooms")) {
            holder.room_no.setText("Booked Room " + reserved.getAMENITIES_NO());
        } else {
            holder.room_no.setText("Booked Cottage " + reserved.getAMENITIES_NO());
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        price = String.valueOf(reserved.getPRICE());

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
        userRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    my_name = snapshot.child("Fullname").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        DBQuery.g_firestore = FirebaseFirestore.getInstance();

        if(!reserved.isREAD()) {
            holder.reserved_card.setCardBackgroundColor(Color.rgb(205, 205, 205));
        }else {
            holder.reserved_card.setCardBackgroundColor(Color.WHITE);
        }

        if(reserved.getSTATUS().equals("Approved") || reserved.getSTATUS().equals("Declined") || reserved.getSTATUS().equals("Cancelled")) {
            holder.accept_btn.setVisibility(View.GONE);
            holder.reject_btn.setText("Delete");

            holder.reject_btn.setOnClickListener(view -> {
                loading.show();
                Map<String, Object> map = new HashMap<>();
                map.put("REMOVED", true);
                reservedColRef.document(reserved.getRESERVED_ID())
                        .update(map)
                        .addOnSuccessListener(unused -> {
                            loading.dismiss();
                            Toast.makeText(context, "Removed successfully!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show());
            });
        }else {
            holder.reject_btn.setOnClickListener(view -> {
                loading.show();
                Map<String, Object> map = new HashMap<>();
                map.put("STATUS", "Declined");

                reservedColRef.document(reserved.getRESERVED_ID())
                        .update(map)
                        .addOnSuccessListener(unused -> {
                            loading.dismiss();
                            sendDeclineNotification(
                                    reserved.getMY_UID(),
                                    reserved.getNAME_OF_USER(),
                                    resort_name
                            );
                            Toast.makeText(context, "Declined successfully!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show());
            });
        }

        holder.see_details.setOnClickListener(view -> {
            loading.show();
            DBQuery.updateIsRead(
                    true,
                    reserved.getRESERVED_ID(),
                    reserved.getRESORT_ID()
            );

            DBQuery.updateIsReadAll(
                    true,
                    reserved.getRESERVED_ID(),
                    new MyCompleteListener() {
                        @Override
                        public void onSuccess() {
                            loading.dismiss();
                            Intent i = new Intent(context, Details.class);
                            i.putExtra("my_name", my_name);
                            i.putExtra("amenities_type", reserved.getAMENITIES_TYPE());
                            i.putExtra("user_name", reserved.getNAME_OF_USER());
                            i.putExtra("user_id", reserved.getMY_UID());
                            i.putExtra("room_name", reserved.getAMENITIES_NO());
                            i.putExtra("img", reserved.getROOM_PHOTO_URL());
                            i.putExtra("reserved_date", reserved.getDATE_RESERVATION());
                            i.putExtra("number", reserved.getCONTACT_OF_USER());
                            i.putExtra("status", reserved.getSTATUS());
                            i.putExtra("reserved_id", reserved.getRESERVED_ID());
                            i.putExtra("resort_id", reserved.getRESORT_ID());
                            i.putExtra("pp", price);
                            context.startActivity(i);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            loading.dismiss();
                            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        });

        holder.accept_btn.setOnClickListener(view -> {
            loading.show();
            Map<String, Object> map = new HashMap<>();
            map.put("STATUS", "Approved");

            reservedColRef.document(reserved.getRESERVED_ID())
                    .update(map)
                    .addOnSuccessListener(unused -> {
                        loading.dismiss();
                        sendNotification(
                                reserved.getMY_UID(),
                                reserved.getNAME_OF_USER(),
                                resort_name
                        );
                        Toast.makeText(context, "Approved successfully!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show());
        });

    }

    @Override
    public int getItemCount() {
        return reservationModelFSList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView user_pic;
        CardView reserved_card;
        TextView user_name, user_contact, see_details, accept_btn, reject_btn, room_no;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            user_name = itemView.findViewById(R.id.user_name);
            user_contact = itemView.findViewById(R.id.user_contact);
            see_details = itemView.findViewById(R.id.see_details);
            user_pic = itemView.findViewById(R.id.user_pic);
            reserved_card = itemView.findViewById(R.id.reserved_card);
            accept_btn = itemView.findViewById(R.id.accept);
            reject_btn = itemView.findViewById(R.id.reject);
            room_no = itemView.findViewById(R.id.room_no);
        }
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
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            FCMNotificationSender.sendNotification(
                    context,
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
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            FCMNotificationSender.sendNotification(
                    context,
                    userToken,
                    "TouRizal",
                    "Hello,\n"+ name + "\n" +
                            resortName +
                            " Declined your booking."
            );
        }, 3000);
    }
}
