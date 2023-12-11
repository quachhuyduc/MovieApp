package com.example.movieapp.resporistories;

import com.example.movieapp.api.RetrofitClient;
import com.example.movieapp.models.Cast;
import com.example.movieapp.object.CastImagesResponse;
import com.example.movieapp.object.CastResponse;
import com.example.movieapp.utils.Constants;
import com.google.gson.JsonObject;

import retrofit2.Call;

public class CastResporistory {

    public Call<CastResponse> getCastMovie(int movie_id){
        return RetrofitClient.getMovieApi().getCastMovie(movie_id, Constants.API_KEY);
    }


}
