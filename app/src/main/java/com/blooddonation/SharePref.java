package com.blooddonation;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePref {

    public static final String OUR_DONORS = "OUR_DONORS";
    public static final String CITY = "CITY";

    public SharePref() {
    }

    public void setCity(Context context, String city) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(OUR_DONORS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CITY, city);
        editor.commit();
    }

    public String getCity(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(OUR_DONORS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(CITY, "");
    }
}
