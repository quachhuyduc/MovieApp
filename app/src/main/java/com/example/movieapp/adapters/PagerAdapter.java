package com.example.movieapp.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.movieapp.fragment.NowPlayingFragment;
import com.example.movieapp.fragment.PopularFragment;
import com.example.movieapp.fragment.TopRateFragment;


public class PagerAdapter extends FragmentStatePagerAdapter {
    public PagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment  =new NowPlayingFragment();
                break;
            case 1:
                fragment = new TopRateFragment();
                break;
            case 2:
                fragment = new PopularFragment();
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
                title = "Now Playing";
                break;
            case 1:
                title = "Top rated";
                break;
            case 2:
                title = "Popular";
                break;

        }
        return title;
    }
}
