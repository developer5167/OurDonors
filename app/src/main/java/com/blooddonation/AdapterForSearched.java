package com.blooddonation;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;



public class AdapterForSearched extends RecyclerView.Adapter<AdapterForSearched.MyViewHolder> {
    private Context contex;
    private FirebaseUser firebaseUser;
    private ArrayList<MyAccountDetails> pojo2s;

    AdapterForSearched(Context contex, ArrayList<MyAccountDetails> pojo2s) {
        this.contex = contex;
        this.pojo2s = pojo2s;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(contex).inflate(R.layout.item_for_searched, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.name.setText("Name :" + pojo2s.get(position).getName());
        holder.age.setText("Age :" + pojo2s.get(position).getAge());
        holder.bg.setText("Blood Group :" + pojo2s.get(position).getBg());
        Picasso.with(contex)
                .load(pojo2s.get(position).getImg_url())
                .into(holder.profile_pic);
        holder.card.setOnClickListener(view -> contex.startActivity(new Intent(contex, DonorPreview.class)
                .putExtra("user", pojo2s.get(holder.getAbsoluteAdapterPosition()).getMy_id())));
    }

    @Override
    public int getItemCount() {
        return pojo2s.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, age, bg, location;
        CircleImageView profile_pic;
        CardView card;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            card = itemView.findViewById(R.id.card);
            age = itemView.findViewById(R.id.age);
            bg = itemView.findViewById(R.id.bg);
            location = itemView.findViewById(R.id.location);
            profile_pic = itemView.findViewById(R.id.profile_pic);
        }
    }
}
