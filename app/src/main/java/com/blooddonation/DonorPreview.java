package com.blooddonation;

import static com.blooddonation.Constants.getUniqueString;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.blooddonation.Models.AccountDetails;
import com.blooddonation.Models.GetProfile;
import com.blooddonation.Notifications.Client;
import com.blooddonation.Models.Data;
import com.blooddonation.Models.MyResponse;
import com.blooddonation.Models.Sender;
import com.blooddonation.Models.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DonorPreview extends BaseActivity  {
    CircleImageView profile_dono;
    DatabaseReference databaseReference, databaseReference2;
    TextView name, requestblood_txt;
    FirebaseUser firebaseUser;
    AccountDetails selectedUserDetails, currentUserDetails;
    ApiClient apiService;

    String user, chatId;
    static Dialog dialog;
    AudioManager manager;
    Ringtone r;
    private String uniqueKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_preview);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.main_color));
        apiService = Client.getClient("https://fcm.googleapis.com/").create(ApiClient.class);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        user = getIntent().getStringExtra(getString(R.string.USER));
        profile_dono = findViewById(R.id.profile_dono);
        requestblood_txt = findViewById(R.id.requestblood_txt);
        name = findViewById(R.id.name_donorP);
        manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        selectedUserDetails= GetProfile.getUserDetails();
        currentUserDetails=GetProfile.getMyDetails();
        Picasso.with(getApplicationContext()).load(selectedUserDetails.getImg_url()).into(profile_dono);
        name.setText(selectedUserDetails.getName());
        uniqueKey = getUniqueString(currentUserDetails.getChatId(), selectedUserDetails.getChatId());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void request_blood(View view) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference().child(Constants.TOKENS);
        Query query = tokens.orderByKey().equalTo(selectedUserDetails.getMy_id());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Token token = dataSnapshot1.getValue(Token.class);
                    Data data2 = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, currentUserDetails.getName() + " requested for blood", "Blood required", selectedUserDetails.getMy_id(), "true", uniqueKey);
                    Sender sender = null;
                    if (token != null) {
                        sender = new Sender(data2, token.getToken());
                        apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {
                                if (response.code() == 200) {
                                    if (response.body() != null && response.body().success == 1) {
                                        requestblood_txt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.requestsent, 0, 0, 0);
                                        requestblood_txt.setText(R.string.requestSent);
                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Notifications").child(selectedUserDetails.getMy_id()).child(firebaseUser.getUid());
                                        databaseReference.setValue(firebaseUser.getUid());
                                        Toast.makeText(DonorPreview.this, "Request sent", Toast.LENGTH_SHORT).show();
                                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("My_requests").child(firebaseUser.getUid()).child(selectedUserDetails.getMy_id());
                                        databaseReference1.setValue(selectedUserDetails.getMy_id());
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
        name_of_recipient.setText("Calling " + selectedUserDetails.getName() + "...");
        Picasso.with(getApplicationContext())
                .load(selectedUserDetails.getImg_url())
                .into(profile_pic_recipient);
        reject.setOnClickListener(view -> {
        });
        dialog.show();
    }


}