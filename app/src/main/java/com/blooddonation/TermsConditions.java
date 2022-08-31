package com.blooddonation;

import android.os.Bundle;
import android.view.View;

;

public class TermsConditions extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_conditions);
    }

    public void back(View view) {
        onBackPressed();
    }
}
