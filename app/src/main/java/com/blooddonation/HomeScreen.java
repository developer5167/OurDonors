package com.blooddonation;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeScreen extends AppCompatActivity {
    private FirebaseUser firebaseUser;
    private MyAccountDetails myAccountDetails;
    private CircleImageView profile;
    private Dialog dialog;
    private boolean your_date_is_outdated;
    private SharedPrefManager sharedPrefManager;

    LatLngModel latLngModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        profile = findViewById(R.id.profile);
        latLngModel = LatLngModel.getInstance();
        MaterialTextView address_text = findViewById(R.id.address_text);
        address_text.setText(latLngModel.getAddress());
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.setupac);
        dialog.setCancelable(false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        sharedPrefManager = SharedPrefManager.getInstance();
        if (sharedPrefManager.getProfileData(HomeScreen.this) != null) {
            MyData.setMyData(sharedPrefManager.getProfileData(HomeScreen.this));
            Picasso.with(getApplicationContext()).load(MyData.getMyData().getImg_url()).into(profile);
        } else {
            setProfilePic();
        }
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("accounts").child(firebaseUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    dialog.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dialog.findViewById(R.id.setbtn).setOnClickListener(view -> startActivity(new Intent(HomeScreen.this, SetUpAccount.class)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.main_color));
        }

        getLocation();
    }

    public String getLocation() {
        GpsTracker gpsTracker = new GpsTracker(this);
        if (!gpsTracker.canGetLocation()) {
            gpsTracker.showSettingsAlert();
        }
        return "";
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dialog.isShowing())
            dialog.dismiss();
    }

    public void myprofile(View view) {
        Intent intent = new Intent(this, Profile.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(HomeScreen.this, profile, "profile");
        startActivity(intent, options.toBundle());
    }

    public void myrequests(View view) {
        startActivity(new Intent(HomeScreen.this, Myrequests.class));
    }

    public void Mynotifications(View view) {
        startActivity(new Intent(HomeScreen.this, Notifications_Class.class).putExtra("from_useractivity", true));
    }

    public void my_chats(View view) {
        startActivity(new Intent(this, MychatsActivity.class));

    }

    public void options(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.mymenu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.logout) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomeScreen.this, MainActivity.class));
            }
            return true;
        });
        popup.show();
    }

    public void search_blood(View view) {
        startActivity(new Intent(this, User_Activity.class));
    }

    void setProfilePic() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference22 = FirebaseDatabase.getInstance().getReference().child("MyAc").child(firebaseUser.getUid());
        databaseReference22.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null) {
                    BloodGroup bg = snapshot.getValue(BloodGroup.class);
                    if (bg != null) {
                        getProfileData(bg.getBg());
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void getProfileData(String bg) {
        DatabaseReference databaseReference22 = FirebaseDatabase.getInstance().getReference().child(Constants.USERS).child(bg).child(firebaseUser.getUid());
        databaseReference22.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    MyData.setMyData(snapshot.getValue(MyData.class));
                    SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance();
                    sharedPrefManager.setProfileData(HomeScreen.this, MyData.getMyData());
                    Picasso.with(getApplicationContext()).load(MyData.getMyData().getImg_url()).into(profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    public void post(View view) {
        CardView my_posts, other_posts;
        Dialog dialog = new Dialog(this, R.style.Theme_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.post_dialog);
        my_posts = dialog.findViewById(R.id.my_posts);
        other_posts = dialog.findViewById(R.id.other_posts);
        my_posts.setOnClickListener(view12 -> {
            dialog.dismiss();
            startActivity(new Intent(HomeScreen.this, PostsActivity.class));
        });
        other_posts.setOnClickListener(view1 -> startActivity(new Intent(HomeScreen.this, OtherPostsActivity.class)));
        dialog.show();
    }

    String key;

    public static ArrayList<String> myStatusus;
    public static ArrayList<String> uploaded_dates;

    public static ArrayList<String> otherStatus;
    public static ArrayList<String> otherUploadedDates;

    @Override
    protected void onResume() {
        super.onResume();
        uploaded_dates = new ArrayList<>();
        otherStatus = new ArrayList<>();
        otherUploadedDates = new ArrayList<>();
        myStatusus = new ArrayList<>();
        latLngModel = LatLngModel.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("MY_STATUS");
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("all_posts");
        Query applesQuery = reference.child(firebaseUser.getUid()).orderByChild("owner").equalTo(firebaseUser.getUid());
        Query all_posts = reference2.child(latLngModel.getPinCode()).child(firebaseUser.getUid()).orderByChild("owner").equalTo(firebaseUser.getUid());
        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                        key = appleSnapshot.getKey();
                        if (isOutDated(key)) {
                            FirebaseDatabase.getInstance().getReference().child("MY_STATUS").child(firebaseUser.getUid()).child(key).removeValue();
                        } else {
                            FirebaseDatabase.getInstance().getReference().child("MY_STATUS").child(firebaseUser.getUid())
                                    .child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Story story = snapshot.getValue(Story.class);
                                            uploaded_dates.add(story.getCurrent());
                                            myStatusus.add(story.getPostUrl());
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        all_posts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                        key = appleSnapshot.getKey();
                        if (isOutDated(key)) {
                            FirebaseDatabase.getInstance().getReference().child("all_posts").child(latLngModel.getPinCode()).child(firebaseUser.getUid())
                                    .child(key).removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        get_allPosts();
    }

    private void get_allPosts() {
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("all_posts");
        reference2.child(latLngModel.getPinCode()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s : snapshot.getChildren()) {
                    Story story = s.getValue(Story.class);
                    String owner = story.getOwner();
                    if (!owner.equals(firebaseUser.getUid())) {
                        otherUploadedDates.add(story.getCurrent());
                        otherStatus.add(story.getPostUrl());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean isOutDated(String key) {

        Long date_from_server = Long.valueOf(key);
        Long latest_date = System.currentTimeMillis();
        if (date_from_server.compareTo(latest_date) > 0) {
            your_date_is_outdated = false;
        } else if (date_from_server.compareTo(latest_date) < 0) {
            your_date_is_outdated = true;
        }
        return your_date_is_outdated;
    }
}
