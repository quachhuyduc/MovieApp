package com.example.movieapp.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class BindingAdapter {

    @androidx.databinding.BindingAdapter("bindImageUrl")
    public static void bindImageUrl(ImageView imageView, String url) {
        String fullUrl = Constants.IMAGE_URL + url;
        Glide.with(imageView.getContext()).load(fullUrl).into(imageView);
    }
}
