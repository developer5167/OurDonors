package com.blooddonation;

import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class GetProfile {
    private AccountDetails accountDetails;
    private BloodGroup bg;
    private ProfileLoaded profileLoaded;

    private static AccountDetails MyDetails;
    private static AccountDetails UserDetails;

    public static AccountDetails getMyDetails() {
        return MyDetails;
    }

    public static void setMyDetails(AccountDetails myDetails) {
        MyDetails = myDetails;
    }

    public static AccountDetails getUserDetails() {
        return UserDetails;
    }

    public static void setUserDetails(AccountDetails userDetails) {
        UserDetails = userDetails;
    }

    public GetProfile(String user, ProfileLoaded profileLoaded, CircleImageView profile_pic, ProgressBar progress_horizontal, RecyclerView.ViewHolder holder) {
        DatabaseReference databaseReference22 = FirebaseDatabase.getInstance().getReference().child("MyAc").child(user);
        databaseReference22.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null) {
                    bg = snapshot.getValue(BloodGroup.class);
                    if (bg != null) {
                        DatabaseReference databaseReference22 = FirebaseDatabase.getInstance().getReference().child(Constants.USERS).child(bg.getBg()).child(user);
                        databaseReference22.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    accountDetails = snapshot.getValue(AccountDetails.class);
                                    profileLoaded.OnProfileLoaded(accountDetails,user,profile_pic,progress_horizontal,holder);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
