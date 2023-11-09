package com.sprtcoding.tourizal.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.sprtcoding.tourizal.DirectionMapsAPI.ResortDirection;
import com.sprtcoding.tourizal.Model.FSModel.FeaturedModelFS;
import com.sprtcoding.tourizal.Model.FeaturedPostModel;
import com.sprtcoding.tourizal.R;
import com.sprtcoding.tourizal.UsersViewPost.UserResortInfo;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserFeaturedViewPagerAdapter extends PagerAdapter {
    Context context;
    List<FeaturedModelFS> featuredPostModels;

    public UserFeaturedViewPagerAdapter(Context context, List<FeaturedModelFS> featuredPostModels) {
        this.context = context;
        this.featuredPostModels = featuredPostModels;
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.resort_list, container, false);

        final ImageView resortPic = view.findViewById(R.id.resortPic);
        final TextView resortName = view.findViewById(R.id.resortName);
        final TextView featuredTitle = view.findViewById(R.id.featuredTitle);
        final TextView resortLocation = view.findViewById(R.id.resortLocation);
        final TextView timePosted = view.findViewById(R.id.timePosted);
        final TextView datePosted = view.findViewById(R.id.datePosted);
        final TextView gotoLocationBtn = view.findViewById(R.id.gotoLocationBtn);

        FeaturedModelFS featured = featuredPostModels.get(position);

        Picasso.get().load(featured.getFEATURED_PHOTO_URL()).into(resortPic);
        resortName.setText(featured.getRESORT_NAME());
        featuredTitle.setText(featured.getFEATURED_TITLE());
        resortLocation.setText(featured.getLOCATION());
        timePosted.setText(featured.getTIME());
        datePosted.setText(featured.getDATE());

        gotoLocationBtn.setOnClickListener(view1 -> {

            Intent gotoDirectionLocation = new Intent(context, UserResortInfo.class);
            gotoDirectionLocation.putExtra("Title", featured.getFEATURED_TITLE());
            gotoDirectionLocation.putExtra("ResortID", featured.getRESORT_ID());
            gotoDirectionLocation.putExtra("UID", featured.getOWNER_UID());
            context.startActivity(gotoDirectionLocation);

//            Intent gotoDirectionLocation = new Intent(context, ResortDirection.class);
//            gotoDirectionLocation.putExtra("Lat", featured.getLAT());
//            gotoDirectionLocation.putExtra("Lon", featured.getLNG());
//            gotoDirectionLocation.putExtra("FeaturedID", featured.getFEATURED_ID());
//            gotoDirectionLocation.putExtra("LocationName", featured.getLOCATION());
//            context.startActivity(gotoDirectionLocation);
        });

        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return featuredPostModels.size();
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
