package com.blooddonation;

;

import android.os.Bundle;
import android.view.View;

public class MyPostActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);
    }

    public void back(View view) {
        onBackPressed();
    }
}
