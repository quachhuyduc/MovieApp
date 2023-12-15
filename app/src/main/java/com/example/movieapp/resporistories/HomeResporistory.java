package com.example.movieapp.resporistories;

import com.example.movieapp.api.RetrofitClient;

import com.example.movieapp.object.ListNowPlayingResponse;
import com.example.movieapp.object.PopularResponse;
import com.example.movieapp.object.TopRatedResponse;
import com.example.movieapp.object.UpComingResponse;
import com.example.movieapp.utils.Constants;

import retrofit2.Call;

public class HomeResporistory {


    public Call<ListNowPlayingResponse> getListNowPlaying(int page) {
        return RetrofitClient.getMovieApi().getNowPlayingMovie(page);
    }
    public Call<PopularResponse> getPopularMovie(int page) {
        return RetrofitClient.getMovieApi().getPopularMovie(page);
    }
    public Call<UpComingResponse> getUpcomingMovie(int page) {
        return RetrofitClient.getMovieApi().getUpcomingMovie(page);
    }
    public Call<TopRatedResponse> getTopRatedMovie(int page) {
        return RetrofitClient.getMovieApi().getTopRatedMovie(page);
    }

}
