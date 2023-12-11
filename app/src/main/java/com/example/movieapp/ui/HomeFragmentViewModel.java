package com.example.movieapp.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;


import com.example.movieapp.object.ListNowPlayingResponse;
import com.example.movieapp.resporistories.HomeResporistory;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragmentViewModel extends AndroidViewModel {

    public MutableLiveData<ListNowPlayingResponse> nowPlaying = new MutableLiveData<>();


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


}
