package com.blooddonation;

import static com.blooddonation.Constants.getUniqueString;
import static com.blooddonation.Constants.send_notification;

import androidx.annotation.NonNull;
;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Notifications_Class extends BaseActivity {

    CircleImageView profile_pic;
    TextView name, age, location, bg;
    private String user_id;
    DatabaseReference databaseReference, databaseReference2;
    AccountDetails accountDetails;
    boolean from_useractivity;
    ArrayList<String> notications_uids = new ArrayList<>();
    FirebaseUser firebaseUser;
    RecyclerView notifications_list_recyclerView;
    CardView card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications__class);
        profile_pic = findViewById(R.id.profile_pic);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.main_color));
        }
        notifications_list_recyclerView = findViewById(R.id.notifications_list_recyclerView);
        notifications_list_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        notifications_list_recyclerView.setHasFixedSize(true);
        name = findViewById(R.id.name);
        card = findViewById(R.id.card);
        user_id = getIntent().getStringExtra("id");
        age = findViewById(R.id.age);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        location = findViewById(R.id.location);
        bg = findViewById(R.id.bg);
        from_useractivity = getIntent().getBooleanExtra("from_useractivity", false);
        if (!from_useractivity) {
            notifications_list_recyclerView.setVisibility(View.GONE);
            card.setVisibility(View.VISIBLE);
            databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.MY_AC).child(user_id);
            databaseReference2 = FirebaseDatabase.getInstance().getReference().child(Constants.MY_AC).child(firebaseUser.getUid());
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    accountDetails = snapshot.getValue(AccountDetails.class);
                    name.setText(accountDetails.getName());
                    age.setText("Age :" + accountDetails.getAge());
                    Picasso.with(getApplicationContext())
                            .load(accountDetails.getImg_url())
                            .into(profile_pic);

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

        } else {
            card.setVisibility(View.GONE);
            notifications_list_recyclerView.setVisibility(View.VISIBLE);
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Notifications").child(firebaseUser.getUid());
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                        String s1 = String.valueOf(snapshot2.getValue());
                        notications_uids.add(s1);
                    }
                    AdapterForNotifications adapterForNotifications = new AdapterForNotifications(Notifications_Class.this, notications_uids);
                    notifications_list_recyclerView.setAdapter(adapterForNotifications);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }

    public void back(View view) {
        onBackPressed();
    }

    public void accept(View view) {


        sendMessage(getString(R.string.readytodonate));
    }

    private void sendMessage(String message) {
        String uniqueKey = getUniqueString(user_id, firebaseUser.getUid());
        send_notification(this, user_id, MyData.getMyData().getName(), message, firebaseUser.getUid(), uniqueKey);
        startActivity(new Intent(this, MessageActivity.class).putExtra("uniqueKey", uniqueKey).putExtra("user", user_id));
        finish();
    }

    public void reject(View view) {
        sendMessage(getString(R.string.notready));
    }
}
