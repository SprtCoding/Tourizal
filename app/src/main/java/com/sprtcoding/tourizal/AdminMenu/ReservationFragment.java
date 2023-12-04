package com.sprtcoding.tourizal.AdminMenu;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sprtcoding.tourizal.Adapter.FireStoreAdapter.AdminReservationAdapter;
import com.sprtcoding.tourizal.Model.FSModel.ReservationModelFS;
import com.sprtcoding.tourizal.R;

import java.util.ArrayList;
import java.util.List;

public class ReservationFragment extends Fragment {
    private RecyclerView reserved_rv;
    private LinearLayout no_post_resort_ll;
    private AdminReservationAdapter adminReservationAdapter;
    private List<ReservationModelFS> reservationModelFSList;
    private FirebaseFirestore db;
    private CollectionReference reservedColRef;
    private FirebaseAuth mAuth;
    private ProgressDialog loading;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_reservation, container, false);

        init();

        reservationModelFSList = new ArrayList<>();

        loading = new ProgressDialog(getContext());
        loading.setMessage("Loading data...");
        loading.show();

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();
        reservedColRef = db.collection("RESERVATION");

        LinearLayoutManager llmRooms = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        llmRooms.setReverseLayout(true);
        llmRooms.setStackFromEnd(true);

        reserved_rv.setHasFixedSize(true);
        reserved_rv.setLayoutManager(llmRooms);

        if(mAuth.getCurrentUser() != null) {
            reservedColRef
                    .whereEqualTo("OWNER_UID", mAuth.getCurrentUser().getUid())
                    .whereEqualTo("REMOVED", false)
                    .addSnapshotListener((value, error) -> {
                        if(error == null && value != null) {
                            if(!value.isEmpty()) {
                                reservationModelFSList.clear();
                                for(QueryDocumentSnapshot doc : value) {
                                    reservationModelFSList.add(new ReservationModelFS(
                                            doc.getString("OWNER_UID"),
                                            doc.getString("MY_UID"),
                                            doc.getString("RESORT_ID"),
                                            doc.getString("AMENITIES_ID"),
                                            doc.getString("RESERVED_ID"),
                                            doc.getString("NAME_OF_USER"),
                                            doc.getString("CONTACT_OF_USER"),
                                            doc.getString("LOCATION_OF_USER"),
                                            doc.getString("DATE_RESERVATION"),
                                            doc.getString("TIME"),
                                            doc.getString("DATE"),
                                            doc.getString("STATUS"),
                                            doc.getString("ROOM_PHOTO_URL"),
                                            doc.getString("DAYTIME"),
                                            doc.getString("USER_PHOTO_URL"),
                                            doc.getString("AMENITIES_TYPE"),
                                            doc.getString("HOURS_STAYED"),
                                            doc.getLong("PRICE").longValue(),
                                            doc.getLong("AMENITIES_NO").intValue(),
                                            doc.getLong("DAYS_STAYED").intValue(),
                                            doc.getLong("GUEST_NO").intValue(),
                                            doc.getBoolean("READ").booleanValue()
                                    ));
                                }
                                loading.dismiss();
                                adminReservationAdapter = new AdminReservationAdapter(getContext(), reservationModelFSList);
                                reserved_rv.setAdapter(adminReservationAdapter);
                            }else {
                                loading.dismiss();
                                no_post_resort_ll.setVisibility(View.VISIBLE);
                                reserved_rv.setVisibility(View.GONE);
                            }
                        } else {
                            loading.dismiss();
                            Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        return v;
    }

    private void init() {
        reserved_rv = v.findViewById(R.id.reserved_rv);
        no_post_resort_ll = v.findViewById(R.id.no_post_resort_ll);
    }
}