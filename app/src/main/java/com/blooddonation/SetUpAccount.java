package com.blooddonation;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
;

import com.blooddonation.Models.LatLngModel;
import com.blooddonation.Models.MyData;
import com.blooddonation.Models.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetUpAccount extends BaseActivity {
    private EditText name, age;
    private boolean accepted = false;
    private Uri uri;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceMyAc;
    private StorageReference storageReference;
    private AutoCompleteTextView spinner2;
    private String blood_grp;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    private ProgressBar progressBar;
    private ActivityResultLauncher<Intent> someActivityResultLauncher;
    boolean picked_from_gal = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_account);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.main_color));
        launcher();
        boolean from_profile = getIntent().getBooleanExtra(Constants.FROM_PROFILE, false);
        databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.USERS);
        databaseReferenceMyAc = FirebaseDatabase.getInstance().getReference().child(Constants.MY_AC);
        storageReference = FirebaseStorage.getInstance().getReference().child(Constants.PROFILE_PHOTOS);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        String googleName = null;
        uri = Uri.parse("");
        if (firebaseUser != null) {
            googleName = firebaseUser.getDisplayName();
            uri = firebaseUser.getPhotoUrl();

        }
        CircleImageView profile_pic = findViewById(R.id.profile);
        TextView register = findViewById(R.id.register);
        progressBar = findViewById(R.id.progress_horizontal);
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        spinner2 = findViewById(R.id.bloodgrp);
        CheckBox checkbox = findViewById(R.id.checkbox);
        if (from_profile) {
            register.setText(Constants.UPDATE_DETAILS);
        }
        checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                accepted = true;
                Toast.makeText(SetUpAccount.this, "Accepted Terms and Conditions", Toast.LENGTH_SHORT).show();
            } else {
                accepted = false;
            }
        });
        Picasso.with(getApplicationContext())
                .load(uri)
                .into(profile_pic);
        name.setText(googleName);
    }

    public void register(View view) {
        if (view.isEnabled()) {
            blood_grp = spinner2.getText().toString();
            if (name.getText().toString().length() != 0 && age.getText().toString().length() != 0
                    && blood_grp.length() != 0) {
                if (accepted) {
                    view.setEnabled(false);
                    progressBar.setIndeterminate(true);
                    progressBar.setVisibility(View.VISIBLE);
                    Handler handler = new Handler();
                    handler.postDelayed(this::register, 2000);
                } else {
                    Toast.makeText(this, Constants.PLEASE_ACCEPT_TERMS_AND_CONDITIONS, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, Constants.FIELD_S_CAN_T_BE_EMPTY, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void register() {
        if (picked_from_gal) {
            storageReference.child(new File(uri.toString()).getName()).putFile(uri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    storageReference.child(new File(uri.toString()).getName()).getDownloadUrl().addOnSuccessListener(uri -> {
                        save_data(uri);
                        progressBar.setVisibility(View.INVISIBLE);
                        startActivity(new Intent(SetUpAccount.this, HomeScreen.class));
                        finish();
                    });
                }
            });
        } else {
            save_data(uri);
            startActivity(new Intent(SetUpAccount.this, HomeScreen.class));
            finish();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, Constants.LOCATION_PERMISSION_NOT_GRANTED + ", " + Constants.RESTART_THE_APP_IF_YOU_WANT_THE_FEATURE, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void save_data(Uri uri) {
        if (uri == null) {
            uri = Uri.parse("");
        }
        Random random = new Random();
        String id = RandomString.getAlphaNumericString(random.nextInt((100 - 20) + 1) + 20);
        LatLngModel latLngModel = LatLngModel.getInstance();
        DatabaseReference databaseReference_ = databaseReference.child(blood_grp).child(firebaseUser.getUid());
        MyData myAccountDetails = new MyData();
        myAccountDetails.setAge(age.getText().toString());
        myAccountDetails.setBg(blood_grp);
        myAccountDetails.setLocation(latLngModel.getAddress());
        myAccountDetails.setName(name.getText().toString());
        myAccountDetails.setImg_url(uri.toString());
        myAccountDetails.setMy_id(firebaseUser.getUid());
        myAccountDetails.setChatId(id);
        databaseReference_.setValue(myAccountDetails);
        DatabaseReference databaseReferenceMyAc_ref = databaseReferenceMyAc.child(firebaseUser.getUid());
        databaseReferenceMyAc_ref.child("bg").setValue(blood_grp);
        DatabaseReference databaseReferenceAct = FirebaseDatabase.getInstance().getReference().child(Constants.ACCOUNTS).child(firebaseUser.getUid());
        databaseReferenceAct.setValue(firebaseUser.getUid());
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> update_token(task.getResult()));
        MyData.setMyData(myAccountDetails);
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance();
        sharedPrefManager.setProfileData(SetUpAccount.this, MyData.getMyData());
    }

    private void update_token(String token) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.TOKENS);
        Token token1 = new Token(token);
        databaseReference.child(firebaseUser.getUid()).setValue(token1);

    }

    private void launcher() {
        someActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
            }
        });
    }

    public void terms(View view) {
        startActivity(new Intent(this, TermsConditions.class));
    }

    public void profile_pic_add(View view) {
        picked_from_gal = true;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(Constants.IMAGE);
        someActivityResultLauncher.launch(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

}
