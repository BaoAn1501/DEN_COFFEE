package com.nhom7.den_cafe.chat;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content_Type:application/json",
            "Authorization:key=AAAA4kdSNO0:APA91bFvKGHkIeTqs-W7FD7cQcLdOTPUWgTTva5fRX4_jEjf8aRzvfEeX4FP2IC325mkwO6yepYRWhaN09MBXEvyI5XYkf1_64bQK9gZenleE_lNZt5tMXzmp6ZKX27aFFFKLkkU_bhb"
    })
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
