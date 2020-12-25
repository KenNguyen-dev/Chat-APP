package com.example.chatapp.Fragments;


import com.example.chatapp.Notifications.MyResponse;
import com.example.chatapp.Notifications.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:appliication/json",
                    "Authorization:key-\tAAAADQH8Bzc:APA91bFbVwAf1o7nHpvJzAnatpLf4GPizy_bn9Qheb7yKKuK9QWaSGZF_SziFva4yN4nnyAwZTKw5QNJyOFYVc0TdXcnSFSC_Q-oCMjDRy3wd6l9K2ws4yxRQVJmPLGMMF7Hi6GyLKYQ"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotifications(@Body Sender body);
}
