package com.example.movieapp.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.movieapp.resporistories.CastResporistory;

public class CastFragmentViewModelFactory implements ViewModelProvider.Factory {

    private Application application;
    private CastResporistory castResporistory;

    public CastFragmentViewModelFactory(Application application, CastResporistory castResporistory) {
        this.application = application;
        this.castResporistory = castResporistory;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new CastFragmentViewModel(application,castResporistory);
    }
}
