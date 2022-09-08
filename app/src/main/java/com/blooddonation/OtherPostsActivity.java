package com.blooddonation;


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

import com.blooddonation.Models.AccountDetails;
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

public class OtherPostsActivity extends BaseActivity implements StoriesProgressView.StoriesListener {
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference22;
    AccountDetails accountDetails;
    LinearLayout status_bar;
    private StoriesProgressView storiesProgressView;
    private ImageView image;
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
//        storiesProgressView.setStoriesCount(otherStatus.size());
        storiesProgressView.setStoryDuration(5000L);
        storiesProgressView.setStoriesListener(this);
        image = (ImageView) findViewById(R.id.image);

//        if (otherStatus.size() != 0) {
//            progressBar.setVisibility(View.VISIBLE);
//            Picasso.with(this).load(otherStatus.get(counter)).into(image);
//            String time1 = otherUploadedDates.get(counter);
//            String res = getFormattedDate(Long.parseLong(time1));
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
//        Picasso.with(this).load(otherStatus.get(val)).into(image);
//        String time1 = otherUploadedDates.get(val);
//        String res = getFormattedDate(Long.parseLong(time1));
//        time.setText(res);
    }

    @Override
    public void onPrev() {
        if ((counter - 1) < 0) return;
        int val = --counter;
//        Picasso.with(this).load(otherStatus.get(val)).into(image);
//        String time1 = otherUploadedDates.get(val);
//        String res = getFormattedDate(Long.parseLong(time1));
//        time.setText(res);
    }

    @Override
    public void onComplete() {
        finish();
    }

    public String getFormattedDate(long smsTimeInMilis) {
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
