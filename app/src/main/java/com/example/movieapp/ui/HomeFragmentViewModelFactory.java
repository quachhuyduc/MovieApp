package com.example.movieapp.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.movieapp.resporistories.HomeResporistory;

public class HomeFragmentViewModelFactory implements ViewModelProvider.Factory {

    private Application application;
    private HomeResporistory homeResporistory;

    public HomeFragmentViewModelFactory(Application application, HomeResporistory homeResporistory) {
        this.application = application;
        this.homeResporistory = homeResporistory;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new HomeFragmentViewModel(application,homeResporistory);
    }
}
