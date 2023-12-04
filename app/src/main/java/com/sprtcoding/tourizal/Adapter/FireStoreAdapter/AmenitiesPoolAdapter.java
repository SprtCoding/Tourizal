package com.sprtcoding.tourizal.Adapter.FireStoreAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sprtcoding.tourizal.Model.FSModel.CottageModelFS;
import com.sprtcoding.tourizal.Model.FSModel.PoolModel;
import com.sprtcoding.tourizal.R;
import com.sprtcoding.tourizal.UsersViewPost.AmenitiesDetails;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AmenitiesPoolAdapter extends RecyclerView.Adapter<AmenitiesPoolAdapter.ViewHolder>{
    Context context;
    List<PoolModel> poolModelList;

    public AmenitiesPoolAdapter(Context context, List<PoolModel> poolModelList) {
        this.context = context;
        this.poolModelList = poolModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.amenities_pool_list,parent,false);
        return new AmenitiesPoolAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PoolModel pool = poolModelList.get(position);

        Picasso
                .get()
                .load(pool.getPOOL_IMG_URL())
                .placeholder(R.drawable.pool_icon)
                .into(holder.poolPhoto);

        holder.poolName.setText("Pool " + pool.getPOOL_NO());
        holder.poolType.setText(pool.getPOOL_TYPE() + " Pool");
        holder.poolSwimmer.setText("For " + pool.getPOOL_INFO());

        holder.itemView.setOnClickListener(view -> {
            Intent i = new Intent(context, AmenitiesDetails.class);
            i.putExtra("AmenitiesType", "Pool");
            i.putExtra("Details", pool.getPOOL_DESC());
            i.putExtra("RoomPicURL", pool.getPOOL_IMG_URL());
            i.putExtra("RoomName", pool.getPOOL_NO());
            i.putExtra("Pool_swimmer", pool.getPOOL_INFO());
            i.putExtra("Pool_type", pool.getPOOL_TYPE());
            context.startActivity(i);
        });

    }

    @Override
    public int getItemCount() {
        return poolModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView poolPhoto;
        TextView poolName, poolType, poolSwimmer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            poolPhoto = itemView.findViewById(R.id.poolPhoto);
            poolName = itemView.findViewById(R.id.poolName);
            poolType = itemView.findViewById(R.id.poolType);
            poolSwimmer = itemView.findViewById(R.id.poolSwimmer);
        }
    }
}
