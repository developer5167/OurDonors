package com.blooddonation;

import com.blooddonation.Address.LocationAddressManager;
import com.blooddonation.Address.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiClient {
    @GET("maps/api/geocode/json")
    Call<LocationAddressManager> getLoaction(@Query("latlng") String latlong, @Query("key") String key);
}
