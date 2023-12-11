package com.example.movieapp.utils;

import android.app.Activity;

import com.google.android.exoplayer2.ExoPlayer;

public class ExoPlayerFactory {

    private static ExoPlayer instances;

    public static ExoPlayer newSimpleInstance(Activity activity) {
        if (instances == null) {
            instances = new ExoPlayer.Builder(activity).build();
        }
        return instances;
    }
}
