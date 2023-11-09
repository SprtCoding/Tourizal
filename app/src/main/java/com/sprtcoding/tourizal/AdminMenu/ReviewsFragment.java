package com.sprtcoding.tourizal.AdminMenu;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sprtcoding.tourizal.Adapter.AdminResortReviewsAdapter;
import com.sprtcoding.tourizal.Adapter.ReviewsCommentRateAdapter;
import com.sprtcoding.tourizal.Model.FSModel.ReviewsModelFS;
import com.sprtcoding.tourizal.Model.ResortsModel;
import com.sprtcoding.tourizal.R;

import java.util.ArrayList;
import java.util.List;

public class ReviewsFragment extends Fragment {
    View view;
    private RecyclerView rate_rv;
    private LinearLayout no_reviews_ll;
    private FirebaseFirestore db;
    private CollectionReference reviewsColRef, ratingCol;
    private FirebaseAuth mAuth;
    private ProgressDialog loading;
    private ReviewsCommentRateAdapter reviewsCommentRateAdapter;
    private List<ReviewsModelFS> reviewsModelFS;
    private String resort_id = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reviews, container, false);

        loading = new ProgressDialog(getContext());
        loading.setMessage("Loading data...");
        loading.show();

        //fs
        db = FirebaseFirestore.getInstance();
        reviewsColRef = db.collection("RESORTS");
        ratingCol = db.collection("RATINGS");

        reviewsModelFS = new ArrayList<>();

        rate_rv = view.findViewById(R.id.rate_rv);
        no_reviews_ll = view.findViewById(R.id.no_reviews_ll);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser _user = mAuth.getCurrentUser();

        LinearLayoutManager llm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rate_rv.setHasFixedSize(true);
        rate_rv.setLayoutManager(llm);

        if(_user != null) {
            reviewsColRef.whereEqualTo("OWNER_UID", _user.getUid())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if(!queryDocumentSnapshots.isEmpty()) {
                            for(QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                resort_id = doc.getString("RESORT_ID");

                                ratingCol.whereEqualTo("OWNER_ID", _user.getUid())
                                        .whereEqualTo("RESORT_ID", resort_id)
                                        .addSnapshotListener((value, error) -> {
                                            if(error == null && value != null) {
                                                if(!value.isEmpty()) {
                                                    reviewsModelFS.clear();
                                                    for(QueryDocumentSnapshot doc1: value) {
                                                        reviewsModelFS.add(new ReviewsModelFS(
                                                                doc1.getString("RESORT_ID"),
                                                                doc1.getString("USER_ID"),
                                                                doc1.getString("OWNER_ID"),
                                                                doc1.getString("DATE_POSTED"),
                                                                doc1.getString("TIME_POSTED"),
                                                                doc1.getString("NAME_OF_USER"),
                                                                doc1.getString("COMMENTS"),
                                                                doc1.getDouble("RATINGS")
                                                        ));
                                                    }
                                                    reviewsCommentRateAdapter = new ReviewsCommentRateAdapter(reviewsModelFS);
                                                    rate_rv.setAdapter(reviewsCommentRateAdapter);
                                                    loading.dismiss();
                                                }else {
                                                    no_reviews_ll.setVisibility(View.VISIBLE);
                                                    rate_rv.setVisibility(View.GONE);
                                                    loading.dismiss();
                                                }
                                            }else {
                                                Toast.makeText(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                                                loading.dismiss();
                                            }
                                        });

//                                reviewsColRef.document(resort_id).collection("RATINGS")
//                                        .whereEqualTo("OWNER_ID", _user.getUid())
//                                        .addSnapshotListener((value, error) -> {
//                                            if(error == null && value != null) {
//                                                if(!value.isEmpty()) {
//                                                    reviewsModelFS.clear();
//                                                    for(QueryDocumentSnapshot doc1: value) {
//                                                        reviewsModelFS.add(new ReviewsModelFS(
//                                                                doc1.getString("RESORT_ID"),
//                                                                doc1.getString("USER_ID"),
//                                                                doc1.getString("OWNER_ID"),
//                                                                doc1.getString("DATE_POSTED"),
//                                                                doc1.getString("TIME_POSTED"),
//                                                                doc1.getString("NAME_OF_USER"),
//                                                                doc1.getString("COMMENTS"),
//                                                                doc1.getDouble("RATINGS")
//                                                        ));
//                                                    }
//                                                    reviewsCommentRateAdapter = new ReviewsCommentRateAdapter(reviewsModelFS);
//                                                    rate_rv.setAdapter(reviewsCommentRateAdapter);
//                                                    loading.dismiss();
//                                                }else {
//                                                    no_reviews_ll.setVisibility(View.VISIBLE);
//                                                    rate_rv.setVisibility(View.GONE);
//                                                    loading.dismiss();
//                                                }
//                                            }else {
//                                                Toast.makeText(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
//                                                loading.dismiss();
//                                            }
//                                        });
                            }
                        }
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                    });


        }

        return view;
    }
}