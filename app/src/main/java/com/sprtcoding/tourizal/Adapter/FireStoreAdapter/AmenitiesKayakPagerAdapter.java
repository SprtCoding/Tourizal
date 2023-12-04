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
import androidx.viewpager.widget.PagerAdapter;

import com.sprtcoding.tourizal.Model.FSModel.KayakinModel;
import com.sprtcoding.tourizal.Model.FSModel.PoolModel;
import com.sprtcoding.tourizal.R;
import com.sprtcoding.tourizal.UsersViewPost.AmenitiesDetails;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class AmenitiesKayakPagerAdapter extends PagerAdapter {
    Context context;
    List<KayakinModel> kayakinModelList;

    public AmenitiesKayakPagerAdapter(Context context, List<KayakinModel> kayakinModelList) {
        this.context = context;
        this.kayakinModelList = kayakinModelList;
    }
    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.amenities_kayakin_list, container, false);

        final TextView kayakinName = view.findViewById(R.id.kayakinName);
        final ImageView kayakinPhoto = view.findViewById(R.id.kayakinPhoto);
        final TextView kayakinDesc = view.findViewById(R.id.kayakinDesc);
        final TextView rentPrice = view.findViewById(R.id.rentPrice);

        KayakinModel kayak = kayakinModelList.get(position);

        Picasso
                .get()
                .load(kayak.getKAYAKIN_IMG_URL())
                .placeholder(R.drawable.kayak)
                .into(kayakinPhoto);

        kayakinName.setText("Kayak " + kayak.getKAYAKIN_NO());
        kayakinDesc.setText(kayak.getKAYAKIN_DESC());
        rentPrice.setText(convertToPhilippinePeso(kayak.getRENT_PRICE()));

        view.setOnClickListener(view1 -> {
            Intent i = new Intent(context, AmenitiesDetails.class);
            i.putExtra("AmenitiesType", "Kayak");
            i.putExtra("Details", kayak.getKAYAKIN_DESC());
            i.putExtra("RoomPicURL", kayak.getKAYAKIN_IMG_URL());
            i.putExtra("RoomName", kayak.getKAYAKIN_NO());
            context.startActivity(i);
        });

        container.addView(view);

        return view;
    }
    @Override
    public int getCount() {
        return kayakinModelList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
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
