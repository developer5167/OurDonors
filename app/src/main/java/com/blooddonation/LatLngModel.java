package com.blooddonation;

public class LatLngModel {
    private double latitude;
    private double longitude;
    private static LatLngModel latLngModel;
    private String pinCode;
    private String locality;
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public static LatLngModel getInstance() {
        if (latLngModel == null)
            latLngModel = new LatLngModel();
        return latLngModel;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }
}
