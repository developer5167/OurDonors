package com.blooddonation;

import static com.blooddonation.Constants.getUniqueString;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class AdapterForNotifications extends RecyclerView.Adapter<AdapterForNotifications.MyViewHolder> implements ProfileLoaded {
    private Context contex;
    private ArrayList<String> notification_uids;
    MyData myData;
    DatabaseReference databaseReference, databaseReference2;
    FirebaseUser firebaseUser;
    AccountDetails accountDetails;

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

        new GetProfile(notification_uids.get(position), this, holder.profile_pic, holder.progress_horizontal, holder);
        new GetProfile(firebaseUser.getUid(), this, holder.profile_pic, holder.progress_horizontal, holder);
        holder.card.setOnClickListener(view -> {
            String uniqueKey = getUniqueString(MyData.getMyData().getChatId(), accountDetails.getChatId());
            Intent intent = new Intent(contex, MessageActivity.class);
            intent.putExtra("uniqueKey", uniqueKey)
                    .putExtra("user", notification_uids.get(holder.getAbsoluteAdapterPosition()));
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation((Activity) contex, holder.profile_pic, "profile");
            contex.startActivity(intent, options.toBundle());
        });
    }

    @Override
    public int getItemCount() {
        return notification_uids.size();
    }

    @Override
    public void OnProfileLoaded(AccountDetails accountDetails, String user, CircleImageView profile_pic, ProgressBar progress_horizontal, RecyclerView.ViewHolder holder) {
        if (!user.equals(firebaseUser.getUid())) {
            this.accountDetails = accountDetails;
            GetProfile.setUserDetails(accountDetails);
            ((MyViewHolder)holder).name.setText(accountDetails.getName());
            ((MyViewHolder)holder).bg.setText(accountDetails.getBg());
            Picasso.with(contex).load(accountDetails.getImg_url()).into(((MyViewHolder)holder).profile_pic);
        } else {
            GetProfile.setMyDetails(accountDetails);
        }

    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, bg, location;
        CircleImageView profile_pic;
        ProgressBar progress_horizontal;
        CardView card;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            card = itemView.findViewById(R.id.card);
            bg = itemView.findViewById(R.id.bg);
            location = itemView.findViewById(R.id.location);
            progress_horizontal = itemView.findViewById(R.id.progress_horizontal);
            profile_pic = itemView.findViewById(R.id.profile_pic);
        }
    }
}
