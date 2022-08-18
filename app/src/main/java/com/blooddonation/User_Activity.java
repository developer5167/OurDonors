package com.blooddonation;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class User_Activity extends AppCompatActivity {
    private CardView accd_lay;
    private FirebaseUser firebaseUser;
    private Dialog dialog;
    boolean local = false;
    private DatabaseReference databaseReference;
    private ArrayList<MyAccountDetails> arrayList = new ArrayList<>();
    private RecyclerView searched_items_recycler, sub_location;
    private AdapterForSearched adapterForSearched;
    private AdapterForSubSearch adapterForSubSearch;
    private ProgressDialog progressDialog;
    private CardView next_lay, searchBar;
    private String locality;
    private String locality2;
    private LinearLayout search_in;
    private RelativeLayout loading_lay;
    private AutoCompleteTextView autoCompleteTextView;
    private LatLngModel latLngModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.main_color));
        }
        accd_lay = findViewById(R.id.accd_lay);
        loading_lay = findViewById(R.id.loading_lay);
        search_in = findViewById(R.id.search_in);
        searchBar = findViewById(R.id.searchBar);
        sub_location = findViewById(R.id.sub_location);
        next_lay = findViewById(R.id.next_lay);
        searched_items_recycler = findViewById(R.id.searched_items_recycler);
        autoCompleteTextView = findViewById(R.id.bloodgrp);

        searched_items_recycler.setLayoutManager(new LinearLayoutManager(this));
        sub_location.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        dialog = new Dialog(User_Activity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.setupac);
        dialog.setCancelable(false);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("accounts").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    accd_lay.setVisibility(View.GONE);
                    loading_lay.setVisibility(View.GONE);
                } else {
                    accd_lay.setVisibility(View.VISIBLE);
                    loading_lay.setVisibility(View.GONE);
                    dialog.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dialog.findViewById(R.id.setbtn).setOnClickListener(view -> startActivity(new Intent(User_Activity.this, SetUpAccount.class)));
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
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(locality).child(blood_grp);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                    MyAccountDetails pojo2 = snapshot2.getValue(MyAccountDetails.class);
                    if (pojo2 != null && !pojo2.getMy_id().equals(firebaseUser.getUid())) {
                        arrayList.add(pojo2);
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
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("accounts").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    accd_lay.setVisibility(View.GONE);
                    loading_lay.setVisibility(View.GONE);
                } else {
                    accd_lay.setVisibility(View.VISIBLE);
                    loading_lay.setVisibility(View.GONE);
                    dialog.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void back(View view) {
        onBackPressed();
    }

    public void searchData(String bindingAdapterPosition) {
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("users");

        Query applesQuery = reference2.orderByChild("location").equalTo(bindingAdapterPosition);
        reference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println("cASCKASC   "+snapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}