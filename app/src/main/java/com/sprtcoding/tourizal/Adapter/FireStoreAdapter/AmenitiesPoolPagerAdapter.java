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
import com.sprtcoding.tourizal.Model.FSModel.PoolModel;
import com.sprtcoding.tourizal.R;
import com.sprtcoding.tourizal.UsersViewPost.AmenitiesDetails;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AmenitiesPoolPagerAdapter extends PagerAdapter {
    Context context;
    List<PoolModel> poolModelList;

    public AmenitiesPoolPagerAdapter(Context context, List<PoolModel> poolModelList) {
        this.context = context;
        this.poolModelList = poolModelList;
    }
    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.amenities_pool_list, container, false);

        final TextView poolName = view.findViewById(R.id.poolName);
        final ImageView poolPhoto = view.findViewById(R.id.poolPhoto);
        final TextView poolType = view.findViewById(R.id.poolType);
        final TextView poolSwimmer = view.findViewById(R.id.poolSwimmer);

        PoolModel pool = poolModelList.get(position);

        Picasso
                .get()
                .load(pool.getPOOL_IMG_URL())
                .placeholder(R.drawable.pool_icon)
                .into(poolPhoto);

        poolName.setText("Pool " + pool.getPOOL_NO());
        poolType.setText(pool.getPOOL_TYPE() + " Pool");
        poolSwimmer.setText("For " + pool.getPOOL_INFO());

        view.setOnClickListener(view1 -> {
            Intent i = new Intent(context, AmenitiesDetails.class);
            i.putExtra("AmenitiesType", "Pool");
            i.putExtra("Details", pool.getPOOL_DESC());
            i.putExtra("RoomPicURL", pool.getPOOL_IMG_URL());
            i.putExtra("RoomName", pool.getPOOL_NO());
            i.putExtra("Pool_swimmer", pool.getPOOL_INFO());
            i.putExtra("Pool_type", pool.getPOOL_TYPE());
            context.startActivity(i);
        });

        container.addView(view);

        return view;
    }
    @Override
    public int getCount() {
        return poolModelList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
