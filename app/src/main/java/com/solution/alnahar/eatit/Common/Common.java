package com.solution.alnahar.eatit.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

import com.solution.alnahar.eatit.Model.User;
import com.solution.alnahar.eatit.remote.APIService;
import com.solution.alnahar.eatit.remote.IGoogleService;
import com.solution.alnahar.eatit.remote.RetrofitClient;

public class Common {

    public  static User currentUser;
    public  static  final String BASE_URL="https://fcm.googleapis.com/";
    public  static  final String GOOGLE_API_URL="https://maps.googleapis.com/";

    public static final String   DELETE="Delete";

    public static final String USER_KEY="User";
    public static final String PASSWORD_KEY="Password";

    public  static APIService getFcmService(){

        return RetrofitClient.getRetrofit(BASE_URL).create(APIService.class);
    }


    public  static IGoogleService getGoogleMapApi(){

        return RetrofitClient.getGoogleClient(GOOGLE_API_URL).create(IGoogleService.class);
    }

    public static   String convertCodeToStatus(String status)
    {
        String res="";

        switch (status)
        {

            case "0":
                res="Placed";
                break;
            case "1":
                res="On my way";
                break;
            case "2":
                res= "Shipped";
                break;
        }
        return res;
    }


    public  static  Boolean isConnectedToInternet(Context context)
    {
        ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager!=null) {
            NetworkInfo[]  info=connectivityManager.getAllNetworkInfo();
            if (info!=null)
            {
                for (int i=0; i<info.length; i++)
                {
                    if (info[i].getState()==NetworkInfo.State.CONNECTED){
                        return  true;
                    }
                }
            }
        }


        return  false;
    }

}
