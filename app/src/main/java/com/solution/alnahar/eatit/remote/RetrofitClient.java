package com.solution.alnahar.eatit.remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {

    public  static Retrofit retrofit=null;
    public  static  Retrofit getRetrofit(String baseUrl_Fcm)
    {
        if (retrofit==null)
        {
            retrofit=new Retrofit.Builder()
                    .baseUrl(baseUrl_Fcm)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return  retrofit;
    }

    public  static  Retrofit getGoogleClient(String baseUrl_device_position)
    {
        if (retrofit==null)
        {
            retrofit=new Retrofit.Builder()
                    .baseUrl(baseUrl_device_position)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return  retrofit;
    }

}
