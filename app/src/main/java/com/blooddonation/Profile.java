package com.blooddonation;

;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.blooddonation.Models.MyData;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends BaseActivity {
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    CircleImageView profile;
    private TextView name, age, bloodgrp;
    private TextView address_tv;
    MyData myData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.main_color));
        }
        myData = MyData.getMyData();
        profile = findViewById(R.id.profile);
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        bloodgrp = findViewById(R.id.bloodgrp);
        address_tv = findViewById(R.id.address_tv);
        name.setText("Name :" + myData.getName());
        age.setText("Age :" + myData.getAge());
        bloodgrp.setText("Blood Group :" + myData.getBg());
            Picasso.with(getApplicationContext())
                    .load(myData.getImg_url())
                    .into(profile);
    }

    public void edit_detils(View view) {
        startActivity(new Intent(this, SetUpAccount.class).putExtra("fromProfile", true));
    }

    public void back(View view) {
        onBackPressed();
    }
}