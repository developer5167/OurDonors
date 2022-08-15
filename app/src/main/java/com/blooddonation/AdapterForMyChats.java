package com.blooddonation;

import static com.blooddonation.Constants.getUniqueString;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterForMyChats extends RecyclerView.Adapter<AdapterForMyChats.MyViewHolder> {
    private Context contex;
    private ArrayList<String> notification_uids;
    private MyAccountDetails myAccountDetails;

    AdapterForMyChats(Context contex, ArrayList<String> notification_uids) {
        this.contex = contex;
        this.notification_uids = notification_uids;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(contex).inflate(R.layout.item_for_mychats, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        myAccountDetails = new MyAccountDetails();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("MyAc").child(notification_uids.get(position));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myAccountDetails = snapshot.getValue(MyAccountDetails.class);
                Glide.with(contex).load(myAccountDetails.getImg_url()).into(holder.profile_pic);
                holder.age.setText(MessageFormat.format("Age :{0}", myAccountDetails.getAge()));
                holder.bg.setText(MessageFormat.format("Blood Grp :{0}", myAccountDetails.getBg()));
                holder.name.setText(myAccountDetails.getName());
                holder.name.setText(myAccountDetails.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        holder.card.setOnClickListener(view -> {
            String uniqueKey = getUniqueString(MyData.getMyData().getChatId(), myAccountDetails.getChatId());
            contex.startActivity(new Intent(contex, MessageActivity.class)
                    .putExtra("user", myAccountDetails.getMy_id()).putExtra("uniqueKey", uniqueKey));
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
            age = itemView.findViewById(R.id.age);
        }
    }
}
