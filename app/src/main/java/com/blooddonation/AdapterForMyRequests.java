package com.blooddonation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.text.MessageFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class AdapterForMyRequests extends RecyclerView.Adapter<AdapterForMyRequests.MyViewHolder> implements ProfileLoaded {
    private Context contex;
    private ArrayList<String> notification_uids;
    private AccountDetails accountDetails;
    private FirebaseUser firebaseUser;

    AdapterForMyRequests(Context contex, ArrayList<String> notification_uids) {
        this.contex = contex;
        this.notification_uids = notification_uids;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(contex).inflate(R.layout.notifications_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        new GetProfile(notification_uids.get(position), this, holder.profile_pic, holder.progress_horizontal, holder);
//        new GetProfile(firebaseUser.getUid(), this, holder.profile_pic, holder.progress_horizontal, holder);
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("MyAc").child(notification_uids.get(position));
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return notification_uids.size();
    }

    @Override
    public void OnProfileLoaded(AccountDetails accountDetails, String user, CircleImageView profile_pic, ProgressBar progress_horizontal, RecyclerView.ViewHolder holder) {
        if (!user.equals(firebaseUser.getUid())){
            ((MyViewHolder)holder).name.setText(accountDetails.getName());
            ((MyViewHolder)holder).name.setText(accountDetails.getName());
            ((MyViewHolder)holder).bg.setText(MessageFormat.format("You Requested {0} for Blood", accountDetails.getName()));
            Picasso.with(contex).load(accountDetails.getImg_url()).into(((MyViewHolder)holder).profile_pic);
        }
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, age, bg, location;
        CircleImageView profile_pic;
        CardView card;
        ProgressBar progress_horizontal;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            card = itemView.findViewById(R.id.card);
            bg = itemView.findViewById(R.id.bg);
            location = itemView.findViewById(R.id.location);
            profile_pic = itemView.findViewById(R.id.profile_pic);
            progress_horizontal = itemView.findViewById(R.id.progress_horizontal);
        }
    }
}
