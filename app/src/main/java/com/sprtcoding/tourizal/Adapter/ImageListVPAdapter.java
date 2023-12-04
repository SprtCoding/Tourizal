package com.sprtcoding.tourizal.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.sprtcoding.tourizal.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageListVPAdapter extends PagerAdapter {
    private Context context;
    ArrayList<Uri> imageUrls;
    ArrayList<String> photoUrls;
    boolean isUpdate;

    public ImageListVPAdapter(Context context, ArrayList<Uri> imageUrls, ArrayList<String> photoUrls, boolean isUpdate) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.photoUrls = photoUrls;
        this.isUpdate = isUpdate;
    }

    @Override
    public int getCount() {
        if(isUpdate) {
            return photoUrls.size();
        }else {
            return imageUrls.size();
        }
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.show_image_list, container, false);
        ImageView image = view.findViewById(R.id.uploadImage);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView remove_image = view.findViewById(R.id.removeImage);
        //image.setImageURI(imageUrls.get(position));
        if(isUpdate) {
            Picasso.get().load(photoUrls.get(position)).fit().placeholder(R.drawable.resort).into(image);
            remove_image.setOnClickListener(view1 -> {
                photoUrls.remove(photoUrls.get(position));
                notifyDataSetChanged();
            });
        }else {
            Picasso.get().load(imageUrls.get(position)).fit().placeholder(R.drawable.resort).into(image);
            remove_image.setOnClickListener(view1 -> {
                imageUrls.remove(imageUrls.get(position));
                notifyDataSetChanged();
            });
        }

        container.addView(view);

        return view;
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
