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

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.MessageFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterForMyChats extends RecyclerView.Adapter<AdapterForMyChats.MyViewHolder> implements ProfileLoaded {
    private Context contex;
    private ArrayList<String> notification_uids;
    private AccountDetails accountDetails;
    private FirebaseUser firebaseUser;

    AdapterForMyChats(Context contex, ArrayList<String> notification_uids) {
        this.contex = contex;
        this.notification_uids = notification_uids;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(contex).inflate(R.layout.item_for_mychats, parent, false);
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
        System.out.println("cdSLCSDLCL   "+firebaseUser.getUid()+"    "+user);

        if (!user.equals(firebaseUser.getUid())) {
            GetProfile.setUserDetails(accountDetails);
            this.accountDetails = accountDetails;
            Glide.with(contex).load(accountDetails.getImg_url()).into(((MyViewHolder) holder).profile_pic);
            ((MyViewHolder) holder).age.setText(MessageFormat.format("Age :{0}", accountDetails.getAge()));
            ((MyViewHolder) holder).bg.setText(MessageFormat.format("Blood Grp :{0}", accountDetails.getBg()));
            ((MyViewHolder) holder).name.setText(accountDetails.getName());
        }else {
            GetProfile.setMyDetails(accountDetails);
            System.out.println("cdSLCSDLCL   "+firebaseUser.getUid()+"    "+GetProfile.getMyDetails());
        }
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, age, bg, location;
        CircleImageView profile_pic;
        ProgressBar progress_horizontal;
        CardView card;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            card = itemView.findViewById(R.id.card);
            bg = itemView.findViewById(R.id.bg);
            location = itemView.findViewById(R.id.location);
            profile_pic = itemView.findViewById(R.id.profile_pic);
            progress_horizontal = itemView.findViewById(R.id.progress_horizontal);
            age = itemView.findViewById(R.id.age);
        }
    }
}
