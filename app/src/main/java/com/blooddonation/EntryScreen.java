package com.blooddonation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.blooddonation.Address.LocationAddressManager;
import com.blooddonation.Address.LocationTrack;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EntryScreen extends AppCompatActivity {
    private final int REQUEST_LOCATION = 100;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        permissions();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void permissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_PHONE_STATE,
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, REQUEST_LOCATION);
        } else {
            getAddress();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults[4] != PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                Toast.makeText(EntryScreen.this, "Please grant permissions " + ("\ud83d\ude01"), Toast.LENGTH_LONG).show();
                finishAffinity();
            } else {
                getAddress();
            }
        }

    }

    private void getAddress() {
        double longitude = 0;
        double latitude = 0;
        LocationTrack locationTrack = new LocationTrack(EntryScreen.this);
        try {
            locationTrack.getLocation();
            if (locationTrack.canGetLocation()) {
                longitude = locationTrack.getLongitude();
                latitude = locationTrack.getLatitude();
                fetchLocation(latitude, longitude);
            } else {
                locationTrack.showSettingsAlert();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Please allow location permissions to access the app", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    void fetchLocation(double latitude, double longitude) {
        LatLngModel latLngModel = LatLngModel.getInstance();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        String BASE_URL = "https://maps.googleapis.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiClient apiClient = retrofit.create(ApiClient.class);
        latLngModel.setLatitude(latitude);
        latLngModel.setLongitude(longitude);
        Call<LocationAddressManager> responseCall = apiClient.getLoaction(latitude + "," + longitude, "AIzaSyDov9RDe4mqEZngeYztb2XFMUvov6eXOcM");
        responseCall.enqueue(new Callback<LocationAddressManager>() {
            @Override
            public void onResponse(@NonNull Call<LocationAddressManager> call, @NonNull Response<LocationAddressManager> response) {
                if (response.isSuccessful()) {
                    LocationAddressManager.setLocationAddressManager(response.body());
                    latLngModel.setAddress(LocationAddressManager.getLocationAddressManager().getResults().get(1).getFormattedAddress());
                    latLngModel.setPinCode(extractDigits(LocationAddressManager.getLocationAddressManager().getResults().get(1).getFormattedAddress()));
                    startActivity(new Intent(EntryScreen.this,MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(EntryScreen.this, "response  " + response.errorBody(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LocationAddressManager> call, @NonNull Throwable t) {
                System.out.println("DCPDSPPC    "+t.getMessage());
                Toast.makeText(EntryScreen.this, "response>>>>>>>  " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static String extractDigits(final String in) {
        final Pattern p = Pattern.compile("(\\d{6})");
        final Matcher m = p.matcher(in);
        if (m.find()) {
            return m.group(0);
        }
        return "";
    }
}
