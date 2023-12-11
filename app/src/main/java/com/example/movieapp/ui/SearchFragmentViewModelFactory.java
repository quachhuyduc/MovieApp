package com.example.movieapp.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.movieapp.resporistories.SearchResporistory;

public class SearchFragmentViewModelFactory implements ViewModelProvider.Factory {

    private Application application;
    private SearchResporistory searchResporistory;

    public SearchFragmentViewModelFactory(Application application, SearchResporistory searchResporistory) {
        this.application = application;
        this.searchResporistory = searchResporistory;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new SearchFragmentViewModel(application,searchResporistory);
    }
}
