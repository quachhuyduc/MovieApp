package com.example.movieapp.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.movieapp.fragment.AboutMovieFragment;
import com.example.movieapp.fragment.CastFragment;
import com.example.movieapp.fragment.ReviewsFragment;


public class PagerAdapterDetail extends FragmentStatePagerAdapter {
    public PagerAdapterDetail(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment  =new AboutMovieFragment();
                break;
            case 1:
                fragment = new ReviewsFragment();
                break;
            case 2:
                fragment = new CastFragment();
                break;

        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position){
            case 0:
                title = "About Movie";
                break;
            case 1:
                title = "Reviews";
                break;
            case 2:
                title = "Cast";
                break;

        }
        return title;
    }
}
