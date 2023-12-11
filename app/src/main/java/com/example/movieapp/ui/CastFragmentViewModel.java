package com.example.movieapp.ui;

import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.movieapp.object.CastResponse;
import com.example.movieapp.resporistories.CastResporistory;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CastFragmentViewModel extends AndroidViewModel {

    public MutableLiveData<CastResponse> castResponse = new MutableLiveData<>();

    CastResporistory castResporistory;

    public CastFragmentViewModel(@NonNull Application application, CastResporistory castResporistory) {
        super(application);
        this.castResporistory = castResporistory;
    }

    public void getListCast(int movie_id){

        castResporistory.getCastMovie(movie_id).enqueue(new Callback<CastResponse>() {
           @Override
           public void onResponse(Call<CastResponse> call, Response<CastResponse> response) {
               if(response.isSuccessful()){
                   castResponse.postValue(response.body());
               }
           }

            @Override
           public void onFailure(Call<CastResponse> call, Throwable t) {
                  castResponse.postValue(null);
            }
      });

    }

}
