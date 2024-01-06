package com.example.movieapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.R;
import com.example.movieapp.activity.DetailActivity;
import com.example.movieapp.adapters.MovieAdapter2;
import com.example.movieapp.interfaces.OnMovieListener;
import com.example.movieapp.models.NowPlayingMovie;
import com.example.movieapp.resporistories.HomeResporistory;
import com.example.movieapp.ui.HomeFragmentViewModel;
import com.example.movieapp.ui.HomeFragmentViewModelFactory;
import com.example.movieapp.utils.Constants;
import com.google.firebase.database.DatabaseReference;

import java.util.List;


public class NowPlayingFragment extends Fragment {

    private List<NowPlayingMovie> listDataNow;
    private HomeFragmentViewModel homeFragmentViewModelNow;
    private MovieAdapter2 mMovieAdapterNow;

    private DatabaseReference watchlistRef;
    private OnMovieListener onMovieListener = new OnMovieListener() {
        @Override
        public void onMovieClick(int positon) {
            onClickGoToDetail(positon);
        }

        @Override
        public void onCastClick(int positon) {

        }

        @Override
        public void onSaveClick(NowPlayingMovie nowPlayingMovie) {

        }


        @Override
        public void onChangeWishList(int position, NowPlayingMovie movie) {
            homeFragmentViewModelNow.updateMovieWishList(position, movie);
        }
    };

    public NowPlayingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_now_playing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModel();
        initView(view);
    }

    private void initView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewNowPlaying);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        mMovieAdapterNow = new MovieAdapter2(getActivity(), onMovieListener);
        recyclerView.setAdapter(mMovieAdapterNow);

    }

    private void initViewModel() {
        HomeResporistory homeResporistoryNow = new HomeResporistory();
        HomeFragmentViewModelFactory homeFragmentViewModelFactoryNow = new HomeFragmentViewModelFactory(getActivity().getApplication(), homeResporistoryNow);
        homeFragmentViewModelNow = new ViewModelProvider(this, homeFragmentViewModelFactoryNow).get(HomeFragmentViewModel.class);
        homeFragmentViewModelNow.getListNowPlaying(2);
        homeFragmentViewModelNow.nowListLiveData.observe(getViewLifecycleOwner(), nowPlayingMovies -> {
            mMovieAdapterNow.setData2(nowPlayingMovies);
        });
    }

    private void onClickGoToDetail(int positon) {
        int movieId = listDataNow.get(positon).getId();
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.MOVIE_ID_KEY, movieId);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}


