package com.example.movieapp.adapters;

import com.example.movieapp.models.NowPlayingMovie;
import com.example.movieapp.models.Result;

public interface OnMovieListener {

    void onMovieClick(int positon);

    void onCastClick(int positon);

    void onSaveClick(NowPlayingMovie nowPlayingMovie);



    void onChangeWishList(int position);


}
