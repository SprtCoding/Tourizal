package com.sprtcoding.tourizal.Adapter.FireStoreAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
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

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sprtcoding.tourizal.AdminPost.ResortPost.AddCottagePage;
import com.sprtcoding.tourizal.AdminPost.ResortPost.AddRoomsPage;
import com.sprtcoding.tourizal.Model.FSModel.CottageModelFS;
import com.sprtcoding.tourizal.R;
import com.squareup.picasso.Picasso;

public class CottageAdapterFS extends FirestoreRecyclerAdapter<CottageModelFS, CottageAdapterFS.ViewHolder> {
    Context context;
    AlertDialog.Builder closeAlertBuilder;
    FirebaseFirestore DB;
    CollectionReference resortCollectionRef;

    public CottageAdapterFS(@NonNull FirestoreRecyclerOptions<CottageModelFS> options) {
        super(options);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull CottageModelFS model) {
        ProgressDialog _loading = new ProgressDialog(context);
        _loading.setTitle("Delete");
        _loading.setMessage("Please wait...");

        closeAlertBuilder = new AlertDialog.Builder(context);

        DB = FirebaseFirestore.getInstance();
        resortCollectionRef = DB.collection("RESORTS");
        StorageReference cottagePhotoRef = FirebaseStorage.getInstance().getReference("CottagesPhotos/");

        Picasso.get().load(model.getCOTTAGE_PHOTO_URL()).into(holder._cottagePhoto);
        holder._cottageName.setText("Cottage "+model.getCOTTAGE_NO());
        holder._cottageDescription.setText(model.getDESCRIPTION());

        holder._deleteBtn.setOnClickListener(view -> {
            closeAlertBuilder.setTitle("Delete")
                    .setMessage("Are you sure want to delete " + model.getCOTTAGE_NO() + "?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        _loading.show();
                        Handler handler = new Handler();
                        Runnable runnable = () -> {
                            _loading.dismiss();
                            cottagePhotoRef.child(model.getCOTTAGE_PIC_ID()).child(model.getCOTTAGE_PIC_NAME()+".jpg").delete();
                            resortCollectionRef.document(model.getRESORT_ID())
                                    .collection("COTTAGE")
                                    .document(model.getCOTTAGE_ID())
                                    .delete().addOnSuccessListener(unused -> {
                                        Toast.makeText(context, "Room " + String.valueOf(model.getCOTTAGE_NO()) + " removed successfully.", Toast.LENGTH_SHORT).show();
                                        _loading.dismiss();
                                    });
                        };
                        handler.postDelayed(runnable, 3000);
                    })
                    .setNegativeButton("No", (dialogInterface, i) -> {
                        dialogInterface.cancel();
                    })
                    .show();
        });

        holder._editBtn.setOnClickListener(view -> {
            Intent i = new Intent(context, AddCottagePage.class);
            i.putExtra("isUpdate", true);
            i.putExtra("resortID", model.getRESORT_ID());
            i.putExtra("cottageID", model.getCOTTAGE_ID());
            i.putExtra("cottagePicUrl", model.getCOTTAGE_PHOTO_URL());
            i.putExtra("cottageNo", model.getCOTTAGE_NO());
            i.putExtra("cottageDesc", model.getDESCRIPTION());
            i.putExtra("cottageFee", model.getPRICE());
            i.putExtra("cottagePicName", model.getCOTTAGE_PIC_NAME());
            i.putExtra("cottagePicID", model.getCOTTAGE_PIC_ID());
            context.startActivity(i);
            // Finish the current activity
            ((Activity) context).finish();
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_rooms_list,parent,false);
        return new CottageAdapterFS.ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView _cottagePhoto;
        TextView _cottageName, _cottageDescription, _editBtn, _deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            _cottagePhoto = itemView.findViewById(R.id.roomPhoto);
            _cottageName = itemView.findViewById(R.id.roomName);
            _cottageDescription = itemView.findViewById(R.id.roomDescription);
            _editBtn = itemView.findViewById(R.id.editBtn);
            _deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }

    }
}
