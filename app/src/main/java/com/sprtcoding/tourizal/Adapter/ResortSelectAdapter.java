package com.sprtcoding.tourizal.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.sprtcoding.tourizal.AdminPost.ResortPost.AddCottagePage;
import com.sprtcoding.tourizal.AdminPost.ResortPost.AddRoomsPage;
import com.sprtcoding.tourizal.Model.ResortsModel;
import com.sprtcoding.tourizal.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ResortSelectAdapter extends RecyclerView.Adapter<ResortSelectAdapter.ResortSelectViewHolder>{
    Context context;
    List<ResortsModel> resortsModels;
    AlertDialog resortDialog;

    public ResortSelectAdapter(Context context, List<ResortsModel> resortsModels, AlertDialog resortDialog) {
        this.context = context;
        this.resortsModels = resortsModels;
        this.resortDialog = resortDialog;
    }

    @NonNull
    @Override
    public ResortSelectAdapter.ResortSelectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.resort_list_select_list,parent,false);
        return new ResortSelectAdapter.ResortSelectViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ResortSelectViewHolder holder, int position) {
        ResortsModel resort = resortsModels.get(position);

        Picasso.get().load(resort.getResortPicURL()).into(holder._resortPhoto);
        holder._resortName.setText(resort.getResortName());
        holder._resortOwner.setText("Owned by "+resort.getOwner());

        holder._selectBtn.setOnClickListener(view -> {
            resortDialog.dismiss();
            AddRoomsPage._resortID = resort.getResortID();
            AddCottagePage._resortID = resort.getResortID();
        });
    }

    @Override
    public int getItemCount() {
        return resortsModels.size();
    }

    public static class ResortSelectViewHolder extends RecyclerView.ViewHolder {
        ImageView _resortPhoto;
        TextView _resortName, _resortOwner, _selectBtn;

        public ResortSelectViewHolder(@NonNull View itemView) {
            super(itemView);
            _resortPhoto = itemView.findViewById(R.id.resortPhoto);
            _resortName = itemView.findViewById(R.id.resortName);
            _resortOwner = itemView.findViewById(R.id.resortOwner);
            _selectBtn = itemView.findViewById(R.id.selectBtn);
        }
    }
}
