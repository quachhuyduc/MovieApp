package com.example.movieapp.resporistories;

import com.example.movieapp.adapters.SearchPersonAdapter;
import com.example.movieapp.api.RetrofitClient;
import com.example.movieapp.object.GenresMovie;
import com.example.movieapp.object.MovieSearchResponse;
import com.example.movieapp.object.SearchPersonResponse;
import com.example.movieapp.utils.Constants;

import retrofit2.Call;

public class SearchResporistory {

   public Call<MovieSearchResponse> getMovieSearch(String query){
        return RetrofitClient.getMovieApi().searchMovie(Constants.API_KEY,query);
    }
    public Call<SearchPersonResponse> getPersonSearch(String query){
        return RetrofitClient.getMovieApi().searchPerson(query);
    }

}
