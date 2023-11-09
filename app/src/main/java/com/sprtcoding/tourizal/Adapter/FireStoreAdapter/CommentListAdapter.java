package com.sprtcoding.tourizal.Adapter.FireStoreAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.sprtcoding.tourizal.Model.FSModel.RatingsList;
import com.sprtcoding.tourizal.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ViewHolder>{
    Context context;
    List<RatingsList> ratingsLists;

    public CommentListAdapter(Context context, List<RatingsList> ratingsLists) {
        this.context = context;
        this.ratingsLists = ratingsLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list,parent,false);
        return new CommentListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RatingsList rating = ratingsLists.get(position);

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
        userRef.child(rating.getUSER_ID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String userPicUrl = snapshot.child("PhotoURL").getValue(String.class);
                    String userName = snapshot.child("Fullname").getValue(String.class);

                    holder.userName.setText(userName);
                    Picasso.get().load(userPicUrl).fit().placeholder(R.drawable.default_profile).into(holder.userPic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.comment.setText(rating.getCOMMENTS());
        holder.date.setText(rating.getDATE_POSTED());
        holder.time.setText(rating.getTIME_POSTED());
        holder.ratingBar.setRating(rating.getRATINGS());
    }

    @Override
    public int getItemCount() {
        return ratingsLists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userPic;
        RatingBar ratingBar;
        TextView comment, date, time, userName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            userName = itemView.findViewById(R.id.userName);
            userPic = itemView.findViewById(R.id.user_pic);
            comment = itemView.findViewById(R.id.comments);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
        }
    }
}
