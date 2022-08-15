package com.blooddonation;

import static com.blooddonation.Constants.getUniqueString;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.blooddonation.Notifications.Client;
import com.blooddonation.Notifications.Data;
import com.blooddonation.Notifications.MyResponse;
import com.blooddonation.Notifications.Sender;
import com.blooddonation.Notifications.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DonorPreview extends AppCompatActivity {
    CircleImageView profile_dono;
    DatabaseReference databaseReference, databaseReference2;
    TextView name, requestblood_txt;
    FirebaseUser firebaseUser;
    MyAccountDetails myAccountDetails, myAccountDetails1;
    APIService apiService;
    ImageView requestblood_img;

    String user, chatId;
    static Dialog dialog;
    AudioManager manager;
    Ringtone r;
    private String uniqueKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_preview);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.main_color));
        }
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        user = getIntent().getStringExtra(getString(R.string.USER));
        databaseReference = FirebaseDatabase.getInstance().getReference().child("MyAc").child(user);
        databaseReference2 = FirebaseDatabase.getInstance().getReference().child("MyAc").child(firebaseUser.getUid());
        profile_dono = findViewById(R.id.profile_dono);

        requestblood_img = findViewById(R.id.requestblood_img);
        requestblood_txt = findViewById(R.id.requestblood_txt);
        name = findViewById(R.id.name_donorP);
        manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myAccountDetails = snapshot.getValue(MyAccountDetails.class);
                Picasso.with(getApplicationContext())
                        .load(myAccountDetails.getImg_url())
                        .into(profile_dono);
                name.setText(myAccountDetails.getName());
                databaseReference2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        myAccountDetails1 = snapshot.getValue(MyAccountDetails.class);
                        uniqueKey = getUniqueString(myAccountDetails1.getChatId(), myAccountDetails.getChatId());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void request_blood(View view) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference().child(Constants.TOKENS);
        Query query = tokens.orderByKey().equalTo(myAccountDetails.getMy_id());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Token token = dataSnapshot1.getValue(Token.class);
                    Data data2 = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, myAccountDetails1.getName() + " requested for blood", "Blood required", myAccountDetails.getMy_id(), "true",uniqueKey);
                    Sender sender = null;
                    if (token != null) {
                        sender = new Sender(data2, token.getToken());
                        apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {
                                if (response.code() == 200) {
                                    if (response.body() != null && response.body().success == 1) {
                                        requestblood_img.setImageResource(R.drawable.requestsent);
                                        requestblood_txt.setText(R.string.requestSent);
                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Notifications").child(myAccountDetails.getMy_id()).child(firebaseUser.getUid());
                                        databaseReference.setValue(firebaseUser.getUid());
                                        Toast.makeText(DonorPreview.this, "Request sent", Toast.LENGTH_SHORT).show();
                                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("My_requests").child(firebaseUser.getUid()).child(myAccountDetails.getMy_id());
                                        databaseReference1.setValue(myAccountDetails.getMy_id());
                                        startActivity(new Intent(DonorPreview.this, MessageActivity.class)
                                                .putExtra("uniqueKey", uniqueKey).putExtra("user", user));
                                        finish();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<MyResponse> call, @NonNull Throwable t) {
                                Log.e("UUUUUU    ", t.getMessage() + " LLLLLLL");

                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DonorPreview.this, "" + databaseError, Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void send_message(View view) {
        startActivity(new Intent(this, MessageActivity.class).putExtra(Constants.USER_ID, user));
    }

    public void call__(View view) {

    }

    private void openCallerDialog() {
        dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.calling_view);
        dialog.setCancelable(false);
        RelativeLayout reject;
        TextView name_of_recipient;
        CircleImageView profile_pic_recipient;
        reject = dialog.findViewById(R.id.hangup);
        name_of_recipient = dialog.findViewById(R.id.name_of_recipient);
        profile_pic_recipient = dialog.findViewById(R.id.profile_pic_recipient);
        name_of_recipient.setText("Calling " + myAccountDetails.getName() + "...");
        Picasso.with(getApplicationContext())
                .load(myAccountDetails.getImg_url())
                .into(profile_pic_recipient);
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        dialog.show();
    }
}