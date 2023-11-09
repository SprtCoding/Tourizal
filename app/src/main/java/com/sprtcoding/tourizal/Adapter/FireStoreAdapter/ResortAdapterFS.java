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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sprtcoding.tourizal.AdminPost.ResortPost.AddResortPage;
import com.sprtcoding.tourizal.Model.FSModel.ResortFireStoreModel;
import com.sprtcoding.tourizal.R;
import com.squareup.picasso.Picasso;

public class ResortAdapterFS extends FirestoreRecyclerAdapter<ResortFireStoreModel, ResortAdapterFS.ViewHolder> {
    Context context;
    AlertDialog.Builder closeAlertBuilder;
    FirebaseFirestore DB;
    CollectionReference resortCollectionRef;
    FirebaseAuth mAuth;

    public ResortAdapterFS(@NonNull FirestoreRecyclerOptions<ResortFireStoreModel> options) {
        super(options);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ResortFireStoreModel model) {
        ProgressDialog _loading = new ProgressDialog(context);
        _loading.setTitle("Delete");
        _loading.setMessage("Please wait...");

        mAuth = FirebaseAuth.getInstance();

        DB = FirebaseFirestore.getInstance();
        resortCollectionRef = DB.collection("RESORTS");
        StorageReference resortPhotoRef = FirebaseStorage.getInstance().getReference("ResortPhotos/");

        holder._resortName.setText(model.getRESORT_NAME());
        holder._resortOwner.setText("Owned by "+model.getOWNER_NAME());
        Picasso.get().load(model.getRESORT_PIC_URL()).into(holder._resortPhoto);

        closeAlertBuilder = new AlertDialog.Builder(context);

        holder._deleteBtn.setOnClickListener(view -> {
            closeAlertBuilder.setTitle("Delete")
                    .setMessage("Are you sure want to delete " + model.getRESORT_NAME() + "?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        _loading.show();
                        Handler handler = new Handler();
                        Runnable runnable = () -> {
                            resortPhotoRef.child(model.getRESORT_PIC_NAME()+".jpg").delete();
                            resortCollectionRef.document(model.getRESORT_ID()).delete().addOnSuccessListener(unused -> {
                                Toast.makeText(context, model.getRESORT_NAME() + " removed successfully.", Toast.LENGTH_SHORT).show();
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
            Intent i = new Intent(context, AddResortPage.class);
            i.putExtra("isUpdate", true);
            i.putExtra("resortPic", model.getRESORT_PIC_URL());
            i.putExtra("resortName", model.getRESORT_NAME());
            i.putExtra("resortOwner", model.getOWNER_NAME());
            i.putExtra("resortLocation", model.getLOCATION());
            i.putExtra("resortEntrance", model.getRESORT_ENTRANCE_FEE());
            i.putExtra("resortPicName", model.getRESORT_PIC_NAME());
            i.putExtra("resortID", model.getRESORT_ID());
            context.startActivity(i);
            // Finish the current activity
            ((Activity) context).finish();
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_resort_list,parent,false);
        return new ResortAdapterFS.ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView _resortPhoto;
        TextView _resortName, _resortOwner, _editBtn, _deleteBtn;
        CardView resortCard;

        public ViewHolder(@NonNull View itemView) {
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
