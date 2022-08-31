package com.blooddonation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.shts.android.storiesprogressview.StoriesProgressView;


public class PostsActivity extends BaseActivity implements StoriesProgressView.StoriesListener {
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference22;
    AccountDetails accountDetails;
    LinearLayout status_bar;
    private StoriesProgressView storiesProgressView;
    private ImageView image;
//    public final String[] resources = new String[]{
//            "https://firebasestorage.googleapis.com/v0/b/firebase-satya.appspot.com/o/images%2Fi00001.jpg?alt=media&token=460667e4-e084-4dc5-b873-eefa028cec32",
//            "https://firebasestorage.googleapis.com/v0/b/firebase-satya.appspot.com/o/images%2Fi00002.jpg?alt=media&token=e8e86192-eb5d-4e99-b1a8-f00debcdc016",
//            "https://firebasestorage.googleapis.com/v0/b/firebase-satya.appspot.com/o/images%2Fi00004.jpg?alt=media&token=af71cbf5-4be3-4f8a-8a2b-2994bce38377",
//            "https://firebasestorage.googleapis.com/v0/b/firebase-satya.appspot.com/o/images%2Fi00005.jpg?alt=media&token=7d179938-c419-44f4-b965-1993858d6e71",
//            "https://firebasestorage.googleapis.com/v0/b/firebase-satya.appspot.com/o/images%2Fi00006.jpg?alt=media&token=cdd14cf5-6ed0-4fb7-95f5-74618528a48b",
//            "https://firebasestorage.googleapis.com/v0/b/firebase-satya.appspot.com/o/images%2Fi00007.jpg?alt=media&token=98524820-6d7c-4fb4-89b1-65301e1d6053",
//            "https://firebasestorage.googleapis.com/v0/b/firebase-satya.appspot.com/o/images%2Fi00008.jpg?alt=media&token=7ef9ed49-3221-4d49-8fb4-2c79e5dab333",
//            "https://firebasestorage.googleapis.com/v0/b/firebase-satya.appspot.com/o/images%2Fi00009.jpg?alt=media&token=00d56a11-7a92-4998-a05a-e1dd77b02fe4",
//            "https://firebasestorage.googleapis.com/v0/b/firebase-satya.appspot.com/o/images%2Fi00010.jpg?alt=media&token=24f8f091-acb9-432a-ae0f-7e6227d18803",
//    };
    int counter = 0;
    long pressTime = 0L;
    long limit = 500L;
    ProgressBar progressBar;
    TextView no_posts_yet, time;
    CircleImageView my_profile;
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_posts);
        progressBar = findViewById(R.id.progressBar);
        status_bar = findViewById(R.id.status_bar);
        no_posts_yet = findViewById(R.id.no_posts_yet);
        storiesProgressView = findViewById(R.id.stories);
        time = findViewById(R.id.time);
        my_profile = findViewById(R.id.my_profile);
//        storiesProgressView.setStoriesCount(myStatusus.size());
        storiesProgressView.setStoryDuration(5000L);
        storiesProgressView.setStoriesListener(this);
        image = (ImageView) findViewById(R.id.image);

//        if (myStatusus.size() != 0) {
//            progressBar.setVisibility(View.VISIBLE);
//            Picasso.with(this).load(myStatusus.get(counter)).into(image);
//            String time1 = uploaded_dates.get(counter);
//            String res = getFormattedDate(this, Long.parseLong(time1));
//            time.setText(res);
//            new Handler().postDelayed(() -> {
//                progressBar.setVisibility(View.GONE);
//                storiesProgressView.startStories();
//            }, 1000);
//        } else {
//            no_posts_yet.setVisibility(View.VISIBLE);
//            status_bar.setVisibility(View.GONE);
//        }
        View reverse = findViewById(R.id.reverse);
        reverse.setOnClickListener(v -> storiesProgressView.reverse());
        reverse.setOnTouchListener(onTouchListener);
        View skip = findViewById(R.id.skip);
        skip.setOnClickListener(v -> storiesProgressView.skip());
        skip.setOnTouchListener(onTouchListener);

    }

    void setprofile_pic() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference22 = FirebaseDatabase.getInstance().getReference().child("MyAc").child(firebaseUser.getUid());
        databaseReference22.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    accountDetails = snapshot.getValue(AccountDetails.class);
                    Picasso.with(getApplicationContext())
                            .load(accountDetails.getImg_url())
                            .into(my_profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onNext() {
        int val = ++counter;
//        Picasso.with(this).load(myStatusus.get(val)).into(image);
//        String time1 = uploaded_dates.get(val);
//        String res = getFormattedDate(this, Long.parseLong(time1));
//        time.setText(res);
    }

    @Override
    public void onPrev() {
        if ((counter - 1) < 0) return;
        int val = --counter;
//        Picasso.with(this).load(myStatusus.get(val)).into(image);
//        String time1 = uploaded_dates.get(val);
//        String res = getFormattedDate(this, Long.parseLong(time1));
//        time.setText(res);
    }

    @Override
    public void onComplete() {
        finish();
    }

    public String getFormattedDate(Context context, long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "h:mm aa";
        final String dateTimeFormatString = "EEEE, MMMM d, h:mm aa";
        final long HOURS = 60 * 60 * 60;
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            return "Today " + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
            return "Yesterday " + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
            return DateFormat.format("MMMM dd yyyy, h:mm aa", smsTime).toString();
        }
    }



    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }

    public void add_post(View view) {
        startActivity(new Intent(this, AddPostActivity.class));
        finish();
    }

}
