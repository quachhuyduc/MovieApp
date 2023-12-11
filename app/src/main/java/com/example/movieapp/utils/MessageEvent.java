package com.example.movieapp.utils;

public class MessageEvent {

    public String mMovieName;

    public MessageEvent(String movieName) {
        mMovieName = movieName;
    }

    public String getMessage() {
        return mMovieName;
    }
}
