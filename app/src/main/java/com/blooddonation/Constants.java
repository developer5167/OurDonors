package com.blooddonation;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.blooddonation.Notifications.Client;
import com.blooddonation.Notifications.Data;
import com.blooddonation.Notifications.MyResponse;
import com.blooddonation.Notifications.Sender;
import com.blooddonation.Notifications.Token;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Constants {
    public static final String ACCOUNTS = "accounts";
    public static final String TOKENS = "Tokens";
    public static final String IMAGE = "image/*";
    public static final String FROM_PROFILE = "fromProfile";
    public static final String USERS = "users";
    public static final String MY_AC = "MyAc";
    public static final String PROFILE_PHOTOS = "profile_photos";
    public static final String PLEASE_ACCEPT_TERMS_AND_CONDITIONS = "Please accept terms and Conditions";
    public static final String FIELD_S_CAN_T_BE_EMPTY = "Field's can't be empty";
    public static final String LOCATION_PERMISSION_NOT_GRANTED = "Location permission not granted";
    public static final String RESTART_THE_APP_IF_YOU_WANT_THE_FEATURE = "restart the app if you want the feature";
    public static final String UPDATE_DETAILS = "Update details";
    public static final String USER_ID = "userId";
    public static final String SHARED_PREFS = "SHARED_PREFS";
    private static APIService apiService;

    public static void checkLocation(Context context) {
        GpsTracker gpsTracker = new GpsTracker(context);
        if (!gpsTracker.canGetLocation()) {
            gpsTracker.showSettingsAlert();
        }
    }

    public static String getUniqueString(String userid, String from) {
        StringBuilder hashBuilder = new StringBuilder();
        if (userid.compareTo(from) > 0) {
            hashBuilder.append(userid).append(from);
        } else {
            hashBuilder.append(from).append(userid);
        }
        byte[] input = hashBuilder.toString().getBytes();
        return UUID.nameUUIDFromBytes(input).toString();
    }

    public static void send_notification(final Context context, final String user_id, final String user, final String msg, String uid, String uniqueKey) {
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference().child("Tokens");
        Query query = tokens.orderByKey().equalTo(user_id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Token token = dataSnapshot1.getValue(Token.class);
                    Data data = new Data(uid, R.mipmap.ic_launcher, msg, user, user_id, "false",uniqueKey);
                    Sender sender = null;
                    if (token != null) {
                        sender = new Sender(data, token.getToken());
                    }
                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {

                        }

                        @Override
                        public void onFailure(@NonNull Call<MyResponse> call, @NonNull Throwable t) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "" + databaseError, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
