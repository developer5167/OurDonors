package com.blooddonation;

import static com.blooddonation.Constants.ENCRYPTED_KEY_1;
import static com.blooddonation.Constants.ENCRYPTED_KEY_2;
import static com.blooddonation.Constants.ENCRYPTED_KEY_3;
import static com.blooddonation.Constants.BASE_URL;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.blooddonation.Address.LocationAddressManager;
import com.blooddonation.Address.LocationTrack;
import com.blooddonation.Models.LatLngModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BaseActivity extends AppCompatActivity {
    private static Retrofit retrofit;
    private Dialog dialog;

    private BroadcastReceiver checkNetWorkConnection = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            if (checkNetWork()) {
                dismissDialog();
                permissions();
            } else {
                showDialog();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new Dialog(this, R.style.full_screen_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.network_dialog);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(checkNetWorkConnection, intentFilter);
        dialog.findViewById(R.id.close).setOnClickListener(v -> finishAffinity());

    }

    private boolean checkNetWork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public void getAddress(boolean fromEntryScreen) {
        double longitude = 0;
        double latitude = 0;
        LocationTrack locationTrack = new LocationTrack(BaseActivity.this);
        try {
            locationTrack.getLocation();
            if (locationTrack.canGetLocation()) {
                longitude = locationTrack.getLongitude();
                latitude = locationTrack.getLatitude();
                if (longitude != 0 && latitude != 0) {
                    fetchLocationDetails(latitude, longitude,fromEntryScreen);
                } else {
                    Toast.makeText(getApplicationContext(), "failed to fetch location please try again later", Toast.LENGTH_SHORT).show();
                }
            } else {
                locationTrack.showSettingsAlert();
            }
        } catch (Exception e) {
            Toast.makeText(BaseActivity.this, "Please allow location permissions to access the app", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(checkNetWorkConnection);
    }

    private void showDialog() {
        dialog.show();
    }

    private void dismissDialog() {
        dialog.dismiss();
    }

    void fetchLocationDetails(double latitude, double longitude,boolean fromEntryScreen) {
        LatLngModel latLngModel = LatLngModel.getInstance();
        ApiClient apiClient = getRetrofit().create(ApiClient.class);
        latLngModel.setLatitude(latitude);
        latLngModel.setLongitude(longitude);
        Call<LocationAddressManager> responseCall = apiClient.getLoaction(latitude + "," + longitude, String.format("%s%s%s", ENCRYPTED_KEY_1, ENCRYPTED_KEY_2, ENCRYPTED_KEY_3));
        responseCall.enqueue(new Callback<LocationAddressManager>() {
            @Override
            public void onResponse(@NonNull Call<LocationAddressManager> call, @NonNull Response<LocationAddressManager> response) {
                if (response.isSuccessful()) {
                    LocationAddressManager.setLocationAddressManager(response.body());
                    latLngModel.setAddress(LocationAddressManager.getLocationAddressManager().getResults().get(1).getFormattedAddress());
                    latLngModel.setPinCode(extractDigits(LocationAddressManager.getLocationAddressManager().getResults().get(1).getFormattedAddress()));
                    if (fromEntryScreen){
                        startActivity(new Intent(BaseActivity.this, MainActivity.class));
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<LocationAddressManager> call, @NonNull Throwable t) {
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

    private Retrofit getRetrofit() {
        if (retrofit != null) {
            return retrofit;
        } else {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
            return retrofit = new Retrofit.Builder().baseUrl(BASE_URL).client(client).addConverterFactory(GsonConverterFactory.create()).build();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean permissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}
