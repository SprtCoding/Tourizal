package com.sprtcoding.tourizal.Adapter.FireStoreAdapter;

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

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sprtcoding.tourizal.Model.FSModel.RatingsList;
import com.sprtcoding.tourizal.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyRatingsAdapter extends RecyclerView.Adapter<MyRatingsAdapter.ViewHolder>{
    Context context;
    List<RatingsList> ratingsLists;

    public MyRatingsAdapter(Context context, List<RatingsList> ratingsLists) {
        this.context = context;
        this.ratingsLists = ratingsLists;
    }

    @NonNull
    @Override
    public MyRatingsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_ratings_list,parent,false);
        return new MyRatingsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRatingsAdapter.ViewHolder holder, int position) {
        RatingsList rating = ratingsLists.get(position);

        CollectionReference resortCol = FirebaseFirestore.getInstance().collection("RESORTS");
        resortCol.document(rating.getRESORT_ID())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()) {
                        String resortPicUrl = documentSnapshot.getString("RESORT_PIC_URL");
                        String resortName = documentSnapshot.getString("RESORT_NAME");

                        holder.name.setText(resortName);
                        Picasso.get().load(resortPicUrl).fit().placeholder(R.drawable.resort).into(holder.resortPic);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show());

        holder.comment.setText(rating.getCOMMENTS());
        holder.rate.setRating(rating.getRATINGS());

    }

    @Override
    public int getItemCount() {
        return ratingsLists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView resortPic;
        TextView name, comment, edit, delete;
        RatingBar rate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            resortPic = itemView.findViewById(R.id.resort_pic);
            comment = itemView.findViewById(R.id.resort_comment);
            name = itemView.findViewById(R.id.resort_name);
            edit = itemView.findViewById(R.id.edit_btn);
            delete = itemView.findViewById(R.id.delete_btn);
            rate = itemView.findViewById(R.id.ratingBar);
        }
    }
}
