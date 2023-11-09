package com.sprtcoding.tourizal.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.sprtcoding.tourizal.AdminPost.ResortPost.ManageResort;
import com.sprtcoding.tourizal.Model.CottageModel;
import com.sprtcoding.tourizal.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdminCottageAdapter extends RecyclerView.Adapter<AdminCottageAdapter.CottageViewHolder>{
    Context context;
    List<CottageModel> cottageModels;
    AlertDialog.Builder closeAlertBuilder;
    DatabaseReference cottageRef, resortRef;

    public AdminCottageAdapter(Context context, List<CottageModel> cottageModels) {
        this.context = context;
        this.cottageModels = cottageModels;
    }

    @NonNull
    @Override
    public CottageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_rooms_list,parent,false);
        return new AdminCottageAdapter.CottageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CottageViewHolder holder, int position) {
        CottageModel cottage = cottageModels.get(position);

        ProgressDialog _loading = new ProgressDialog(context);
        _loading.setTitle("Delete");
        _loading.setMessage("Please wait...");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser _user = mAuth.getCurrentUser();
        FirebaseDatabase mDb = FirebaseDatabase.getInstance();
        cottageRef = mDb.getReference("Cottages");
        resortRef = mDb.getReference("Resorts/"+_user.getUid());
        StorageReference cottagePhotoRef = FirebaseStorage.getInstance().getReference("CottagesPhotos/");

        Picasso.get().load(cottage.getCottagePhotoURL()).into(holder._roomPhoto);
        holder._roomName.setText(cottage.getCottageNumber());
        holder._roomDescription.setText(cottage.getDescription());

        closeAlertBuilder = new AlertDialog.Builder(context);

        holder._deleteBtn.setOnClickListener(view -> {
            closeAlertBuilder.setTitle("Delete")
                    .setMessage("Are you sure want to delete " + cottage.getCottageNumber() + "?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        _loading.show();
                        Handler handler = new Handler();
                        Runnable runnable = () -> {
                            _loading.dismiss();
                            cottagePhotoRef.child(cottage.getCottagePicID()).child(cottage.getCottagePicName()+".jpg").delete();
                            cottageRef.child(_user.getUid()).child(cottage.getCottageID()).removeValue();
                            resortRef.child("/Cottages/" + cottage.getCottageID()).removeValue().addOnCompleteListener(task -> {
                                if(task.isComplete()) {
                                    Toast.makeText(context, cottage.getCottageNumber() + " deleted successfully.", Toast.LENGTH_SHORT).show();
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
        return cottageModels.size();
    }

    public static class CottageViewHolder extends RecyclerView.ViewHolder {
        ImageView _roomPhoto;
        TextView _roomName, _roomDescription, _editBtn, _deleteBtn;

        public CottageViewHolder(@NonNull View itemView) {
            super(itemView);
            _roomPhoto = itemView.findViewById(R.id.roomPhoto);
            _roomName = itemView.findViewById(R.id.roomName);
            _roomDescription = itemView.findViewById(R.id.roomDescription);
            _editBtn = itemView.findViewById(R.id.editBtn);
            _deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }
}
