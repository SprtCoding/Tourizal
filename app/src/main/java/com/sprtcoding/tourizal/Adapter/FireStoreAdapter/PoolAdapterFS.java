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
import com.sprtcoding.tourizal.AdminPost.ResortPost.AddPoolPage;
import com.sprtcoding.tourizal.Model.FSModel.PoolModel;
import com.sprtcoding.tourizal.R;
import com.squareup.picasso.Picasso;

public class PoolAdapterFS extends FirestoreRecyclerAdapter<PoolModel, PoolAdapterFS.ViewHolder> {
    Context context;
    AlertDialog.Builder closeAlertBuilder;
    FirebaseFirestore DB;
    CollectionReference resortCollectionRef;
    public PoolAdapterFS(@NonNull FirestoreRecyclerOptions<PoolModel> options) {
        super(options);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull PoolModel model) {
        ProgressDialog _loading = new ProgressDialog(context);
        _loading.setTitle("Delete");
        _loading.setMessage("Please wait...");

        closeAlertBuilder = new AlertDialog.Builder(context);

        DB = FirebaseFirestore.getInstance();
        resortCollectionRef = DB.collection("RESORTS");
        StorageReference poolPhotoRef = FirebaseStorage.getInstance().getReference("PoolPhotos/");

        Picasso.get().load(model.getPOOL_IMG_URL()).into(holder.poolPhoto);
        holder.poolNo.setText("Pool "+model.getPOOL_NO());
        holder.poolType.setText(model.getPOOL_TYPE());

        holder.deleteBtn.setOnClickListener(view -> {
            closeAlertBuilder.setTitle("Delete")
                    .setMessage("Are you sure want to delete pool " + model.getPOOL_NO() + "?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        _loading.show();
                        Handler handler = new Handler();
                        Runnable runnable = () -> {
                            _loading.dismiss();
                            poolPhotoRef.child(model.getPOOL_PIC_ID()).child(model.getPOOL_PIC_NAME()+".jpg").delete();
                            resortCollectionRef.document(model.getRESORT_ID())
                                    .collection("POOL")
                                    .document(model.getPOOL_ID())
                                    .delete().addOnSuccessListener(unused -> {
                                        Toast.makeText(context, "Pool " + model.getPOOL_NO() + " removed successfully.", Toast.LENGTH_SHORT).show();
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

        holder.editBtn.setOnClickListener(view -> {
            Intent i = new Intent(context, AddPoolPage.class);
            i.putExtra("isUpdate", true);
            i.putExtra("resortID", model.getRESORT_ID());
            i.putExtra("poolID", model.getPOOL_ID());
            i.putExtra("poolPicUrl", model.getPOOL_IMG_URL());
            i.putExtra("poolNo", model.getPOOL_NO());
            i.putExtra("poolDesc", model.getPOOL_DESC());
            i.putExtra("poolSize", model.getPOOL_SIZE());
            i.putExtra("poolType", model.getPOOL_TYPE());
            i.putExtra("poolSwimmer", model.getPOOL_INFO());
            i.putExtra("poolPicName", model.getPOOL_PIC_NAME());
            i.putExtra("poolPicID", model.getPOOL_PIC_ID());
            context.startActivity(i);
            // Finish the current activity
            ((Activity) context).finish();
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_pool_list,parent,false);
        return new PoolAdapterFS.ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView poolPhoto;
        TextView poolNo, poolType, editBtn, deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            poolPhoto = itemView.findViewById(R.id.poolPhoto);
            poolNo = itemView.findViewById(R.id.poolNo);
            poolType = itemView.findViewById(R.id.poolType);
            editBtn = itemView.findViewById(R.id.editBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }

    }
}
