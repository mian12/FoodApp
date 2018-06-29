package com.solution.alnahar.eatit.remote;


import com.solution.alnahar.eatit.fcmModel.MyResponse;
import com.solution.alnahar.eatit.fcmModel.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAn3NqhuE:APA91bE_M5O4zj-5PTU02K94NvjI-423FU7HvrIngcPZQkEUpO4huQXq1ZGQt--ueAmRkrNgxhLa6WP3b8s23wprQT-eSJCLzD2OL7oahXa_j5iawjdtgsdU9n3QMeWCo2eepC4hM3p3"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
