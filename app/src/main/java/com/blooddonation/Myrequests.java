package com.blooddonation;

import androidx.annotation.NonNull;
;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Myrequests extends BaseActivity {
    RecyclerView myrequest_recycler;
    FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    ArrayList<String> notications_uids = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myrequests);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.main_color));
        }
        myrequest_recycler = findViewById(R.id.myrequest_recycler);
        myrequest_recycler.setLayoutManager(new LinearLayoutManager(this));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("My_requests").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                    String s1 = String.valueOf(snapshot2.getValue());
                    notications_uids.add(s1);
                }
                AdapterForMyRequests adapterForNotifications = new AdapterForMyRequests(Myrequests.this, notications_uids);
                myrequest_recycler.setAdapter(adapterForNotifications);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void back(View view) {
        onBackPressed();
    }
}
