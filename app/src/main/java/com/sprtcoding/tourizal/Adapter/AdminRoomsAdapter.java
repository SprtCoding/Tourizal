package com.sprtcoding.tourizal.Adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sprtcoding.tourizal.Model.RoomsModel;
import com.sprtcoding.tourizal.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdminRoomsAdapter extends RecyclerView.Adapter<AdminRoomsAdapter.RoomsViewHolder>{
    Context context;
    List<RoomsModel> roomsModels;
    AlertDialog.Builder closeAlertBuilder;
    DatabaseReference roomRef, resortRef;

    public AdminRoomsAdapter(Context context, List<RoomsModel> roomsModels) {
        this.context = context;
        this.roomsModels = roomsModels;
    }

    @NonNull
    @Override
    public RoomsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_rooms_list,parent,false);
        return new AdminRoomsAdapter.RoomsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomsViewHolder holder, int position) {
        RoomsModel rooms = roomsModels.get(position);

        ProgressDialog _loading = new ProgressDialog(context);
        _loading.setTitle("Delete");
        _loading.setMessage("Please wait...");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser _user = mAuth.getCurrentUser();
        FirebaseDatabase mDb = FirebaseDatabase.getInstance();
        roomRef = mDb.getReference("Rooms");
        resortRef = mDb.getReference("Resorts/"+_user.getUid());
        StorageReference roomPhotoRef = FirebaseStorage.getInstance().getReference("RoomsPhotos/");

        Picasso.get().load(rooms.getRoomsPhotoURL()).into(holder._roomPhoto);
        holder._roomName.setText(rooms.getRoomNumber());
        holder._roomDescription.setText(rooms.getDescription());

        closeAlertBuilder = new AlertDialog.Builder(context);

        holder._deleteBtn.setOnClickListener(view -> {
            closeAlertBuilder.setTitle("Delete")
                    .setMessage("Are you sure want to delete " + rooms.getRoomNumber() + "?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        _loading.show();
                        Handler handler = new Handler();
                        Runnable runnable = () -> {
                            _loading.dismiss();
                            roomPhotoRef.child(rooms.getRoomPicID()).child(rooms.getRoomPicName()+".jpg").delete();
                            roomRef.child(_user.getUid()).child(rooms.getRoomsID()).removeValue();
                            resortRef.child("/Rooms/" + rooms.getRoomsID()).removeValue().addOnCompleteListener(task -> {
                                if(task.isComplete()) {
                                    Toast.makeText(context, rooms.getRoomNumber() + " deleted successfully.", Toast.LENGTH_SHORT).show();
                                    _loading.dismiss();
                                }
                            }).addOnFailureListener(e -> {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        };
                        handler.postDelayed(runnable, 3000);
                    })
                    .setNegativeButton("No", (dialogInterface, i) -> {
                        dialogInterface.cancel();
                    })
                    .show();
        });

    }

    @Override
    public int getItemCount() {
        return roomsModels.size();
    }

    public static class RoomsViewHolder extends RecyclerView.ViewHolder {
        ImageView _roomPhoto;
        TextView _roomName, _roomDescription, _editBtn, _deleteBtn;

        public RoomsViewHolder(@NonNull View itemView) {
            super(itemView);

            _roomPhoto = itemView.findViewById(R.id.roomPhoto);
            _roomName = itemView.findViewById(R.id.roomName);
            _roomDescription = itemView.findViewById(R.id.roomDescription);
            _editBtn = itemView.findViewById(R.id.editBtn);
            _deleteBtn = itemView.findViewById(R.id.deleteBtn);

        }
    }
}
