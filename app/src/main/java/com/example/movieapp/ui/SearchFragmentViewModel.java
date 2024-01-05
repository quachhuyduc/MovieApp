package com.example.movieapp.ui;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.example.movieapp.activity.DetailActivity;
import com.example.movieapp.adapters.SearchMovieAdapter;
import com.example.movieapp.models.Genre;
import com.example.movieapp.object.GenresMovie;
import com.example.movieapp.object.MovieSearchResponse;
import com.example.movieapp.object.SearchPersonResponse;
import com.example.movieapp.resporistories.SearchResporistory;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragmentViewModel extends AndroidViewModel {

    public MutableLiveData<MovieSearchResponse> movieSearch = new MutableLiveData<>();
    public MutableLiveData<SearchPersonResponse> personSearch = new MutableLiveData<>();


    SearchResporistory searchResporistory;

    public SearchFragmentViewModel(@NonNull Application application, SearchResporistory searchResporistory) {
        super(application);
        this.searchResporistory = searchResporistory;
    }
    public void getListMovieSearch(String query){
        searchResporistory.getMovieSearch(query).enqueue(new Callback<MovieSearchResponse>() {
            @Override
            public void onResponse(Call<MovieSearchResponse> call, Response<MovieSearchResponse> response) {
                if (response.isSuccessful()) {
                    movieSearch.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<MovieSearchResponse> call, Throwable t) {
                movieSearch.postValue(null);
            }
        });
    }
    public void getListPersonSearch(String query){
        searchResporistory.getPersonSearch(query).enqueue(new Callback<SearchPersonResponse>() {
            @Override
            public void onResponse(Call<SearchPersonResponse> call, Response<SearchPersonResponse> response) {
                if (response.isSuccessful()) {
                    personSearch.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<SearchPersonResponse> call, Throwable t) {
                personSearch.postValue(null);
            }
        });
    }

}
