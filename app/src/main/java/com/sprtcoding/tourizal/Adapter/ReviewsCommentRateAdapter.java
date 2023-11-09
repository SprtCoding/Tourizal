package com.sprtcoding.tourizal.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sprtcoding.tourizal.Model.FSModel.ReviewsModelFS;
import com.sprtcoding.tourizal.Model.ReviewModel;
import com.sprtcoding.tourizal.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewsCommentRateAdapter extends RecyclerView.Adapter<ReviewsCommentRateAdapter.ReviewHolder>{
    List<ReviewsModelFS> reviewModels;

    public ReviewsCommentRateAdapter(List<ReviewsModelFS> reviewModels) {
        this.reviewModels = reviewModels;
    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_reviews_comment_list,parent,false);
        return new ReviewsCommentRateAdapter.ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewHolder holder, int position) {
        ReviewsModelFS reviews = reviewModels.get(position);

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");

        userRef.child(reviews.getUSER_ID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String PhotoURL = snapshot.child("PhotoURL").getValue(String.class);
                    try {
                        Picasso.get().load(PhotoURL).into(holder.profilePic);
                    }catch (Exception e) {
                        Picasso.get().load(R.drawable.default_profile).into(holder.profilePic);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw new RuntimeException();
            }
        });

        holder.userName.setText(reviews.getNAME_OF_USER());
        holder.ratingBar.setRating((float) reviews.getRATINGS());
        holder.userComment.setText(reviews.getCOMMENTS());

    }

    @Override
    public int getItemCount() {
        return reviewModels.size();
    }

    public static class ReviewHolder extends RecyclerView.ViewHolder {
        private CircleImageView profilePic;
        private TextView userName, userComment;
        private RatingBar ratingBar;

        public ReviewHolder(@NonNull View itemView) {
            super(itemView);

            profilePic = itemView.findViewById(R.id.profilePic);
            userName = itemView.findViewById(R.id.userName);
            userComment = itemView.findViewById(R.id.userComment);
            ratingBar = itemView.findViewById(R.id.ratingBar);

        }
    }
}
