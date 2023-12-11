package com.example.movieapp.resporistories;

import com.example.movieapp.api.RetrofitClient;

import com.example.movieapp.object.ListNowPlayingResponse;
import com.example.movieapp.utils.Constants;

import retrofit2.Call;

public class HomeResporistory {


    public Call<ListNowPlayingResponse> getListNowPlaying(int page) {
        return RetrofitClient.getMovieApi().getNowPlayingMovie(page);
    }

}
