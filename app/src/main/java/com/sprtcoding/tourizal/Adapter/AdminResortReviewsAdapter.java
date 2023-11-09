package com.sprtcoding.tourizal.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sprtcoding.tourizal.Model.FSModel.ReviewsModelFS;
import com.sprtcoding.tourizal.Model.ResortsModel;
import com.sprtcoding.tourizal.Model.ReviewModel;
import com.sprtcoding.tourizal.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdminResortReviewsAdapter extends RecyclerView.Adapter<AdminResortReviewsAdapter.ReviewHolder>{
    private Context context;
    private List<ReviewsModelFS> reviewsModelFS;
    private float ratingSum = 0;

    public AdminResortReviewsAdapter(Context context, List<ReviewsModelFS> reviewsModelFS) {
        this.context = context;
        this.reviewsModelFS = reviewsModelFS;
    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reviews_list,parent,false);
        return new AdminResortReviewsAdapter.ReviewHolder(view);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ReviewHolder holder, int position) {
        ReviewsModelFS reviews = reviewsModelFS.get(position);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser _user = mAuth.getCurrentUser();

        Task<DocumentSnapshot> resortColRef = FirebaseFirestore.getInstance().collection("RESORTS")
                        .document(reviews.getRESORT_ID()).get().addOnSuccessListener(documentSnapshot -> {
                            holder.resortName.setText(documentSnapshot.getString("RESORT_NAME"));

                            try {
                                Picasso.get().load(documentSnapshot.getString("RESORT_PIC_URL")).into(holder.resortPic);
                            }catch (Exception e) {
                                Picasso.get().load(R.drawable.default_profile).into(holder.resortPic);
                            }
                        });

        float rating = (float) reviews.getRATINGS();

        long numberOfReviews = reviewsModelFS.size();

        holder.ratingBar.setRating(rating);
        holder.resortRate.setText(String.format("%.2f", rating));
        holder.resortComments.setText(numberOfReviews + " Comments");

        holder.resortComments.setOnClickListener(view -> {
            holder.hr.setVisibility(View.VISIBLE);
            holder.commentsAndRatesRV.setVisibility(View.VISIBLE);

            LinearLayoutManager llm = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            holder.commentsAndRatesRV.setHasFixedSize(true);
            holder.commentsAndRatesRV.setLayoutManager(llm);

            if(reviews.getCOMMENTS() != null || !reviews.getCOMMENTS().equals("")) {
                ReviewsCommentRateAdapter reviewsCommentRateAdapter = new ReviewsCommentRateAdapter(reviewsModelFS);
                holder.commentsAndRatesRV.setAdapter(reviewsCommentRateAdapter);
            }else {
                holder.no_reviews_ll.setVisibility(View.VISIBLE);
                holder.commentsAndRatesRV.setVisibility(View.GONE);
            }

        });

    }

    @Override
    public int getItemCount() {
        return reviewsModelFS.size();
    }

    public static class ReviewHolder extends RecyclerView.ViewHolder {
        private ImageView resortPic;
        private TextView resortName, resortRate, resortComments;
        private RatingBar ratingBar;
        private RecyclerView commentsAndRatesRV;
        LinearLayout hr, no_reviews_ll;

        public ReviewHolder(@NonNull View itemView) {
            super(itemView);

            resortPic = itemView.findViewById(R.id.resortPic);
            resortName = itemView.findViewById(R.id.resortName);
            resortRate = itemView.findViewById(R.id.resortRate);
            resortComments = itemView.findViewById(R.id.resortComments);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            commentsAndRatesRV = itemView.findViewById(R.id.commentsAndRatesRV);
            hr = itemView.findViewById(R.id.hr);
            no_reviews_ll = itemView.findViewById(R.id.no_reviews_ll);
        }
    }
}
