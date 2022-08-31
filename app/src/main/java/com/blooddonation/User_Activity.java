package com.blooddonation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class User_Activity extends BaseActivity implements ProfileLoaded {
    private FirebaseUser firebaseUser;
    private final ArrayList<AccountDetails> arrayList = new ArrayList<>();
    private RecyclerView searched_items_recycler, sub_location;
    private AdapterForSearched adapterForSearched;
    private AdapterForSubSearch adapterForSubSearch;
    private ProgressDialog progressDialog;
    private CardView next_lay, searchBar;
    private LinearLayout search_in;
    private AutoCompleteTextView autoCompleteTextView;
    private LatLngModel latLngModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.main_color));
        search_in = findViewById(R.id.search_in);
        searchBar = findViewById(R.id.searchBar);
        sub_location = findViewById(R.id.sub_location);
        next_lay = findViewById(R.id.next_lay);
        searched_items_recycler = findViewById(R.id.searched_items_recycler);
        autoCompleteTextView = findViewById(R.id.bloodgrp);
        searched_items_recycler.setLayoutManager(new LinearLayoutManager(this));
        sub_location.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    @Override

    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);

    }


    @Override
    public void onBackPressed() {
        if (searchBar.getVisibility() == View.GONE) {
            searchBar.setVisibility(View.VISIBLE);
            next_lay.setVisibility(View.VISIBLE);
            arrayList.clear();
            adapterForSearched = new AdapterForSearched(User_Activity.this, arrayList);
            searched_items_recycler.setAdapter(adapterForSearched);
        } else {
            super.onBackPressed();
        }

    }

    public void get_loc2(String locality) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("searching please wait");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String blood_grp = autoCompleteTextView.getText().toString();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.USERS).child(blood_grp);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                    AccountDetails pojo2 = snapshot2.getValue(AccountDetails.class);
                    if (pojo2 != null && pojo2.getLocation().contains(locality)) {
                        if (!pojo2.getMy_id().equals(firebaseUser.getUid())) {
                            arrayList.add(pojo2);
                        }
                    }
                }
                if (arrayList.size() == 0) {
                    Toast.makeText(User_Activity.this, "0 items found", Toast.LENGTH_SHORT).show();
                    searchBar.setVisibility(View.VISIBLE);
                    next_lay.setVisibility(View.VISIBLE);
                    arrayList.clear();

                }
                adapterForSearched = new AdapterForSearched(User_Activity.this, arrayList);
                searched_items_recycler.setAdapter(adapterForSearched);
                search_in.setVisibility(View.VISIBLE);
                adapterForSubSearch = new AdapterForSubSearch(User_Activity.this, getItems());
                sub_location.setAdapter(adapterForSubSearch);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private List<String> getItems() {
        String address = latLngModel.getAddress();
        return Arrays.asList(address.trim().split("\\s*,\\s*"));
    }

    public void setAcc(View view) {
        startActivity(new Intent(this, SetUpAccount.class));
    }

    public void search(View view) {
        latLngModel = LatLngModel.getInstance();
        get_loc2(latLngModel.getPinCode());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void back(View view) {
        onBackPressed();
    }

    public void searchData(String bindingAdapterPosition) {
        get_loc2(bindingAdapterPosition);
    }

    public void setData(String user, CircleImageView profile_pic, ProgressBar progress_horizontal) {
        new GetProfile(user, this, profile_pic, progress_horizontal, null);
        new GetProfile(firebaseUser.getUid(), this, profile_pic, progress_horizontal, null);

    }

    @Override
    public void OnProfileLoaded(AccountDetails accountDetails, String user, CircleImageView profile_pic, ProgressBar progress_horizontal, RecyclerView.ViewHolder holder) {
        if (user.equals(firebaseUser.getUid())) {
            GetProfile.setMyDetails(accountDetails);
        } else {
            GetProfile.setUserDetails(accountDetails);
            Intent intent = new Intent(this, DonorPreview.class);
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(User_Activity.this, profile_pic, "profile");
            if (GetProfile.getMyDetails() != null && GetProfile.getUserDetails() != null) {
                startActivity(intent, options.toBundle());
            } else {
                Toast.makeText(this, "Some thing went wrong please try again", Toast.LENGTH_SHORT).show();
            }
            progress_horizontal.setVisibility(View.INVISIBLE);
        }
    }
}