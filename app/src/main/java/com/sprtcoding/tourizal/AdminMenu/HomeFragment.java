package com.sprtcoding.tourizal.AdminMenu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sprtcoding.tourizal.AdminPost.FeaturedPost;
import com.sprtcoding.tourizal.AdminPost.ManageResortCategory;
import com.sprtcoding.tourizal.Auth.Login;
import com.sprtcoding.tourizal.R;
public class HomeFragment extends Fragment {
    private TextView _viewFeatured, _viewResortPostTV;
    private CardView _featuredPostCard, _resortPostCard;
    ProgressDialog _loading;
    View homeView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        homeView = inflater.inflate(R.layout.fragment_home, container, false);

        _var();

        _loading = new ProgressDialog(getContext());
        _loading.setTitle("Loading");
        _loading.setMessage("Please wait...");

        _viewFeatured.setOnClickListener(view -> {
            _loading.show();
            Handler handler = new Handler();
            Runnable runnable = () -> {
                _loading.dismiss();
                Intent gotoFeaturedPost = new Intent(getContext(), FeaturedPost.class);
                startActivity(gotoFeaturedPost);
            };
            handler.postDelayed(runnable, 2000);
        });

        _featuredPostCard.setOnClickListener(view -> {
            _loading.show();
            Handler handler = new Handler();
            Runnable runnable = () -> {
                _loading.dismiss();
                Intent gotoFeaturedPost = new Intent(getContext(), FeaturedPost.class);
                startActivity(gotoFeaturedPost);
            };
            handler.postDelayed(runnable, 2000);
        });

        _viewResortPostTV.setOnClickListener(view -> {
            _loading.show();
            Handler handler = new Handler();
            Runnable runnable = () -> {
                _loading.dismiss();
                Intent gotoFeaturedPost = new Intent(getContext(), ManageResortCategory.class);
                startActivity(gotoFeaturedPost);
            };
            handler.postDelayed(runnable, 2000);
        });

        _resortPostCard.setOnClickListener(view -> {
            _loading.show();
            Handler handler = new Handler();
            Runnable runnable = () -> {
                _loading.dismiss();
                Intent gotoFeaturedPost = new Intent(getContext(), ManageResortCategory.class);
                startActivity(gotoFeaturedPost);
            };
            handler.postDelayed(runnable, 2000);
        });

        return homeView;
    }

    private void _var() {
        _viewFeatured = homeView.findViewById(R.id.viewFeatured);
        _featuredPostCard = homeView.findViewById(R.id.featuredPostCard);
        _viewResortPostTV = homeView.findViewById(R.id.viewResortPostTV);
        _resortPostCard = homeView.findViewById(R.id.resortPostCard);
    }
}