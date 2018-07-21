package com.solution.alnahar.eatit.remote;

import com.solution.alnahar.eatit.mapsPlacesModel.MyPlacesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IGoogleService {
//
//    @GET
//    Call<String> getAdressName(@Url String url);
//
    @GET
    Call<MyPlacesResponse> getAdressName(@Url String url);

}
