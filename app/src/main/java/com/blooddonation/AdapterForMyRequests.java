package com.blooddonation;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class AdapterForMyRequests extends RecyclerView.Adapter<AdapterForMyRequests.MyViewHolder> {
    private Context contex;
    private ArrayList<String> notification_uids;
    private MyAccountDetails myAccountDetails;

    AdapterForMyRequests(Context contex, ArrayList<String> notification_uids) {
        this.contex = contex;
        this.notification_uids = notification_uids;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(contex).inflate(R.layout.notifications_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        myAccountDetails = new MyAccountDetails();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("MyAc").child(notification_uids.get(position));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myAccountDetails = new MyAccountDetails();
                HashMap<String, String> hashMap = (HashMap<String, String>) snapshot.getValue();
                myAccountDetails.setName(hashMap.get("name"));
                myAccountDetails.setAge(hashMap.get("age"));
                myAccountDetails.setLocation(hashMap.get("location"));
                myAccountDetails.setBg(hashMap.get("bg"));
                myAccountDetails.setImg_url(hashMap.get("img_url"));
                Log.e("PPPPPPP    ", myAccountDetails.getAge() + "  " + myAccountDetails.getName());
                holder.name.setText(myAccountDetails.getName());
                holder.bg.setText("You Requested " + myAccountDetails.getName() + " for Blood");
                myAccountDetails.setMy_id(hashMap.get("my_id"));
                Picasso.with(contex)
                        .load(myAccountDetails.getImg_url())
                        .into(holder.profile_pic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    @Override
    public int getItemCount() {
        return notification_uids.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, age, bg, location;
        CircleImageView profile_pic;
        CardView card;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            card = itemView.findViewById(R.id.card);
            bg = itemView.findViewById(R.id.bg);
            location = itemView.findViewById(R.id.location);
            profile_pic = itemView.findViewById(R.id.profile_pic);
        }
    }
}
