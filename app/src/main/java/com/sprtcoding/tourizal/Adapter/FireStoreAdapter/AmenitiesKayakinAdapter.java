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
import androidx.recyclerview.widget.RecyclerView;

import com.sprtcoding.tourizal.Model.FSModel.KayakinModel;
import com.sprtcoding.tourizal.Model.FSModel.PoolModel;
import com.sprtcoding.tourizal.R;
import com.sprtcoding.tourizal.UsersViewPost.AmenitiesDetails;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class AmenitiesKayakinAdapter extends RecyclerView.Adapter<AmenitiesKayakinAdapter.ViewHolder>{
    Context context;
    List<KayakinModel> kayakinModelList;

    public AmenitiesKayakinAdapter(Context context, List<KayakinModel> kayakinModelList) {
        this.context = context;
        this.kayakinModelList = kayakinModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.amenities_kayakin_list,parent,false);
        return new AmenitiesKayakinAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        KayakinModel kayakin = kayakinModelList.get(position);

        Picasso
                .get()
                .load(kayakin.getKAYAKIN_IMG_URL())
                .placeholder(R.drawable.kayak)
                .into(holder.kayakinPhoto);

        holder.kayakinName.setText("Kayak " + kayakin.getKAYAKIN_NO());
        holder.kayakinDesc.setText(kayakin.getKAYAKIN_DESC());
        holder.rentPrice.setText(convertToPhilippinePeso(kayakin.getRENT_PRICE()));

        holder.itemView.setOnClickListener(view -> {
            Intent i = new Intent(context, AmenitiesDetails.class);
            i.putExtra("AmenitiesType", "Kayak");
            i.putExtra("Details", kayakin.getKAYAKIN_DESC());
            i.putExtra("RoomPicURL", kayakin.getKAYAKIN_IMG_URL());
            i.putExtra("RoomName", kayakin.getKAYAKIN_NO());
            context.startActivity(i);
        });

    }

    @Override
    public int getItemCount() {
        return kayakinModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView kayakinPhoto;
        TextView kayakinName, kayakinDesc, rentPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            kayakinPhoto = itemView.findViewById(R.id.kayakinPhoto);
            kayakinName = itemView.findViewById(R.id.kayakinName);
            kayakinDesc = itemView.findViewById(R.id.kayakinDesc);
            rentPrice = itemView.findViewById(R.id.rentPrice);
        }
    }

    public static String convertToPhilippinePeso(double amount) {
        // Create a Locale for the Philippines
        Locale philippinesLocale = new Locale("en", "PH");

        // Create a NumberFormat for currency in the Philippines
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(philippinesLocale);

        // Set the currency code to PHP (Philippine Peso)
        currencyFormatter.setCurrency(Currency.getInstance("PHP"));

        // Format the amount as Philippine Peso currency
        String formattedAmount = currencyFormatter.format(amount);

        return formattedAmount;
    }
}
