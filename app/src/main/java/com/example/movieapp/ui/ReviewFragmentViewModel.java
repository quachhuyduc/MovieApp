package com.example.movieapp.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.movieapp.object.CastResponse;
import com.example.movieapp.object.ReviewsResponse;
import com.example.movieapp.resporistories.CastResporistory;
import com.example.movieapp.resporistories.ReviewResposistory;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewFragmentViewModel extends AndroidViewModel  {
    public MutableLiveData<ReviewsResponse> reviewsResponse = new MutableLiveData<>();

    ReviewResposistory reviewResposistory;
    public ReviewFragmentViewModel(@NonNull Application application, ReviewResposistory reviewResposistory) {
        super(application);
        this.reviewResposistory = reviewResposistory;
    }
    public void getListReview(int movie_id){

        reviewResposistory.getReviewsMovie(movie_id).enqueue(new Callback<ReviewsResponse>() {
            @Override
            public void onResponse(Call<ReviewsResponse> call, Response<ReviewsResponse> response) {
                if(response.isSuccessful()){
                    reviewsResponse.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<ReviewsResponse> call, Throwable t) {
                reviewsResponse.postValue(null);
            }
        });

    }
}
