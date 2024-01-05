package com.example.movieapp.utils;

import android.graphics.Movie;

import com.example.movieapp.models.NowPlayingMovie;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseHelper {
    private DatabaseReference databaseReference;

    public FirebaseHelper() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void addMovieToWatchList(NowPlayingMovie nowPlayingMovie, String userId) {
        databaseReference.child("users").child(userId).child("watchList").child(String.valueOf(nowPlayingMovie.getId())).setValue(nowPlayingMovie);
    }

    public void removeMovieFromWatchList(String movieId, String userId) {
        databaseReference.child("users").child(userId).child("watchList").child(movieId).removeValue();
    }
    public void getWatchList(String userId, ValueEventListener valueEventListener) {
        // Đường dẫn tới danh sách phim của người dùng trong Firebase
        DatabaseReference watchListRef = databaseReference.child("users").child(userId).child("watchList");

        // Thực hiện lắng nghe sự kiện khi dữ liệu thay đổi
        watchListRef.addValueEventListener(valueEventListener);
    }
}

