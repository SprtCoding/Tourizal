package com.sprtcoding.tourizal.Adapter.FireStoreAdapter;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sprtcoding.tourizal.Model.FSModel.FeaturedModelFS;
import com.sprtcoding.tourizal.R;
import com.squareup.picasso.Picasso;

public class FeaturedAdapterFS extends FirestoreRecyclerAdapter<FeaturedModelFS, FeaturedAdapterFS.ViewHolder> {
    Context context;
    AlertDialog.Builder closeAlertBuilder;
    FirebaseFirestore DB;
    CollectionReference resortCollectionRef, featuredColRef;

    public FeaturedAdapterFS(@NonNull FirestoreRecyclerOptions<FeaturedModelFS> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull FeaturedModelFS model) {
        holder.featured_title.setText(model.getFEATURED_TITLE());
        holder.featured_name.setText(model.getRESORT_NAME());
        holder.timePosted.setText(model.getTIME());
        holder.datePosted.setText(model.getDATE());
        Picasso.get().load(model.getFEATURED_PHOTO_URL()).into(holder.featured_photo);

        ProgressDialog _loading = new ProgressDialog(context);
        _loading.setTitle("Delete");
        _loading.setMessage("Please wait...");

        closeAlertBuilder = new AlertDialog.Builder(context);

        DB = FirebaseFirestore.getInstance();
        resortCollectionRef = DB.collection("RESORTS");
        featuredColRef = DB.collection("FEATURED");
        StorageReference featuredPhotoRef = FirebaseStorage.getInstance().getReference("FeaturedPhotos/");

        holder.deletePost.setOnClickListener(view -> {
            closeAlertBuilder.setTitle("Delete")
                    .setMessage("Are you sure want to delete " + model.getFEATURED_TITLE() + "?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        _loading.show();
                        Handler handler = new Handler();
                        Runnable runnable = () -> {
                            _loading.dismiss();
                            featuredPhotoRef.child(model.getFEATURED_PHOTO_ID()).child(model.getFEATURED_PHOTO_NAME()+".jpg").delete();
                            featuredColRef.document(model.getFEATURED_ID()).delete();
                            resortCollectionRef.document(model.getRESORT_ID())
                                    .collection("FEATURED")
                                    .document(model.getFEATURED_ID())
                                    .delete().addOnSuccessListener(unused -> {
                                        Toast.makeText(context, model.getFEATURED_TITLE() + " removed successfully.", Toast.LENGTH_SHORT).show();
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.featured_post_list,parent,false);
        return new FeaturedAdapterFS.ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView featured_photo;
        TextView featured_title, featured_name, timePosted, datePosted, deletePost, editPost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            featured_photo = itemView.findViewById(R.id.featured_photo);
            featured_title = itemView.findViewById(R.id.featured_title);
            featured_name = itemView.findViewById(R.id.featured_name);
            timePosted = itemView.findViewById(R.id.timePosted);
            datePosted = itemView.findViewById(R.id.datePosted);
            deletePost = itemView.findViewById(R.id.deletePost);
            editPost = itemView.findViewById(R.id.editPost);
        }

    }
}
