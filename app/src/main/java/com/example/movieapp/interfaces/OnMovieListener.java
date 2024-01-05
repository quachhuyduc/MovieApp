package com.example.movieapp.interfaces;

import com.example.movieapp.models.NowPlayingMovie;
import com.example.movieapp.models.Result;

import java.util.List;

public interface OnMovieListener {

    void onMovieClick(int positon);

    void onCastClick(int positon);

    void onSaveClick(NowPlayingMovie nowPlayingMovie);



    void onChangeWishList(int position);


}
