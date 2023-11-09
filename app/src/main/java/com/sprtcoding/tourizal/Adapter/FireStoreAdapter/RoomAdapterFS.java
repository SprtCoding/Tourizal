package com.sprtcoding.tourizal.Adapter.FireStoreAdapter;

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
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sprtcoding.tourizal.Model.FSModel.RoomFSModel;
import com.sprtcoding.tourizal.R;
import com.squareup.picasso.Picasso;

public class RoomAdapterFS extends FirestoreRecyclerAdapter<RoomFSModel, RoomAdapterFS.ViewHolder> {
    Context context;
    AlertDialog.Builder closeAlertBuilder;
    FirebaseFirestore DB;
    CollectionReference resortCollectionRef;
    FirebaseAuth mAuth;

    public RoomAdapterFS(@NonNull FirestoreRecyclerOptions<RoomFSModel> options) {
        super(options);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull RoomFSModel model) {
        Picasso.get().load(model.getROOM_PHOTO_URL()).into(holder._roomPhoto);
        holder._roomName.setText("Room "+ model.getROOM_NO());
        holder._roomDescription.setText(model.getDESCRIPTION());

        ProgressDialog _loading = new ProgressDialog(context);
        _loading.setTitle("Delete");
        _loading.setMessage("Please wait...");

        closeAlertBuilder = new AlertDialog.Builder(context);

        DB = FirebaseFirestore.getInstance();
        resortCollectionRef = DB.collection("RESORTS");
        StorageReference roomPhotoRef = FirebaseStorage.getInstance().getReference("RoomsPhotos/");

        holder._deleteBtn.setOnClickListener(view -> {
            closeAlertBuilder.setTitle("Delete")
                    .setMessage("Are you sure want to delete Room " + String.valueOf(model.getROOM_NO()) + "?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        _loading.show();
                        Handler handler = new Handler();
                        Runnable runnable = () -> {
                            _loading.dismiss();
                            roomPhotoRef.child(model.getROOM_PIC_ID()).child(model.getROOM_PIC_NAME()+".jpg").delete();
                            resortCollectionRef.document(model.getRESORT_ID())
                                    .collection("ROOMS")
                                    .document(model.getROOM_ID())
                                    .delete().addOnSuccessListener(unused -> {
                                Toast.makeText(context, "Room " + String.valueOf(model.getROOM_NO()) + " removed successfully.", Toast.LENGTH_SHORT).show();
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
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_rooms_list,parent,false);
        return new RoomAdapterFS.ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView _roomPhoto;
        TextView _roomName, _roomDescription, _editBtn, _deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            _roomPhoto = itemView.findViewById(R.id.roomPhoto);
            _roomName = itemView.findViewById(R.id.roomName);
            _roomDescription = itemView.findViewById(R.id.roomDescription);
            _editBtn = itemView.findViewById(R.id.editBtn);
            _deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }

    }
}
