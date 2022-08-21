package com.blooddonation;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

    public class SharedPrefManager {
    private static SharedPrefManager sharedPrefManager;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;

    public static SharedPrefManager getInstance() {
        if (sharedPrefManager == null)
            sharedPrefManager = new SharedPrefManager();
        return sharedPrefManager;
    }

    protected void setProfileData(Context context, MyData myAccountDetails) {
        sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();
        String json = gson.toJson(myAccountDetails);
        editor.putString("myAccountDetails", json);
        editor.apply();

    }

    protected MyData getProfileData(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        gson = new Gson();
        String json = sharedPreferences.getString("myAccountDetails", "");
        return gson.fromJson(json, MyData.class);
    }
}
