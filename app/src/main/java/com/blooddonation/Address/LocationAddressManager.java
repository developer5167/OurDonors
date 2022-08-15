package com.blooddonation.Address;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class LocationAddressManager {

    private static LocationAddressManager locationAddressManager;

    public static LocationAddressManager getLocationAddressManager() {
        return locationAddressManager;
    }

    public static void setLocationAddressManager(LocationAddressManager locationAddressManager) {
        LocationAddressManager.locationAddressManager = locationAddressManager;
    }



    @SerializedName("plus_code")
    @Expose
    private PlusCode plusCode;

    @SerializedName("results")
    @Expose
    private ArrayList<Result> results = null;

    public ArrayList<Result> getResults() {
        return results;
    }

    public PlusCode getPlusCode() {
        return plusCode;
    }

    public void setPlusCode(PlusCode plusCode) {
        this.plusCode = plusCode;
    }

    public void setResults(ArrayList<Result> results) {
        this.results = results;
    }
}
