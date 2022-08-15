package com.blooddonation;


import com.blooddonation.Notifications.MyResponse;
import com.blooddonation.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
                    "Content-Type:application/json",
                    "Authorization:key=AAAARfXsA0E:APA91bGk6JiwhKVyMnBR5RhB1-D53KBtR13TWRhhZvolEAAzJwcO8WmZYvOMpx06jHYDwtDQ4U6EATHtsAdJcGxGb-f2qMsZBTocXR0XufNOeVBndaXJRH3sjn0oxAN4sTjS_sMFUxe4"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
