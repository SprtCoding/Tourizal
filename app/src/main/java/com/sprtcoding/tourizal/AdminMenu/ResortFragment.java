package com.sprtcoding.tourizal.AdminMenu;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.sprtcoding.tourizal.Adapter.AdminResortAdapter;
import com.sprtcoding.tourizal.Adapter.FireStoreAdapter.ResortAdapterFS;
import com.sprtcoding.tourizal.Model.FSModel.ResortFireStoreModel;
import com.sprtcoding.tourizal.Model.ResortsModel;
import com.sprtcoding.tourizal.R;

import java.util.ArrayList;
import java.util.List;

public class ResortFragment extends Fragment {
    private RecyclerView _admin_resort_rv;
    private CardView _resort_rv_card;
    private LinearLayout _no_post_resort_ll;
    private FirebaseAuth mAuth;
    List<ResortsModel> resortsModels;
    ProgressDialog _loading;
    FirebaseFirestore DB;
    CollectionReference resortCollectionRef;
    ResortAdapterFS resortAdapterFS;
    FirebaseUser _user;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_resort, container, false);

        _var();

        resortsModels = new ArrayList<>();

        _loading = new ProgressDialog(getContext());
        _loading.setTitle("Loading");
        _loading.setMessage("Please wait...");

        mAuth = FirebaseAuth.getInstance();

        //firestore
        DB = FirebaseFirestore.getInstance();
        resortCollectionRef = DB.collection("RESORTS");

        _user = mAuth.getCurrentUser();

        LinearLayoutManager llm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        _admin_resort_rv.setHasFixedSize(true);
        _admin_resort_rv.setLayoutManager(llm);

        if(_user != null) {

            Query userQuery = resortCollectionRef.whereEqualTo("OWNER_UID", _user.getUid()).orderBy("TIME", Query.Direction.DESCENDING);

            FirestoreRecyclerOptions<ResortFireStoreModel> options = new FirestoreRecyclerOptions.Builder<ResortFireStoreModel>()
                    .setQuery(userQuery, ResortFireStoreModel.class)
                    .build();

            resortAdapterFS = new ResortAdapterFS(options);

            resortAdapterFS.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    // Called when items are inserted into the adapter
                    // This indicates that the collection is not empty

                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    super.onItemRangeRemoved(positionStart, itemCount);
                    // Called when items are removed from the adapter
                    // You can check if the adapter is now empty and handle it accordingly
                    if (itemCount == 0) {
                        // Collection is empty
                        // You can handle this case here
                        _resort_rv_card.setVisibility(View.GONE);
                        _no_post_resort_ll.setVisibility(View.VISIBLE);
                    }else {
                        _resort_rv_card.setVisibility(View.VISIBLE);
                        _no_post_resort_ll.setVisibility(View.GONE);
                    }
                }
            });

            _admin_resort_rv.setAdapter(resortAdapterFS);
        }

//        resortPostRef.child(_user.getUid()).child("Resorts").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()) {
//                    resortsModels.clear();
//                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                        ResortsModel resort = dataSnapshot.getValue(ResortsModel.class);
//                        resortsModels.add(resort);
//                    }
//                    adminResortAdapter = new AdminResortAdapter(getContext(), resortsModels);
//                    _admin_resort_rv.setAdapter(adminResortAdapter);
//                }else {
//                    _admin_resort_rv.setVisibility(View.GONE);
//                    _no_post_resort_ll.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });

        return view;
    }

    private void _var() {
        _admin_resort_rv = view.findViewById(R.id.admin_resort_rv);
        _no_post_resort_ll = view.findViewById(R.id.no_post_resort_ll);
        _resort_rv_card = view.findViewById(R.id.resort_rv_card);
    }

    @Override
    public void onStart() {
        super.onStart();
        resortAdapterFS.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        resortAdapterFS.stopListening();
    }
}