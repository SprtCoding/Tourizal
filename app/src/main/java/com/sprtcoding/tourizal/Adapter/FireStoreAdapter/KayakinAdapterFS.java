package com.sprtcoding.tourizal.Adapter.FireStoreAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sprtcoding.tourizal.AdminPost.ResortPost.AddKayakPage;
import com.sprtcoding.tourizal.AdminPost.ResortPost.AddPoolPage;
import com.sprtcoding.tourizal.Model.FSModel.KayakinModel;
import com.sprtcoding.tourizal.R;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class KayakinAdapterFS extends FirestoreRecyclerAdapter<KayakinModel, KayakinAdapterFS.ViewHolder> {
    Context context;
    AlertDialog.Builder closeAlertBuilder;
    FirebaseFirestore DB;
    CollectionReference resortCollectionRef;
    public KayakinAdapterFS(@NonNull FirestoreRecyclerOptions<KayakinModel> options) {
        super(options);
    }
    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull KayakinModel model) {
        ProgressDialog _loading = new ProgressDialog(context);
        _loading.setTitle("Delete");
        _loading.setMessage("Please wait...");

        closeAlertBuilder = new AlertDialog.Builder(context);

        DB = FirebaseFirestore.getInstance();
        resortCollectionRef = DB.collection("RESORTS");
        StorageReference kayakinPhotoRef = FirebaseStorage.getInstance().getReference("KayakinPhotos/");

        Picasso.get().load(model.getKAYAKIN_IMG_URL()).into(holder.kayakinPhoto);
        holder.kayakinNo.setText("Kayakin "+model.getKAYAKIN_NO());
        holder.kayakinPrice.setText(convertToPhilippinePeso(model.getRENT_PRICE()));

        holder.deleteBtn.setOnClickListener(view -> {
            closeAlertBuilder.setTitle("Delete")
                    .setMessage("Are you sure want to delete kayakin " + model.getKAYAKIN_NO() + "?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        _loading.show();
                        Handler handler = new Handler();
                        Runnable runnable = () -> {
                            _loading.dismiss();
                            kayakinPhotoRef.child(model.getKAYAKIN_PIC_ID()).child(model.getKAYAKIN_PIC_NAME()+".jpg").delete();
                            resortCollectionRef.document(model.getRESORT_UID())
                                    .collection("POOL")
                                    .document(model.getKAYAKIN_UID())
                                    .delete().addOnSuccessListener(unused -> {
                                        Toast.makeText(context, "Kayakin " + model.getKAYAKIN_NO() + " removed successfully.", Toast.LENGTH_SHORT).show();
                                        _loading.dismiss();
                                    });
                        };
                        handler.postDelayed(runnable, 3000);
                    })
                    .setNegativeButton("No", (dialogInterface, i) -> {
                        dialogInterface.cancel();
                    })
                    .show();
        });

        holder.editBtn.setOnClickListener(view -> {
            Intent i = new Intent(context, AddKayakPage.class);
            i.putExtra("isUpdate", true);
            i.putExtra("resortID", model.getRESORT_UID());
            i.putExtra("kayakID", model.getKAYAKIN_UID());
            i.putExtra("kayakPicUrl", model.getKAYAKIN_IMG_URL());
            i.putExtra("kayakPicID", model.getKAYAKIN_PIC_ID());
            i.putExtra("kayakPicName", model.getKAYAKIN_PIC_NAME());
            i.putExtra("kayakNo", model.getKAYAKIN_NO());
            i.putExtra("kayakDesc", model.getKAYAKIN_DESC());
            i.putExtra("kayakPrice", model.getRENT_PRICE());
            context.startActivity(i);
            // Finish the current activity
            ((Activity) context).finish();
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_kayakin_list,parent,false);
        return new KayakinAdapterFS.ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView kayakinPhoto;
        TextView kayakinNo, kayakinPrice, editBtn, deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            kayakinPhoto = itemView.findViewById(R.id.kayakinPhoto);
            kayakinNo = itemView.findViewById(R.id.kayakinNo);
            kayakinPrice = itemView.findViewById(R.id.kayakinPrice);
            editBtn = itemView.findViewById(R.id.editBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }

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
