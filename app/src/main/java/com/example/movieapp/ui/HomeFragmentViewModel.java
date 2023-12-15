package com.example.movieapp.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;


import com.example.movieapp.object.ListNowPlayingResponse;
import com.example.movieapp.object.PopularResponse;
import com.example.movieapp.object.TopRatedResponse;
import com.example.movieapp.object.UpComingResponse;
import com.example.movieapp.resporistories.HomeResporistory;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragmentViewModel extends AndroidViewModel {

    public MutableLiveData<ListNowPlayingResponse> nowPlaying = new MutableLiveData<>();
    public MutableLiveData<PopularResponse> popular = new MutableLiveData<>();
    public MutableLiveData<UpComingResponse> upComing = new MutableLiveData<>();
    public MutableLiveData<TopRatedResponse> topRated = new MutableLiveData<>();


    HomeResporistory homeResporistory;



    public HomeFragmentViewModel(@NonNull Application application, HomeResporistory homeResporistory) {
        super(application);
        this.homeResporistory = homeResporistory;
    }

    public void getListNowPlaying(int page) {
        homeResporistory.getListNowPlaying(page).enqueue(new Callback<ListNowPlayingResponse>() {
            @Override
            public void onResponse(Call<ListNowPlayingResponse> call, Response<ListNowPlayingResponse> response) {
                if (response.isSuccessful()) {
                    nowPlaying.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<ListNowPlayingResponse> call, Throwable t) {
                nowPlaying.postValue(null);
            }
        });
    }
    public void getPopularMovie(int page) {
        homeResporistory.getPopularMovie(page).enqueue(new Callback<PopularResponse>() {
            @Override
            public void onResponse(Call<PopularResponse> call, Response<PopularResponse> response) {
                if (response.isSuccessful()) {
                    popular.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<PopularResponse> call, Throwable t) {
                popular.postValue(null);
            }
        });
    }
    public void getUpComingMovie(int page) {
        homeResporistory.getUpcomingMovie(page).enqueue(new Callback<UpComingResponse>() {
            @Override
            public void onResponse(Call<UpComingResponse> call, Response<UpComingResponse> response) {
                if (response.isSuccessful()) {
                    upComing.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<UpComingResponse> call, Throwable t) {
                upComing.postValue(null);
            }
        });
    }
    public void getTopRatedMovie(int page) {
        homeResporistory.getTopRatedMovie(page).enqueue(new Callback<TopRatedResponse>() {
            @Override
            public void onResponse(Call<TopRatedResponse> call, Response<TopRatedResponse> response) {
                if (response.isSuccessful()) {
                    topRated.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<TopRatedResponse> call, Throwable t) {
                topRated.postValue(null);
            }
        });
    }


}
