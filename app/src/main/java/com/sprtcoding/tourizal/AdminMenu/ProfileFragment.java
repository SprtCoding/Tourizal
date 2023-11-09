package com.sprtcoding.tourizal.AdminMenu;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sprtcoding.tourizal.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private CircleImageView userPic;
    private TextView name, email, bdate, age, gender;
    private MaterialButton edit_btn;
    DatabaseReference userRef;
    FirebaseDatabase db;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_profile, container, false);
        _init();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

        mUser = mAuth.getCurrentUser();
        userRef = db.getReference("Users");

        if(mUser != null) {
            userRef.child(mUser.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                String _name = snapshot.child("Fullname").getValue(String.class);
                                String _email = snapshot.child("Email").getValue(String.class);
                                String _bdate = snapshot.child("DateOfBirth").getValue(String.class);
                                String _age = snapshot.child("Age").getValue(String.class);
                                String _gender = snapshot.child("Gender").getValue(String.class);
                                String _picUrl = snapshot.child("PhotoURL").getValue(String.class);

                                Picasso.get().load(_picUrl).placeholder(R.drawable.default_profile).fit().into(userPic);
                                name.setText(_name);
                                email.setText(_email);
                                bdate.setText(_bdate);
                                age.setText(_age);
                                gender.setText(_gender);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }


        return v;
    }

    private void _init() {
        userPic = v.findViewById(R.id.user_pic);
        name = v.findViewById(R.id.name);
        email = v.findViewById(R.id.email);
        bdate = v.findViewById(R.id.bdate);
        age = v.findViewById(R.id.age);
        gender = v.findViewById(R.id.gender);
        edit_btn = v.findViewById(R.id.edit_btn);
    }
}