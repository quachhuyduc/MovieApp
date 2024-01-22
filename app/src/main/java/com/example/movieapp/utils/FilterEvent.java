package com.example.movieapp.utils;

public class FilterEvent {
    private int genreId;

    public FilterEvent(int genreId) {
        this.genreId = genreId;
    }

    public int getGenreId() {
        return genreId;
    }
}
