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
import com.sprtcoding.tourizal.Model.FeaturedPostModel;
import com.sprtcoding.tourizal.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FeaturedPostAdapter extends RecyclerView.Adapter<FeaturedPostAdapter.FeaturedViewHolder>{
    Context mContext;
    List<FeaturedPostModel> featuredPostModels;
    AlertDialog.Builder closeAlertBuilder;
    DatabaseReference featuredRef, userFRef;

    public FeaturedPostAdapter(Context mContext, List<FeaturedPostModel> featuredPostModels) {
        this.mContext = mContext;
        this.featuredPostModels = featuredPostModels;
    }

    @NonNull
    @Override
    public FeaturedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.featured_post_list,parent,false);
        return new FeaturedPostAdapter.FeaturedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedViewHolder holder, int position) {
        FeaturedPostModel featuredPost = featuredPostModels.get(position);

        ProgressDialog _loading = new ProgressDialog(mContext);
        _loading.setTitle("Delete");
        _loading.setMessage("Please wait...");

        closeAlertBuilder = new AlertDialog.Builder(mContext);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser _user = mAuth.getCurrentUser();

        featuredRef = FirebaseDatabase.getInstance().getReference("FeaturedPost");
        userFRef = FirebaseDatabase.getInstance().getReference("FeaturedUser");

        StorageReference fPhotoRef = FirebaseStorage.getInstance().getReference("FeaturedPhotos/");

        holder.featured_title.setText(featuredPost.getFeaturedTitle());
        holder.featured_name.setText(featuredPost.getResortName());
        holder.timePosted.setText(featuredPost.getTimePosted());
        holder.datePosted.setText(featuredPost.getDatePosted());
        Picasso.get().load(featuredPost.getThumbnailPhoto()).into(holder.featured_photo);

        holder.deletePost.setOnClickListener(view -> {
            closeAlertBuilder.setTitle("Delete")
                    .setMessage("Are you sure want to delete " + featuredPost.getFeaturedTitle() + "?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        _loading.show();
                        Handler handler = new Handler();
                        Runnable runnable = () -> {
                            _loading.dismiss();
                            fPhotoRef.child(featuredPost.getFeaturedPhotoID()).child(featuredPost.getFeaturedPhotoName()+".jpg").delete();
                            featuredRef.child(_user.getUid()).child(featuredPost.getThumbnailID()).removeValue();
                            userFRef.child(featuredPost.getThumbnailID()).removeValue().addOnCompleteListener(task -> {
                                if(task.isComplete()) {
                                    Toast.makeText(mContext, featuredPost.getFeaturedTitle() + " deleted successfully.", Toast.LENGTH_SHORT).show();
                                    _loading.dismiss();
                                }
                            }).addOnFailureListener(e -> {
                                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
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
        return featuredPostModels.size();
    }

    public static class FeaturedViewHolder extends RecyclerView.ViewHolder {
        ImageView featured_photo;
        TextView featured_title, featured_name, timePosted, datePosted, deletePost, editPost;

        public FeaturedViewHolder(@NonNull View itemView) {
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
