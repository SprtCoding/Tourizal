package com.sprtcoding.tourizal.UserMenuFragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sprtcoding.tourizal.Adapter.UserFeaturedViewPagerAdapter;
import com.sprtcoding.tourizal.Adapter.UserResortAdapter;
import com.sprtcoding.tourizal.Model.FSModel.FeaturedModelFS;
import com.sprtcoding.tourizal.Model.FSModel.ResortFireStoreModel;
import com.sprtcoding.tourizal.Model.FeaturedPostModel;
import com.sprtcoding.tourizal.Model.ResortsModel;
import com.sprtcoding.tourizal.R;
import com.sprtcoding.tourizal.UserMenuFragment.UserOtherOptions.MyRatings;
import com.sprtcoding.tourizal.UserMenuFragment.UserOtherOptions.MyReservation;
import com.sprtcoding.tourizal.UserMenuFragment.UserOtherOptions.Profile;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserHomeFragment extends Fragment {
    private RecyclerView resortFilteredRecycleView;
    private CircleImageView profile;
    private CardView _no_featured_post_card, no_resort_card;
    private AutoCompleteTextView genderCTV;
    private ImageView _reservation_btn, _rating_btn;
    private ViewPager featuredViewPager;
    private TabLayout tabs;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDb;
    private DatabaseReference userRef;
    UserFeaturedViewPagerAdapter userFeaturedViewPagerAdapter;
    UserResortAdapter resortUserAdapter;
    FirebaseFirestore DB;
    CollectionReference featuredCollectionRef, resortColRef;

    List<ResortsModel> resortsModels;
    List<FeaturedPostModel> featuredPostModels;
    List<FeaturedModelFS> featuredModelFS;
    List<ResortFireStoreModel> resortFireStoreModels;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_home, container, false);

        _var();

        featuredModelFS = new ArrayList<>();
        resortFireStoreModels = new ArrayList<>();

        DB = FirebaseFirestore.getInstance();
        featuredCollectionRef = DB.collection("FEATURED");
        resortColRef = DB.collection("RESORTS");

        resortColRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if(!queryDocumentSnapshots.isEmpty()) {
                List<String> dropdownItemsList = new ArrayList<>();
                for(QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    String itemName = doc.getString("RESORT_NAME");
                    dropdownItemsList.add(itemName);
                }
                // Call a method to populate the dropdown with the retrieved data
                populateDropdown(dropdownItemsList);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDb = FirebaseDatabase.getInstance();
        userRef = mDb.getReference("Users");

        if(mUser != null) {
            userRef.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        if(snapshot.hasChild("PhotoURL")) {
                            String photoURL = snapshot.child("PhotoURL").getValue(String.class);
                            try {
                                Picasso.get().load(photoURL).into(profile);
                            }catch (Exception ex) {
                                Picasso.get().load(R.drawable.default_profile).into(profile);
                            }
                        }else {
                            Picasso.get().load(R.drawable.default_profile).into(profile);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        resortsModels = new ArrayList<>();
        featuredPostModels = new ArrayList<>();

        tabs.setupWithViewPager(featuredViewPager);
        featuredViewPager.setClipToPadding(false);
        featuredViewPager.setClipChildren(false);
        featuredViewPager.setPageMargin(30);

        LinearLayoutManager llm_Vertical = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        llm_Vertical.setReverseLayout(true);
        llm_Vertical.setStackFromEnd(true);
        resortFilteredRecycleView.setHasFixedSize(true);
        resortFilteredRecycleView.setLayoutManager(llm_Vertical);

        featuredCollectionRef.addSnapshotListener((value, error) -> {
            if(error == null && value != null) {
                if(!value.isEmpty()) {
                    featuredModelFS.clear();
                    for(QueryDocumentSnapshot snap : value) {
                        featuredModelFS.add(new FeaturedModelFS(
                                snap.getString("OWNER_UID"),
                                snap.getString("RESORT_ID"),
                                snap.getString("FEATURED_ID"),
                                snap.getString("FEATURED_TITLE"),
                                snap.getString("FEATURED_PHOTO_ID"),
                                snap.getString("FEATURED_PHOTO_NAME"),
                                snap.getString("RESORT_NAME"),
                                snap.getString("OWNER_NAME"),
                                snap.getString("LOCATION"),
                                snap.getString("LAT"),
                                snap.getString("LNG"),
                                snap.getString("TIME"),
                                snap.getString("DATE"),
                                snap.getString("FEATURED_PHOTO_URL")
                                ));
                    }
                    userFeaturedViewPagerAdapter = new UserFeaturedViewPagerAdapter(getContext(), featuredModelFS);
                    featuredViewPager.setAdapter(userFeaturedViewPagerAdapter);
                    autoImageSlide();
                }else {
                    featuredViewPager.setVisibility(View.GONE);
                    tabs.setVisibility(View.GONE);
                    _no_featured_post_card.setVisibility(View.VISIBLE);
                }
            }else {
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        resortColRef.addSnapshotListener((value, error) -> {
            if(error == null && value != null) {
                if(!value.isEmpty()) {
                    resortFireStoreModels.clear();
                    for(QueryDocumentSnapshot snap : value) {
                        resortFireStoreModels.add(new ResortFireStoreModel(
                                snap.getString("OWNER_UID"),
                                snap.getString("RESORT_ID"),
                                snap.getString("RESORT_PIC_URL"),
                                snap.getString("RESORT_ENTRANCE_FEE"),
                                snap.getString("RESORT_PIC_NAME"),
                                snap.getString("RESORT_NAME"),
                                snap.getString("OWNER_NAME"),
                                snap.getString("LOCATION"),
                                snap.getDouble("LAT").intValue(),
                                snap.getDouble("LNG").intValue(),
                                snap.getString("TIME"),
                                snap.getString("DATE")
                        ));
                    }
                    resortUserAdapter = new UserResortAdapter(getContext(), resortFireStoreModels);
                    resortFilteredRecycleView.setAdapter(resortUserAdapter);
                }else {
                    resortFilteredRecycleView.setVisibility(View.GONE);
                    no_resort_card.setVisibility(View.VISIBLE);
                }
            }else {
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        profile.setOnClickListener(view -> {
            Intent i = new Intent(getContext(), Profile.class);
            startActivity(i);
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            _reservation_btn.setTooltipText("My Reservation");
        }
        _reservation_btn.setOnClickListener(view1 -> {
            Intent i = new Intent(getContext(), MyReservation.class);
            startActivity(i);
        });

        _rating_btn.setOnClickListener(view1 -> {
            Intent i = new Intent(getContext(), MyRatings.class);
            startActivity(i);
        });

        genderCTV.setOnItemClickListener((adapterView, view, i, l) -> {
            String value = adapterView.getItemAtPosition(i).toString();
            getSearchResort(value);
        });

        return view;
    }

    private void getSearchResort(String value) {
        resortColRef.whereEqualTo("RESORT_NAME", value).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if(!queryDocumentSnapshots.isEmpty()) {
                        resortFireStoreModels.clear();
                        for(QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            resortFireStoreModels.add(new ResortFireStoreModel(
                                    doc.getString("OWNER_UID"),
                                    doc.getString("RESORT_ID"),
                                    doc.getString("RESORT_PIC_URL"),
                                    doc.getString("RESORT_ENTRANCE_FEE"),
                                    doc.getString("RESORT_PIC_NAME"),
                                    doc.getString("RESORT_NAME"),
                                    doc.getString("OWNER_NAME"),
                                    doc.getString("LOCATION"),
                                    doc.getDouble("LAT").intValue(),
                                    doc.getDouble("LNG").intValue(),
                                    doc.getString("TIME"),
                                    doc.getString("DATE")
                            ));
                        }
                        resortUserAdapter = new UserResortAdapter(getContext(), resortFireStoreModels);
                        resortFilteredRecycleView.setAdapter(resortUserAdapter);
                    } else {
                        Toast.makeText(getContext(), "Resort not found.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void populateDropdown(List<String> items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                R.layout.day_time_list_item,
                items
        );

        genderCTV.setAdapter(adapter);
    }

    private void autoImageSlide() {
        final long DELAY_MS = 5000;
        final long PERIOD_MS = 5000;

        final Handler handler = new Handler();
        final Runnable runnable = () -> {
            if(featuredViewPager.getCurrentItem() == featuredPostModels.size() - 1) {
                featuredViewPager.setCurrentItem(0);
            }else {
                featuredViewPager.setCurrentItem(featuredViewPager.getCurrentItem() + 1, true);
            }
        };

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(runnable);
            }
        },DELAY_MS,PERIOD_MS);
    }

    private void _var() {
        resortFilteredRecycleView = view.findViewById(R.id.resortFilteredRecycleView);
        profile = view.findViewById(R.id.profile);
        _no_featured_post_card = view.findViewById(R.id.no_featured_post_card);
        no_resort_card = view.findViewById(R.id.no_resort_card);
        featuredViewPager = view.findViewById(R.id.featuredViewPager);
        tabs = view.findViewById(R.id.tabs);
        _reservation_btn = view.findViewById(R.id.reservation_btn);
        genderCTV = view.findViewById(R.id.genderCTV);
        _rating_btn = view.findViewById(R.id.rating_btn);
    }
}