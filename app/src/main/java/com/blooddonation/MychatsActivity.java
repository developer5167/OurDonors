package com.blooddonation;

import androidx.annotation.NonNull;
;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MychatsActivity extends BaseActivity {
    RecyclerView recyclerView;
    FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    ArrayList<String> mychat_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mychats);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.main_color));
        }
        initialise();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                    String s1 = String.valueOf(snapshot2.getValue());
                    mychat_list.add(s1);
                }
                AdapterForMyChats adapterForNotifications = new AdapterForMyChats(MychatsActivity.this, mychat_list);
                recyclerView.setAdapter(adapterForNotifications);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initialise() {
        recyclerView = findViewById(R.id.mychats_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("My_requests").child(firebaseUser.getUid());

    }

    public void back(View view) {
        onBackPressed();
    }
}
