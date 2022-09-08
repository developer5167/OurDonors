package com.blooddonation;

import static com.blooddonation.Models.App.CHANNEL_ID;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;

import com.blooddonation.Models.LatLngModel;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import developer.semojis.Helper.EmojiconEditText;
import developer.semojis.actions.EmojIconActions;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class AddPostActivity extends BaseActivity {
    public static String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/ourdonors";

    RelativeLayout root_view, root_lay, options;
    ImageView emoji_btn;
    EmojiconEditText emojiedit_text;
    LinearLayout sendBtn;
    int i = 0;
    File file2;
    ArrayList<PendingIntent> intentArray = new ArrayList<PendingIntent>();

    final int PROGRESS_MAX = 100;
    RelativeLayout dimmlay;
    int color_code = 0;
    String[] colors = {"#5b39c6", "#808080", "#141414", "#ba160c", "#373f1a", "#8657c5"};
    StorageReference storageReference;
    NotificationManagerCompat notificationManagerCompat;
    DatabaseReference reference;
    DatabaseReference posts_reference;
    FirebaseUser firebaseUser;
    SpinKitView progress_bar;
    private GpsTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storageReference = FirebaseStorage.getInstance().getReference().child("MyPosts");
        posts_reference = FirebaseDatabase.getInstance().getReference().child("all_posts");
        setContentView(R.layout.activity_add_post);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference().child("MY_STATUS").child(firebaseUser.getUid());
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        emoji_btn = findViewById(R.id.emoji_btn);
        dimmlay = findViewById(R.id.dimmlay);
        progress_bar = findViewById(R.id.progress_bar);
        options = findViewById(R.id.options);
        sendBtn = findViewById(R.id.sendBtn);
        root_lay = findViewById(R.id.root_lay);
        emojiedit_text = findViewById(R.id.emojiedit_text);
        root_view = findViewById(R.id.root_view);
        EmojIconActions emojIcon = new EmojIconActions(this, root_lay, emojiedit_text, emoji_btn);
        emojIcon.ShowEmojIcon();
        notificationManagerCompat = NotificationManagerCompat.from(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.main_color));
        }
    }

    public String getLocation() {
        List<Address> addresses = null;
        String city = null;
        gpsTracker = new GpsTracker(this);
        if (gpsTracker.canGetLocation()) {
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            Geocoder geocoder = new Geocoder(this);
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                city = addresses.get(0).getSubAdminArea();
                System.out.println("FFFFFFFF   " + latitude + "  KKKKKK  " + longitude + "RRRRRRR  " + city);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            gpsTracker.showSettingsAlert();
        }
        return city;
    }

    public void font_style(View view) {
        if (i == 0) {
            Typeface face = ResourcesCompat.getFont(this, R.font.f1);
            emojiedit_text.setTypeface(face);
            i++;
        } else if (i == 1) {
            Typeface face = ResourcesCompat.getFont(this, R.font.f2);
            emojiedit_text.setTypeface(face);
            i++;
        } else if (i == 2) {
            Typeface face = ResourcesCompat.getFont(this, R.font.f3);
            emojiedit_text.setTypeface(face);
            i++;
        } else if (i == 3) {
            Typeface face = ResourcesCompat.getFont(this, R.font.f4);
            emojiedit_text.setTypeface(face);
            i = 0;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        sendBtn.setVisibility(View.VISIBLE);
        options.setVisibility(View.VISIBLE);
    }

    public void background_color(View view) {
        if (color_code == 0) {
            root_lay.setBackgroundColor(Color.parseColor(colors[0]));
            setnavColor(Color.parseColor(colors[0]));
            color_code++;
        } else if (color_code == 1) {
            root_lay.setBackgroundColor(Color.parseColor(colors[1]));
            setnavColor(Color.parseColor(colors[1]));
            color_code++;
        } else if (color_code == 2) {
            root_lay.setBackgroundColor(Color.parseColor(colors[2]));
            setnavColor(Color.parseColor(colors[2]));
            color_code++;
        } else if (color_code == 3) {
            root_lay.setBackgroundColor(Color.parseColor(colors[3]));
            setnavColor(Color.parseColor(colors[3]));
            color_code++;
        } else if (color_code == 4) {
            root_lay.setBackgroundColor(Color.parseColor(colors[4]));
            setnavColor(Color.parseColor(colors[4]));
            color_code++;
        } else if (color_code == 5) {
            root_lay.setBackgroundColor(Color.parseColor(colors[5]));
            setnavColor(Color.parseColor(colors[5]));
            color_code = 0;
        }
    }

    private void setnavColor(int parseColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(parseColor);
        }
    }

    static Bitmap b;

    public void send_post(View view) {
        Uri photoURI;
        dimmlay.setVisibility(View.VISIBLE);
        progress_bar.setVisibility(View.VISIBLE);
        emojiedit_text.clearFocus();
        sendBtn.setVisibility(View.INVISIBLE);
        options.setVisibility(View.INVISIBLE);
        b = Screenshot.takescreenshotOfRootView(emojiedit_text);
        File f;
        f = screenshot_2(b);
        if (Build.VERSION.SDK_INT > 22) {
            photoURI = FileProvider.getUriForFile(this, getPackageName() + ".provider", f);
        } else {
            photoURI = Uri.fromFile(f);
        }
        Upload(photoURI);
    }
    private File screenshot_2(Bitmap b) {
        try {
            String dir_path = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/.Ourdonors";
            File file = new File(dir_path);
            file.mkdir();
            File imageurl = new File(dir_path + "/." + System.currentTimeMillis() + ".png");
            if (!imageurl.exists()) {
                imageurl.createNewFile();
            }
            FileOutputStream outputStream = new FileOutputStream(imageurl);
            b.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            return imageurl;
        } catch (IOException io) {
            io.printStackTrace();
        }
        return null;
    }


    private void Upload(Uri uri) {
        send_notification_Channel();
        storageReference.child(new File(Objects.requireNonNull(uri.getPath())).getName()).putFile(uri).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                storageReference.child(new File(uri.toString()).getName()).getDownloadUrl().addOnSuccessListener(uri1 ->
                        scheduleAlarm(uri1.toString()));
            }
        }).addOnProgressListener(snapshot -> {
            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
            if ((int) progress != PROGRESS_MAX) {
                builder.setProgress(PROGRESS_MAX, (int) progress, true);
                notificationManagerCompat.notify(2, builder.build());
            } else {
                builder.setOnlyAlertOnce(true);
                builder.setOngoing(false);
                builder.setContentText("Upload Finished")
                        .setProgress(0, 0, false);
                Intent resultIntent = new Intent(this, OtherPostsActivity.class);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addNextIntentWithParentStack(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(resultPendingIntent);
                notificationManagerCompat.notify(2, builder.build());
                progress_bar.setVisibility(View.GONE);
                dimmlay.setVisibility(View.GONE);
                startActivity(new Intent(AddPostActivity.this, HomeScreen.class));
                finish();
            }
        }).addOnPausedListener(snapshot -> {
        });
    }

    NotificationCompat.Builder builder;

    void send_notification_Channel() {
        builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Uploading post")
                .setOngoing(true)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setProgress(PROGRESS_MAX, 0, true);
        notificationManagerCompat.notify(2, builder.build());
    }


    public static class Screenshot {
        static Bitmap takescreenshot(View v) {
            v.setDrawingCacheEnabled(true);
            v.buildDrawingCache(true);
            Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
            v.setDrawingCacheEnabled(false);
            return b;
        }

        static Bitmap takescreenshotOfRootView(View v) {
            return takescreenshot(v.getRootView());
        }
    }

    public void scheduleAlarm(String s) {
        LatLngModel latLngModel = LatLngModel.getInstance();
        long current = System.currentTimeMillis();
        long time = System.currentTimeMillis() + 60000;
        String date = getDate(time, "dd/MM/yyyy");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("postUrl", s);
        hashMap.put("owner", firebaseUser.getUid());
        hashMap.put("date", date);
        hashMap.put("current", Long.toString(current));
        HashMap<String, Object> hashMap2 = new HashMap<>();
        hashMap2.put("postUrl", s);
        hashMap2.put("owner", firebaseUser.getUid());
        hashMap2.put("date", time);
        hashMap2.put("current", Long.toString(current));
        posts_reference.child(latLngModel.getPinCode()).push().setValue(hashMap2);
        reference.child(String.valueOf(time)).setValue(hashMap);
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

}
