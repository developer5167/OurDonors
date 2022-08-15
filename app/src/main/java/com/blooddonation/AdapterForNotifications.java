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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class AdapterForNotifications extends RecyclerView.Adapter<AdapterForNotifications.MyViewHolder> {
    private Context contex;
    private ArrayList<String> notification_uids;
    MyData myData;
    DatabaseReference databaseReference, databaseReference2;
    FirebaseUser firebaseUser;
    MyAccountDetails myAccountDetails;
    public AdapterForNotifications(Context contex, ArrayList<String> notification_uids) {
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
        databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.MY_AC).child(notification_uids.get(position));
        databaseReference2 = FirebaseDatabase.getInstance().getReference().child(Constants.MY_AC).child(firebaseUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 myAccountDetails = snapshot.getValue(MyAccountDetails.class);
                holder.name.setText(myAccountDetails.getName());
                Picasso.with(contex).load(myAccountDetails.getImg_url()).into(holder.profile_pic);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MyData myData = snapshot.getValue(MyData.class);
                MyData.setMyData(myData);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        holder.card.setOnClickListener(view -> {
            String uniqueKey = getUniqueString(MyData.getMyData().getChatId(), myAccountDetails.getChatId());

            contex.startActivity(new Intent(contex, MessageActivity.class)
                    .putExtra("uniqueKey", uniqueKey)
                    .putExtra("user", notification_uids.get(holder.getAbsoluteAdapterPosition())));
        });
    }

    @Override
    public int getItemCount() {
        return notification_uids.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, bg, location;
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
