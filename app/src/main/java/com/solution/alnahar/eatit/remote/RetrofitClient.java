package com.solution.alnahar.eatit.remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {

    public  static Retrofit retrofitGson=null;
    public  static Retrofit retrofitScalar=null;

    public  static  Retrofit getRetrofit(String baseUrl_Fcm)
    {
        if (retrofitGson==null)
        {
            retrofitGson=new Retrofit.Builder()
                    .baseUrl(baseUrl_Fcm)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return  retrofitGson;
    }

    public  static  Retrofit getGoogleClient(String baseUrl_device_position)
    {
        if (retrofitScalar==null)
        {
            retrofitScalar=new Retrofit.Builder()
                    .baseUrl(baseUrl_device_position)
//                    .addConverterFactory(GsonConverterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return  retrofitScalar;
    }

}
