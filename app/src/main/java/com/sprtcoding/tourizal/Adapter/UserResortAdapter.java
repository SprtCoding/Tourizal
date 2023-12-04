package com.sprtcoding.tourizal.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sprtcoding.tourizal.Model.FSModel.ResortFireStoreModel;
import com.sprtcoding.tourizal.Model.ResortsModel;
import com.sprtcoding.tourizal.R;
import com.sprtcoding.tourizal.UsersViewPost.UserResortInfo;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

public class UserResortAdapter extends RecyclerView.Adapter<UserResortAdapter.ResortFilteredViewHolder>{
    Context mContext;
    List<ResortFireStoreModel> resortsModels;
    private float ratingSum = 0;

    public UserResortAdapter(Context mContext, List<ResortFireStoreModel> resortsModels) {
        this.mContext = mContext;
        this.resortsModels = resortsModels;
    }
    @NonNull
    @Override
    public ResortFilteredViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.resort_filtered_list,parent,false);
        return new UserResortAdapter.ResortFilteredViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ResortFilteredViewHolder holder, int position) {
        ResortFireStoreModel resort = resortsModels.get(position);

        holder.resortName.setText(resort.getRESORT_NAME());
        holder.resortLocation.setText(resort.getLOCATION());
        Picasso.get().load(resort.getRESORT_PIC_URL()).into(holder.resortPic);

        holder._resort_item_card.setOnClickListener(view -> {
            Intent gotoResortInfo = new Intent(mContext, UserResortInfo.class);
            gotoResortInfo.putExtra("UID", resort.getUSER_ID());
            gotoResortInfo.putExtra("ResortID", resort.getRESORT_ID());
            mContext.startActivity(gotoResortInfo);
        });

        FirebaseFirestore DB = FirebaseFirestore.getInstance();
        //CollectionReference resortRef = DB.collection("RESORTS");
        CollectionReference ratingRef = DB.collection("RATINGS");
        DecimalFormat decimalFormat = new DecimalFormat("#.#");

        ratingRef.whereEqualTo("RESORT_ID", resort.getRESORT_ID())
                        .addSnapshotListener((value, error) -> {
                            if(error == null && value != null) {
                                if(!value.isEmpty()) {
                                    ratingSum = 0;
                                    for(QueryDocumentSnapshot doc : value) {
                                        Double rating = doc.getDouble("RATINGS");
                                        ratingSum += rating;
                                    }
                                    long numberOfReviews = value.size();
                                    float avgRating = ratingSum/numberOfReviews;

                                    holder.resortRating.setText(""+decimalFormat.format(avgRating));
                                    holder.resortComments.setText(""+numberOfReviews);
                                }
                            }else {
                                Toast.makeText(mContext, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

//        resortRef.document(resort.getRESORT_ID()).collection("RATINGS")
//                .addSnapshotListener((value, error) -> {
//                    if(error == null && value != null) {
//                        if(!value.isEmpty()) {
//                            ratingSum = 0;
//                            for(QueryDocumentSnapshot doc : value) {
//                                Double rating = doc.getDouble("RATINGS");
//                                ratingSum += rating;
//                            }
//                            long numberOfReviews = value.size();
//                            float avgRating = ratingSum/numberOfReviews;
//
//                            holder.resortRating.setText("" + avgRating);
//                            holder.resortComments.setText(""+numberOfReviews);
//                        }
//                    }else {
//                        Toast.makeText(mContext, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });

    }

    @Override
    public int getItemCount() {
        return resortsModels.size();
    }

    public static class ResortFilteredViewHolder extends RecyclerView.ViewHolder {
        ImageView resortPic;
        TextView resortName, resortLocation, resortRating, resortComments;
        CardView _resort_item_card;

        public ResortFilteredViewHolder(@NonNull View itemView) {
            super(itemView);
            resortPic = itemView.findViewById(R.id.resortUserPic);
            resortName = itemView.findViewById(R.id.resortName);
            resortRating = itemView.findViewById(R.id.resortRating);
            resortLocation = itemView.findViewById(R.id.resortLocationName);
            resortComments = itemView.findViewById(R.id.resortComments);
            _resort_item_card = itemView.findViewById(R.id.resort_item_card);
        }
    }
}
