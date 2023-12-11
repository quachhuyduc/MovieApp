package com.example.movieapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.movieapp.R;
import com.example.movieapp.api.MovieApi;
import com.example.movieapp.api.RetrofitClient;
import com.example.movieapp.object.DetailMovieResponse;
import com.example.movieapp.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AboutMovieFragment extends Fragment {


    private static final String TAG = "AboutMovieFragment";


    private MovieApi movieApi;

    public AboutMovieFragment() {

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_about_movie, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }


    private void initView(View view) {
        TextView textView_AboutMovie = view.findViewById(R.id.textView_AboutMovie);

        textView_AboutMovie.setNestedScrollingEnabled(false);
        Intent intent = getActivity().getIntent();
        int movieID =  intent.getExtras().getInt(Constants.MOVIE_ID_KEY,-1);
        if(movieID != -1){
            movieApi = RetrofitClient.getMovieApi();
            movieApi.getDetailMovie(movieID,Constants.API_KEY).enqueue(new Callback<DetailMovieResponse>() {
                @Override
                public void onResponse(Call<DetailMovieResponse> call, Response<DetailMovieResponse> response) {
                    if(response.isSuccessful()){
                        if(response.code() == 200){
                            textView_AboutMovie.setText(response.body().getOverview());
                        }
                    }
                }

                @Override
                public void onFailure(Call<DetailMovieResponse> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());

                }
            });
        }else {
            getActivity().finish();
        }
    }
}