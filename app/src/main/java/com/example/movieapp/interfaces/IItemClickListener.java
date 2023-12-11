package com.example.movieapp.interfaces;

import com.example.movieapp.object.DetailMovieResponse;

public interface IItemClickListener {

    void onItemClick(int movieID);

    void onSaveClick(DetailMovieResponse detailMovieResponse);

}
