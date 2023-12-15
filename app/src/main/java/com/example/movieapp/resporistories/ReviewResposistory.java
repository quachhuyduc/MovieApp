package com.example.movieapp.resporistories;

import com.example.movieapp.api.RetrofitClient;
import com.example.movieapp.object.CastResponse;
import com.example.movieapp.object.ReviewsResponse;
import com.example.movieapp.utils.Constants;

import retrofit2.Call;

public class ReviewResposistory {
    public Call<ReviewsResponse> getReviewsMovie(int movie_id){
        return RetrofitClient.getMovieApi().getReviewsMovie(movie_id);
    }
}
