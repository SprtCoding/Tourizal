package com.sprtcoding.tourizal.Adapter;

import android.annotation.SuppressLint;
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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sprtcoding.tourizal.Model.FeaturedPostModel;
import com.sprtcoding.tourizal.Model.ResortsModel;
import com.sprtcoding.tourizal.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdminResortAdapter extends RecyclerView.Adapter<AdminResortAdapter.ResortViewHolder>{
    Context context;
    List<ResortsModel> resortsModels;
    AlertDialog.Builder closeAlertBuilder;
    DatabaseReference cottageRef, resortRef, roomRef, userResortRef;

    public AdminResortAdapter(Context context, List<ResortsModel> resortsModels) {
        this.context = context;
        this.resortsModels = resortsModels;
    }

    @NonNull
    @Override
    public ResortViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_resort_list,parent,false);
        return new AdminResortAdapter.ResortViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ResortViewHolder holder, int position) {
        ResortsModel resort = resortsModels.get(position);

        ProgressDialog _loading = new ProgressDialog(context);
        _loading.setTitle("Delete");
        _loading.setMessage("Please wait...");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser _user = mAuth.getCurrentUser();
        FirebaseDatabase mDb = FirebaseDatabase.getInstance();

        userResortRef = mDb.getReference("UsersResorts/" + resort.getResortID());
        cottageRef = mDb.getReference("Cottages/"+_user.getUid());
        roomRef = mDb.getReference("Rooms/"+_user.getUid());
        resortRef = mDb.getReference("Resorts/"+_user.getUid());
        StorageReference resortPhotoRef = FirebaseStorage.getInstance().getReference("ResortPhotos/");

        holder._resortName.setText(resort.getResortName());
        holder._resortOwner.setText("Owned by "+resort.getOwner());
        Picasso.get().load(resort.getResortPicURL()).into(holder._resortPhoto);

        closeAlertBuilder = new AlertDialog.Builder(context);

        holder._deleteBtn.setOnClickListener(view -> {
            closeAlertBuilder.setTitle("Delete")
                    .setMessage("Are you sure want to delete " + resort.getResortName() + "?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        _loading.show();
                        Handler handler = new Handler();
                        Runnable runnable = () -> {
                            _loading.dismiss();
                            resortPhotoRef.child(resort.getResortPicName()+".jpg").delete();
                            cottageRef.removeValue();
                            roomRef.removeValue();
                            userResortRef.removeValue();
                            resortRef.removeValue().addOnCompleteListener(task -> {
                                if(task.isComplete()) {
                                    Toast.makeText(context, resort.getResortName() + " deleted successfully.", Toast.LENGTH_SHORT).show();
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
        return resortsModels.size();
    }

    public static class ResortViewHolder extends RecyclerView.ViewHolder {
        ImageView _resortPhoto;
        TextView _resortName, _resortOwner, _editBtn, _deleteBtn;
        CardView resortCard;

        public ResortViewHolder(@NonNull View itemView) {
            super(itemView);
            _resortPhoto = itemView.findViewById(R.id.resortPhoto);
            _resortName = itemView.findViewById(R.id.resortName);
            _resortOwner = itemView.findViewById(R.id.resortOwner);
            _editBtn = itemView.findViewById(R.id.editBtn);
            _deleteBtn = itemView.findViewById(R.id.deleteBtn);
            resortCard = itemView.findViewById(R.id.resortCard);
        }
    }
}
