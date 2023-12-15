package com.example.movieapp.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.movieapp.resporistories.CastResporistory;
import com.example.movieapp.resporistories.ReviewResposistory;

public class ReviewFragmentViewModelFactory implements ViewModelProvider.Factory {
    private Application application;
    private ReviewResposistory reviewResposistory;

    public ReviewFragmentViewModelFactory(Application application, ReviewResposistory reviewResposistory) {
        this.application = application;
        this.reviewResposistory = reviewResposistory;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ReviewFragmentViewModel(application,reviewResposistory);
    }
}
