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

import com.blooddonation.Models.AccountDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.text.MessageFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class AdapterForSearched extends RecyclerView.Adapter<AdapterForSearched.MyViewHolder> {
    private Context contex;
    private FirebaseUser firebaseUser;
    private ArrayList<AccountDetails> pojo2s;
    private int selectedProfile = -1;

    AdapterForSearched(Context contex, ArrayList<AccountDetails> pojo2s) {
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
        holder.name.setText(MessageFormat.format("Name :{0}", pojo2s.get(position).getName()));
        holder.age.setText(MessageFormat.format("Age :{0}", pojo2s.get(position).getAge()));
        holder.bg.setText(MessageFormat.format("Blood Group :{0}", pojo2s.get(position).getBg()));
        Picasso.with(contex).load(pojo2s.get(position).getImg_url()).into(holder.profile_pic);
        holder.card.setOnClickListener(view -> {
            holder.progress_horizontal.setVisibility(View.VISIBLE);
            ((User_Activity)contex).setData(
                    pojo2s.get(holder.getAbsoluteAdapterPosition()).getMy_id()
                    ,holder.profile_pic
            ,holder.progress_horizontal);
            holder.progress_horizontal.setIndeterminate(true);
            selectedProfile = holder.getAbsoluteAdapterPosition();
        });



    }

    @Override
    public int getItemCount() {
        return pojo2s.size();
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
            progress_horizontal = itemView.findViewById(R.id.progress_horizontal);
            age = itemView.findViewById(R.id.age);
            bg = itemView.findViewById(R.id.bg);
            location = itemView.findViewById(R.id.location);
            profile_pic = itemView.findViewById(R.id.profile_pic);
        }
    }
}
