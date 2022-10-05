package com.blooddonation.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.blooddonation.Models.GetProfile;
import com.blooddonation.Models.AccountDetails;
import com.blooddonation.Models.Token;
import com.blooddonation.ProfileLoaded;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.blooddonation.MessageActivity;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyFirebaseMessaging extends FirebaseMessagingService implements ProfileLoaded {
    private String uniqueKey;
    private String user;
    private FirebaseUser firebaseUser;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String sented = remoteMessage.getData().get("sented");
        String user = remoteMessage.getData().get("user");
        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        String currentUser = preferences.getString("currentuser", "none");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        new GetProfile(user, this, null, null, null);
        new GetProfile(firebaseUser.getUid(), this, null, null, null);
        if (firebaseUser != null && sented.equals(firebaseUser.getUid())) {
            if (!currentUser.equals(user)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sendOreoNotification(remoteMessage);
                } else {
                    sendNotification(remoteMessage);
                }
                update();

            }
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            updateToken(token);
        }
    }

    private void updateToken(String refreshToken) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(refreshToken);
        reference.child(firebaseUser.getUid()).setValue(token);
    }

    private void sendOreoNotification(RemoteMessage remoteMessage) {
        PendingIntent pendingIntent;
        Uri defaultSound;
        user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        uniqueKey = remoteMessage.getData().get("uniqueKey");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent;
        Bundle bundle = new Bundle();
        intent = new Intent(this, MessageActivity.class);
        bundle.putString("uniqueKey", uniqueKey);
        bundle.putString("user", user);

        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT);
        defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoNotification oreoNotification = new OreoNotification(this);
        Notification.Builder builder = oreoNotification.getOreoNotification(title, body, pendingIntent,
                defaultSound, icon);

        int i = Math.max(j, 0);

        oreoNotification.getManager().notify(i, builder.build());

    }

    private void sendNotification(RemoteMessage remoteMessage) {
        PendingIntent pendingIntent;
        Uri defaultSound;
        user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        uniqueKey = remoteMessage.getData().get("uniqueKey");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent;
        Bundle bundle = new Bundle();
        intent = new Intent(this, MessageActivity.class);
        bundle.putString("uniqueKey", uniqueKey);
        bundle.putString("user", user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);
        defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);
        NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int i = 0;
        if (j > 0) {
            i = j;
        }

        noti.notify(i, builder.build());

    }

    private void update() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chats").child(uniqueKey);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("is_seen", "Delivered");
                    snapshot.getRef().updateChildren(hashMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void OnProfileLoaded(AccountDetails accountDetails, String user, CircleImageView profile_pic, ProgressBar progress_horizontal, RecyclerView.ViewHolder holder) {
        if (user.equals(firebaseUser.getUid())) {
            GetProfile.setMyDetails(accountDetails);
        } else {
            GetProfile.setUserDetails(accountDetails);
        }
    }
}
